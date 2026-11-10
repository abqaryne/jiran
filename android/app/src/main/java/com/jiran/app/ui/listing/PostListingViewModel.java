package com.jiran.app.ui.listing;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.jiran.app.data.ListingForm;
import com.jiran.app.data.ListingGateway;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/** Retains the form through rotation;
confirms each server write before advancing. */
public final class PostListingViewModel extends ViewModel {
    public enum State { EDITING, PREPARING, SAVING, UPLOADING, SAVED, ERROR, UNCERTAIN }
    public final ListingForm form = new ListingForm();
    private final ListingGateway gateway;
    private final MutableLiveData<State> state = new MutableLiveData<>(State.EDITING);
    private final List<byte[]> photos = new ArrayList<>();
    private String listingId = "";
    private int uploaded;
    private boolean cleared;
    private ListingGateway.Problem problem;
    public PostListingViewModel(ListingGateway gateway) {
        this.gateway = gateway;
    }
    public LiveData<State> state() {
        return state;
    }
    public String listingId() {
        return listingId;
    }
    public int uploaded() {
        return uploaded;
    }
    public ListingGateway.Problem problem() {
        return problem;
    }
    public List<byte[]> photos() {
        return Collections.unmodifiableList(photos);
    }
    public boolean editable() {
        return listingId.isEmpty() && (state.getValue() == State.EDITING || state.getValue() == State.ERROR);
    }
    public boolean busy() {
        return state.getValue() == State.SAVING || state.getValue() == State.UPLOADING
            || state.getValue() == State.PREPARING;
    }
    public boolean preparePhoto() {
        if (cleared || !editable() || photos.size() >= 5) {
            return false;
        }
        state.setValue(State.PREPARING);
        return true;
    }
    public void photoPrepared(byte[] bytes) {
        if (cleared || state.getValue() != State.PREPARING) {
            return;
        }
        if (bytes != null && bytes.length > 0 && bytes.length <= 2 * 1024 * 1024) {
            photos.add(bytes);
        }
        state.setValue(State.EDITING);
    }
    public void removePhoto(int index) {
        if (editable() && index >= 0 && index < photos.size()) {
            photos.remove(index);
            state.setValue(State.EDITING);
        }
    }
    public String invalidField() {
        String invalid = form.invalidField();
        return !invalid.isEmpty() ? invalid : photos.isEmpty() ? "photos" : "";
    }
    public void save() {
        if (cleared || busy() || state.getValue() == State.SAVED || state.getValue() == State.UNCERTAIN
            || !invalidField().isEmpty()) {
            return;
        }
        if (!listingId.isEmpty()) {
            uploadNext();
            return;
        }
        state.setValue(State.SAVING);
        gateway.create(form.payload(), new ListingGateway.Callback() {
            @Override public void success(String value) {
                if (cleared) {
                    return;
                }
                listingId = value;
                uploadNext();
            }
            @Override public void failure(ListingGateway.Problem value) {
                fail(value);
            }
        });
    }
    private void uploadNext() {
        if (uploaded == photos.size()) {
            state.setValue(State.SAVED);
            return;
        }
        state.setValue(State.UPLOADING);
        gateway.upload(listingId, photos.get(uploaded), new ListingGateway.Callback() {
            @Override public void success(String value) {
                if (cleared) {
                    return;
                }
                uploaded++;
                uploadNext();
            }
            @Override public void failure(ListingGateway.Problem value) {
                fail(value);
            }
        });
    }
    private void fail(ListingGateway.Problem value) {
        if (cleared) {
            return;
        }
        problem = value;
        state.setValue(value == ListingGateway.Problem.UNCERTAIN ? State.UNCERTAIN : State.ERROR);
    }
    public void markInterrupted() {
        state.setValue(State.UNCERTAIN);
    }
    public void endSession() {
        cleared = true;
        photos.clear();
        form.address = "";
        form.buildingName = "";
        form.area = null;
        form.rent = "";
        form.deposit = "";
        form.amenities.clear();
    }

    @Override protected void onCleared() {
        endSession();
    }
}
