package com.jiran.app.data;
import android.os.Handler;
import android.os.Looper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jiran.app.BuildConfig;
import com.jiran.app.domain.auth.UmEmailPolicy;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.json.JSONObject;
/** Sends ID tokens only to the configured endpoint;
never logs tokens, addresses or bodies. */
public final class WorkerListingGateway implements ListingGateway {
    private static final ExecutorService IO = Executors.newSingleThreadExecutor();
    private final Handler main = new Handler(Looper.getMainLooper());
    private final String ownerUid;
    public WorkerListingGateway() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        ownerUid = user == null ? "" : user.getUid();
    }
    @Override public void create(Map<String, Object> data, Callback callback) {
        request("/listings", new JSONObject(data).toString().getBytes(StandardCharsets.UTF_8),
        "application/json", "listingId", callback);
    }
    @Override public void upload(String listingId, byte[] jpeg, Callback callback) {
        if (!listingId.matches("[A-Za-z0-9_-]{1,128}")) {
            callback.failure(Problem.REJECTED);
            return;
        }
        request("/listings/" + listingId + "/photos", jpeg, "image/jpeg", "photoUrl", callback);
    }
    private boolean sameUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null && user.getUid().equals(ownerUid) && user.isEmailVerified()
            && UmEmailPolicy.isUmEmail(user.getEmail());
    }
    private void request(String path, byte[] body, String contentType, String resultKey, Callback callback) {
        URI endpoint;
        try {
            endpoint = URI.create(BuildConfig.LISTING_API_BASE_URL);
            boolean local = BuildConfig.USE_FIREBASE_EMULATORS && "http".equals(endpoint.getScheme())
                && "127.0.0.1".equals(endpoint.getHost());
            if ((!"https".equals(endpoint.getScheme()) && !local) || endpoint.getHost() == null
                || endpoint.getRawUserInfo() != null || endpoint.getRawQuery() != null
                || endpoint.getRawFragment() != null
                || !(endpoint.getPath().isEmpty() || endpoint.getPath().equals("/"))) {
                throw new IllegalArgumentException("endpoint");
            }
        } catch (IllegalArgumentException error) {
            callback.failure(Problem.UNAVAILABLE);
            return;
        }
        if (!sameUser()) {
            callback.failure(Problem.AUTH);
            return;
        }
        URI target = endpoint.resolve(path);
        FirebaseAuth.getInstance().getCurrentUser().getIdToken(false).addOnCompleteListener(task -> {
            if (!task.isSuccessful() || !sameUser() || task.getResult().getToken() == null) {
                callback.failure(Problem.AUTH);
                return;
            }
            String token = task.getResult().getToken();
            IO.execute(() -> send(target, body, contentType, token, resultKey, callback));
        });
    }
    private void send(URI endpoint, byte[] body, String type, String token, String key, Callback callback) {
        HttpURLConnection connection = null;
        try {
            if (!sameUser()) {
                deliver(callback, null, Problem.AUTH);
                return;
            }
            connection = (HttpURLConnection) endpoint.toURL().openConnection();
            connection.setInstanceFollowRedirects(false);
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(30000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + token);
            connection.setRequestProperty("Content-Type", type);
            connection.setFixedLengthStreamingMode(body.length);
            connection.setDoOutput(true);
            try (java.io.OutputStream output = connection.getOutputStream()) {
                output.write(body);
            }
            int code = connection.getResponseCode();
            if (code != 201) {
                // 5xx/timeouts may follow a successful write. Never silently retry or claim failure to save.
                Problem problem = code == 401 || code == 403 ? Problem.AUTH
                    : code >= 400 && code < 500 && code != 408 ? Problem.REJECTED : Problem.UNCERTAIN;
                deliver(callback, null, problem);
                return;
            }
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            try (InputStream input = connection.getInputStream()) {
                byte[] buffer = new byte[1024];
                int count;
                while ((count = input.read(buffer)) != -1) {
                    if (bytes.size() + count > 8192) {
                        throw new java.io.IOException("response-limit");
                    }
                    bytes.write(buffer, 0, count);
                }
            }
            JSONObject response = new JSONObject(bytes.toString(StandardCharsets.UTF_8.name()));
            String value = response.getString(key);
            boolean valid = key.equals("listingId")
                ? value.matches("[A-Za-z0-9_-]{1,128}") && "draft".equals(response.getString("status"))
                : value.startsWith("https://res.cloudinary.com/");
            if (!valid) {
                throw new java.io.IOException("invalid-response");
            }
            deliver(callback, value, null);
        } catch (Exception error) {
            deliver(callback, null, Problem.UNCERTAIN);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    private void deliver(Callback callback, String value, Problem problem) {
        main.post(() -> {
            if (!sameUser()) {
                callback.failure(Problem.UNCERTAIN);
            }
            else if (problem != null) {
                callback.failure(problem);
            }
            else {
                callback.success(value);
            }
        });
    }
}
