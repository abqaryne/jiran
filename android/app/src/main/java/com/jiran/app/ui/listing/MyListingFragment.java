package com.jiran.app.ui.listing;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jiran.app.R;
import com.jiran.app.data.FirestoreMyListingRepository;
import com.jiran.app.data.MyListingRepository;
import com.jiran.app.data.RoomAllocation;
import com.jiran.app.data.RoomRepository;
import com.jiran.app.databinding.FragmentMyListingBinding;
import com.jiran.app.domain.auth.VerifiedHostPolicy;
import com.jiran.app.ui.auth.LauncherActivity;
import com.jiran.app.ui.common.InfoBannerView;
import java.math.BigInteger;

/** Host listing management; request details use only the message and status, never private profiles. */
public class MyListingFragment extends Fragment {
    private FragmentMyListingBinding binding;
    private MyListingViewModel model;
    private String hostUid;
    private boolean leaving;
    private androidx.appcompat.app.AlertDialog requestDialog;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable ticker = new Runnable() {
        @Override public void run() {
            model.tick();
            handler.postDelayed(this, 1000);
        }
    };
    private final FirebaseAuth.IdTokenListener hostListener = auth -> checkHost();
    protected boolean allowsSampleData() { return false; }
    protected MyListingRepository createRepository() { return new FirestoreMyListingRepository(); }
    protected void closeScreen() { NavHostFragment.findNavController(this).popBackStack(); }

    @Override public void onCreate(@Nullable Bundle state) {
        super.onCreate(state);
        if (!allowsSampleData()) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            hostUid = state == null ? (user == null ? "" : user.getUid()) : state.getString("hostUid", "");
        }
        String id = getArguments() == null ? null : getArguments().getString("listingId");
        model = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull @Override @SuppressWarnings("unchecked")
            public <T extends ViewModel> T create(@NonNull Class<T> type) {
                return (T) new MyListingViewModel(id, createRepository(), System::currentTimeMillis);
            }
        }).get(MyListingViewModel.class);
    }
    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater,
            @Nullable ViewGroup container, @Nullable Bundle state) {
        binding = FragmentMyListingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle state) {
        binding.myListingBack.setOnClickListener(button -> closeScreen());
        binding.myListingRenew.setOnClickListener(button -> model.renew());
        binding.myListingRefresh.setOnClickListener(button -> model.reload());
        binding.myListingSample.setVisibility(allowsSampleData() ? View.VISIBLE : View.GONE);
        binding.myListingSample.setText(getString(R.string.my_listing_sample));
        ViewCompat.setOnApplyWindowInsetsListener(view, (target, insets) -> {
            androidx.core.graphics.Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            target.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });
        model.changes().observe(getViewLifecycleOwner(), ignored -> render());
        ViewCompat.requestApplyInsets(view);
    }
    private MyListingRepository.Listing rendered;
    private boolean renderedEnabled;
    private void render() {
        if (binding == null || leaving) { return; }
        if (model.state() == MyListingViewModel.State.AUTH) {
            leaving = true;
            if (requestDialog != null) { requestDialog.dismiss(); }
            Intent intent = new Intent(requireContext(), LauncherActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
            return;
        }
        MyListingRepository.Listing listing = model.listing();
        binding.myListingContent.setVisibility(listing == null ? View.GONE : View.VISIBLE);
        boolean busy = model.state() == MyListingViewModel.State.LOADING || model.state() == MyListingViewModel.State.SAVING;
        binding.myListingRefresh.setEnabled(!busy);
        binding.myListingRenew.setEnabled(model.canRenew());
        int feedback = R.string.my_listing_loading;
        switch (model.state()) {
            case EMPTY: feedback = R.string.my_listing_empty;
                break;
            case SAVING: feedback = R.string.my_listing_saving;
                break;
            case LOAD_ERROR: feedback = R.string.my_listing_load_error;
                break;
            case SAVE_ERROR:
                feedback = model.problem() == RoomRepository.Problem.CONFLICT ? R.string.my_listing_conflict
                        : listing != null && listing.vacantRooms() == 0 ? R.string.my_listing_close_error
                        : R.string.my_listing_save_error;
                break;
            default: break;
        }
        binding.myListingFeedback.setVisibility(model.state() == MyListingViewModel.State.READY ? View.GONE : View.VISIBLE);
        binding.myListingFeedback.setKind(model.state() == MyListingViewModel.State.SAVE_ERROR
                || model.state() == MyListingViewModel.State.LOAD_ERROR ? InfoBannerView.Kind.WARNING : InfoBannerView.Kind.INFO);
        binding.myListingFeedback.setText(getString(feedback));
        if (listing == null) {
            rendered = null;
            return;
        }
        binding.myListingBuilding.setText(listing.buildingName);
        String status = model.status();
        binding.myListingStatus.setKind("active".equals(status) ? InfoBannerView.Kind.SUCCESS : InfoBannerView.Kind.WARNING);
        binding.myListingStatus.setText("active".equals(status) ? getString(R.string.my_listing_active, model.daysRemaining())
                : getString("expired".equals(status) ? R.string.my_listing_expired
                : listing.vacantRooms() == 0 ? R.string.my_listing_full : R.string.my_listing_closed));
        binding.myListingRenew.setVisibility(listing.vacantRooms() > 0 ? View.VISIBLE : View.GONE);
        if (rendered == listing && renderedEnabled == model.canAct()) { return; }
        rendered = listing;
        renderedEnabled = model.canAct();
        binding.myListingRooms.removeAllViews();
        for (RoomAllocation room : listing.rooms) {
            TextView row = textRow(binding.myListingRooms);
            int type = "master".equals(room.type) ? R.string.room_prices_master
                    : "medium".equals(room.type) ? R.string.room_prices_medium : R.string.room_prices_small;
            row.setText(getString(R.string.my_listing_room, getString(type),
                    RoomPricesViewModel.display(BigInteger.valueOf(room.priceSen)),
                    getString(room.occupied ? R.string.room_prices_occupied : R.string.room_prices_vacant)));
            row.setCompoundDrawablesRelativeWithIntrinsicBounds(room.occupied ? R.drawable.ic_person : R.drawable.ic_door_front,
                    0, 0, 0);
        }
        if (listing.rooms.isEmpty()) { textRow(binding.myListingRooms).setText(R.string.my_listing_no_rooms); }
        binding.myListingRequests.removeAllViews();
        int index = 0;
        for (MyListingRepository.Request request : listing.requests) {
            String title = getString(R.string.my_listing_request, ++index, getString(requestStatus(request.status)));
            MaterialButton button = (MaterialButton) getLayoutInflater().inflate(R.layout.item_my_listing_request,
                    binding.myListingRequests, false);
            button.setText(title);
            button.setIconResource("pending".equals(request.status) ? R.drawable.ic_person : R.drawable.ic_info);
            button.setEnabled(model.canAct());
            button.setOnClickListener(view -> openRequest(request, title));
            binding.myListingRequests.addView(button);
        }
        if (listing.requests.isEmpty()) { textRow(binding.myListingRequests).setText(R.string.my_listing_no_requests); }
    }
    private TextView textRow(ViewGroup parent) {
        TextView row = (TextView) getLayoutInflater().inflate(R.layout.item_my_listing_room, parent, false);
        parent.addView(row);
        return row;
    }
    private static int requestStatus(String status) {
        switch (status) {
            case "accepted": return R.string.my_listing_accepted;
            case "rejected": return R.string.my_listing_rejected;
            case "cancelled": return R.string.my_listing_cancelled;
            default: return R.string.my_listing_pending;
        }
    }
    private void openRequest(MyListingRepository.Request request, String title) {
        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(requireContext()).setTitle(title)
                .setMessage(getString(R.string.my_listing_request_body, getString(requestStatus(request.status)), request.message))
                .setNeutralButton(R.string.my_listing_dismiss, null);
        if ("pending".equals(request.status)) {
            dialog.setPositiveButton(R.string.my_listing_accept, (target, which) -> model.decide(request.requesterUid, "accepted"))
                    .setNegativeButton(R.string.my_listing_reject, (target, which) -> model.decide(request.requesterUid, "rejected"));
        }
        requestDialog = dialog.show();
    }
    private void checkHost() {
        if (allowsSampleData() || model == null) { return; }
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || !VerifiedHostPolicy.permits(hostUid, user.getUid(), user.getEmail(), user.isEmailVerified())) {
            model.endSession();
        }
    }
    @Override public void onStart() {
        super.onStart();
        if (!allowsSampleData()) {
            FirebaseAuth.getInstance().addIdTokenListener(hostListener);
            checkHost();
        }
        if (model.state() == MyListingViewModel.State.READY) { model.reload(); }
        handler.post(ticker);
    }
    @Override public void onStop() {
        handler.removeCallbacks(ticker);
        if (!allowsSampleData()) { FirebaseAuth.getInstance().removeIdTokenListener(hostListener); }
        if (requestDialog != null) { requestDialog.dismiss(); }
        super.onStop();
    }
    @Override public void onSaveInstanceState(@NonNull Bundle state) {
        super.onSaveInstanceState(state);
        state.putString("hostUid", hostUid);
    }
    @Override public void onDestroyView() {
        binding = null;
        rendered = null;
        super.onDestroyView();
    }
}
