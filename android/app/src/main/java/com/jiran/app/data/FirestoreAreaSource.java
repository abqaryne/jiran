package com.jiran.app.data;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Source;
import com.jiran.app.domain.auth.UmEmailPolicy;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Reads only public area references. Firestore Rules authorize the server request. */
public final class FirestoreAreaSource implements AreaSource {
    // Defensive catalog bound, not a product target. Never silently truncate seed data.
    private static final int MAX_AREAS = 500;

    @Override
    public void load(Callback callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || !user.isEmailVerified() || !UmEmailPolicy.isUmEmail(user.getEmail())) {
            callback.onError(Problem.SIGN_IN_REQUIRED);
            return;
        }
        String uid = user.getUid();
        FirebaseFirestore.getInstance().collection("areas").limit(MAX_AREAS + 1)
                .get(Source.SERVER).addOnCompleteListener(task -> {
                    FirebaseUser current = FirebaseAuth.getInstance().getCurrentUser();
                    if (current == null || !uid.equals(current.getUid()) || !current.isEmailVerified()) {
                        callback.onError(Problem.SIGN_IN_REQUIRED);
                        return;
                    }
                    if (!task.isSuccessful()) {
                        Exception error = task.getException();
                        boolean denied = error instanceof FirebaseFirestoreException
                                && (((FirebaseFirestoreException) error).getCode()
                                == FirebaseFirestoreException.Code.PERMISSION_DENIED
                                || ((FirebaseFirestoreException) error).getCode()
                                == FirebaseFirestoreException.Code.UNAUTHENTICATED);
                        callback.onError(denied ? Problem.SIGN_IN_REQUIRED : Problem.UNAVAILABLE);
                        return;
                    }
                    try {
                        if (task.getResult().size() > MAX_AREAS) {
                            throw new IllegalArgumentException("Area catalog exceeds bound");
                        }
                        List<AreaOption> areas = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            areas.add(AreaOption.fromDocument(document.getId(), document.getData()));
                        }
                        areas.sort(Comparator.comparing((AreaOption area) -> area.name,
                                String.CASE_INSENSITIVE_ORDER).thenComparing(area -> area.id));
                        callback.onLoaded(areas);
                    } catch (IllegalArgumentException error) {
                        callback.onError(Problem.INVALID_DATA);
                    }
                });
    }
}
