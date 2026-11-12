package com.jiran.app.data;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.jiran.app.domain.profile.DisplayName;
import com.jiran.app.domain.profile.SetupProgress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link ProfileRepository} on Cloud Firestore. Security Rules are the authority (owner-only private documents,
 * verified UM account); this class only shapes the documents. Needs human review: it is the first Firestore write
 * path for user data in the app.
 */
public final class FirestoreProfileRepository implements ProfileRepository {

    private final FirebaseAuth auth;
    private final FirebaseFirestore db;

    public FirestoreProfileRepository() {
        this(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance());
    }

    FirestoreProfileRepository(FirebaseAuth auth, FirebaseFirestore db) {
        this.auth = auth;
        this.db = db;
    }

    @Override
    public String signUpName() {
        FirebaseUser user = auth.getCurrentUser();
        String name = user == null ? null : user.getDisplayName();
        return name == null ? "" : name.trim();
    }

    @Override
    public void loadProgress(ProgressCallback callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            callback.onError(Problem.SIGN_IN_REQUIRED);
            return;
        }
        Task<DocumentSnapshot> publicDoc = userDoc(user).get();
        Task<DocumentSnapshot> profile = privateDoc(user, "profile").get();
        Tasks.whenAllComplete(publicDoc, profile).addOnCompleteListener(all -> {
            for (Task<DocumentSnapshot> part : Arrays.asList(publicDoc, profile)) {
                if (!part.isSuccessful()) {
                    callback.onError(map(part.getException()));
                    return;
                }
            }
            DocumentSnapshot pub = publicDoc.getResult();
            DocumentSnapshot prof = profile.getResult();
            callback.onLoaded(SetupProgress.fromFields(pub.getData(), prof.getData()));
        });
    }

    @Override
    public void saveBasics(String fullName, String gender, String faculty, int year, Callback callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            callback.onError(Problem.SIGN_IN_REQUIRED);
            return;
        }
        String name = fullName.trim();
        // Read first so createdAt is set only once, even if the student comes back to step 1.
        userDoc(user).get().addOnCompleteListener(read -> {
            if (!read.isSuccessful()) {
                callback.onError(map(read.getException()));
                return;
            }
            Map<String, Object> pub = new HashMap<>();
            pub.put("uid", user.getUid());
            pub.put("initials", DisplayName.initials(name));
            pub.put("gender", gender);
            pub.put("faculty", faculty);
            pub.put("year", year);
            pub.put("updatedAt", FieldValue.serverTimestamp());
            if (!read.getResult().exists()) {
                pub.put("createdAt", FieldValue.serverTimestamp());
                pub.put("lifestyleTags", new ArrayList<String>());
            }
            WriteBatch batch = db.batch();
            batch.set(userDoc(user), pub, SetOptions.merge());
            batch.set(privateDoc(user, "profile"), Collections.singletonMap("fullName", name), SetOptions.merge());
            batch.set(privateDoc(user, "contact"), Collections.singletonMap("firstName", DisplayName.firstName(name)), SetOptions.merge());
            commit(batch, callback);
        });
    }

    @Override
    public void savePreferences(long budgetMinSen, long budgetMaxSen, List<String> lifestyleTags,
            boolean hasCar, Callback callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            callback.onError(Problem.SIGN_IN_REQUIRED);
            return;
        }
        Map<String, Object> pub = new HashMap<>();
        pub.put("uid", user.getUid());
        pub.put("lifestyleTags", new ArrayList<>(lifestyleTags));
        pub.put("updatedAt", FieldValue.serverTimestamp());
        Map<String, Object> prof = new HashMap<>();
        prof.put("budgetMinSen", budgetMinSen);
        prof.put("budgetMaxSen", budgetMaxSen);
        prof.put("hasCar", hasCar);
        WriteBatch batch = db.batch();
        batch.set(userDoc(user), pub, SetOptions.merge());
        batch.set(privateDoc(user, "profile"), prof, SetOptions.merge());
        commit(batch, callback);
    }

    @Override
    public void savePrivacy(boolean showPhoneToConnections, boolean showBudgetToConnections, boolean showPhotoToHouseholdOnly,
            Callback callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            callback.onError(Problem.SIGN_IN_REQUIRED);
            return;
        }
        Map<String, Object> privacy = new HashMap<>();
        privacy.put("showPhoneToConnections", showPhoneToConnections);
        privacy.put("showBudgetToConnections", showBudgetToConnections);
        privacy.put("showPhotoToHouseholdOnly", showPhotoToHouseholdOnly);
        WriteBatch batch = db.batch();
        batch.set(privateDoc(user, "profile"), Collections.singletonMap("privacy", privacy), SetOptions.merge());
        commit(batch, callback);
    }

    private DocumentReference userDoc(FirebaseUser user) {
        return db.collection("users").document(user.getUid());
    }

    private DocumentReference privateDoc(FirebaseUser user, String name) {
        return userDoc(user).collection("private").document(name);
    }

    private static void commit(WriteBatch batch, Callback callback) {
        batch.commit().addOnCompleteListener(done -> {
            if (done.isSuccessful()) {
                callback.onSaved();
            } else {
                callback.onError(map(done.getException()));
            }
        });
    }

    private static Problem map(Exception error) {
        if (error instanceof FirebaseNetworkException) {
            return Problem.NETWORK;
        }
        if (error instanceof FirebaseFirestoreException) {
            FirebaseFirestoreException.Code code = ((FirebaseFirestoreException) error).getCode();
            if (code == FirebaseFirestoreException.Code.PERMISSION_DENIED
                    || code == FirebaseFirestoreException.Code.UNAUTHENTICATED) {
                return Problem.SIGN_IN_REQUIRED;
            }
            if (code == FirebaseFirestoreException.Code.UNAVAILABLE) {
                return Problem.NETWORK;
            }
        }
        return Problem.UNKNOWN;
    }
}
