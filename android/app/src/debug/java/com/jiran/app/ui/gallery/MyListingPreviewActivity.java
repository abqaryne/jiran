package com.jiran.app.ui.gallery;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

/** FR2.1 offline preview, accessible from the Jiran UI launcher. */
public final class MyListingPreviewActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        EdgeToEdge.enable(this);
        if (state == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new SampleMyListingFragment()).commit();
        }
    }
}
