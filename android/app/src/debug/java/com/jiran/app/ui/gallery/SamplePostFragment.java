package com.jiran.app.ui.gallery;
import android.os.Handler;
import android.os.Looper;
import com.jiran.app.data.ListingGateway;
import com.jiran.app.ui.listing.BuildingPickerDialog;
import com.jiran.app.ui.listing.PostListingFragment;
import java.util.Map;
public final class SamplePostFragment extends PostListingFragment {
    @Override protected boolean allowsSampleData() {
        return true;
    }
    @Override protected BuildingPickerDialog createPicker() {
        return new SampleBuildingPickerDialog();
    }
    @Override protected void closeForm() {
        requireActivity().finish();
    }
    @Override protected ListingGateway createGateway() {
        return new ListingGateway() {
            private final Handler main = new Handler(Looper.getMainLooper());
            @Override public void create(Map<String, Object> input, Callback callback) {
                main.postDelayed(() -> callback.success("sample-draft"), 600);
            }
            @Override public void upload(String id, byte[] bytes, Callback callback) {
                main.postDelayed(() -> callback.success("sample-photo"), 600);
            }
        };
    }
}
