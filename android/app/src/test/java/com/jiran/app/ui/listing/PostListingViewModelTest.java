package com.jiran.app.ui.listing;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import com.jiran.app.data.AreaOption;
import com.jiran.app.data.ListingGateway;
import java.util.Map;
import org.junit.Rule;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
public class PostListingViewModelTest {
    @Rule public InstantTaskExecutorRule instant = new InstantTaskExecutorRule();
    private final Fake gateway = new Fake();
    private PostListingViewModel ready(int photoCount) {
        PostListingViewModel model = new PostListingViewModel(gateway);
        model.form.area = new AreaOption("sample", "Sample Area", 0, 0);
        model.form.buildingName = "Fictional Building";
        model.form.address = "Fictional Unit 1";
        model.form.rent = "10";
        model.form.deposit = "0";
        for (int i = 0;
        i < photoCount;
        i++) {
            model.preparePhoto();
            model.photoPrepared(new byte[]{1});
        }
        return model;
    }
    @Test public void noWriteUntilValidAndPhotoPresent() {
        PostListingViewModel model = ready(0);
        model.save();
        assertEquals(0, gateway.creates);
        model.preparePhoto();
        model.photoPrepared(new byte[]{1});
        model.form.rent = "0";
        model.save();
        assertEquals(0, gateway.creates);
    }
    @Test public void createsOnceAndUploadsSequentiallyBeforeSuccess() {
        PostListingViewModel model = ready(2);
        model.save();
        model.save();
        assertEquals(1, gateway.creates);
        gateway.callback.success("draft-1");
        assertEquals(1, gateway.uploads);
        assertFalse(model.editable());
        gateway.callback.success("photo1");
        assertEquals(2, gateway.uploads);
        assertEquals(1, model.uploaded());
        gateway.callback.success("photo2");
        assertEquals(PostListingViewModel.State.SAVED, model.state().getValue());
        model.save();
        assertEquals(1, gateway.creates);
    }
    @Test public void explicitRejectedUploadRetriesOnlyUnfinishedPhoto() {
        PostListingViewModel model = ready(2);
        model.save();
        gateway.callback.success("draft-1");
        gateway.callback.success("photo1");
        gateway.callback.failure(ListingGateway.Problem.REJECTED);
        model.save();
        assertEquals(1, gateway.creates);
        assertEquals(3, gateway.uploads);
        assertEquals(1, model.uploaded());
    }
    @Test public void ambiguousCreateOrUploadBlocksRetry() {
        PostListingViewModel model = ready(1);
        model.save();
        gateway.callback.failure(ListingGateway.Problem.UNCERTAIN);
        model.save();
        assertEquals(1, gateway.creates);
        assertFalse(model.editable());
        model = ready(1);
        model.save();
        gateway.callback.success("draft-2");
        gateway.callback.failure(ListingGateway.Problem.UNCERTAIN);
        model.save();
        assertEquals(1, gateway.uploads);
    }
    @Test public void failedPreparationAndFivePhotoBound() {
        PostListingViewModel model = ready(0);
        model.preparePhoto();
        model.photoPrepared(null);
        assertTrue(model.photos().isEmpty());
        model = ready(5);
        assertFalse(model.preparePhoto());
        model.removePhoto(1);
        assertEquals(4, model.photos().size());
    }
    @Test public void unavailableEndpointAllowsEditWithoutFalseSuccess() {
        PostListingViewModel model = ready(1);
        model.save();
        gateway.callback.failure(ListingGateway.Problem.UNAVAILABLE);
        assertTrue(model.editable());
        assertEquals(PostListingViewModel.State.ERROR, model.state().getValue());
    }
    @Test public void destroyedModelIgnoresLateCompletion() {
        PostListingViewModel model = ready(1);
        model.save();
        model.onCleared();
        gateway.callback.success("draft-1");
        assertEquals(0, gateway.uploads);
        assertEquals("", model.form.address);
    }
    private static final class Fake implements ListingGateway {
        Callback callback;
        int creates;
        int uploads;
        @Override public void create(Map<String, Object> input, Callback callback) {
            creates++;
            this.callback = callback;
        }
        @Override public void upload(String id, byte[] bytes, Callback callback) {
            uploads++;
            this.callback = callback;
        }
    }
}
