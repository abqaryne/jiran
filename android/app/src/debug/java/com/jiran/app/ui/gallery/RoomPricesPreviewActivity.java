package com.jiran.app.ui.gallery;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

/** Offline design-system demo. The sample repository never calls Firebase. */
public final class RoomPricesPreviewActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        EdgeToEdge.enable(this);
        if (state == null) {
            SampleRoomPricesFragment fragment = new SampleRoomPricesFragment();
            Bundle args = new Bundle();
            args.putString("listingId", "sample-rooms");
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction().replace(android.R.id.content, fragment).commit();
        }
    }
}
