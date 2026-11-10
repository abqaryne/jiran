package com.jiran.app.ui.gallery;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
/** Debug-only local fixture;
never writes to Firebase or uploads photos. */
public final class PostPreviewActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        EdgeToEdge.enable(this);
        if (state == null) {
            getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SamplePostFragment()).commit();
        }
    }
}
