package com.jiran.app.ui.listing;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jiran.app.R;
import com.jiran.app.data.FirestoreRoomRepository;
import com.jiran.app.data.RoomRepository;
import com.jiran.app.databinding.FragmentRoomPricesBinding;
import com.jiran.app.databinding.ItemRoomPriceEditorBinding;
import com.jiran.app.domain.auth.VerifiedHostPolicy;
import com.jiran.app.domain.auth.UmEmailPolicy;
import com.jiran.app.domain.profile.Ringgit;
import com.jiran.app.ui.auth.LauncherActivity;
import com.jiran.app.ui.common.InfoBannerView;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/** Draft Rooms editor. No publication or household membership changes happen here. */
public class RoomPricesFragment extends Fragment {
    private FragmentRoomPricesBinding binding;
    private RoomPricesViewModel model;
    private final List<ItemRoomPriceEditorBinding> rowViews = new ArrayList<>();
    private final List<String> renderedIds = new ArrayList<>();
    private boolean rendering;
    private boolean leaving;
    private String hostUid;
    private final FirebaseAuth.IdTokenListener hostListener = auth -> checkHost();

    protected boolean allowsSampleData() { return false; }
    protected RoomRepository createRepository() { return new FirestoreRoomRepository(); }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!allowsSampleData()) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            hostUid = savedInstanceState == null ? (user == null ? "" : user.getUid())
                    : savedInstanceState.getString("hostUid", "");
        }
        String listingId = requireArguments().getString("listingId", "");
        model = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull @Override @SuppressWarnings("unchecked")
            public <T extends ViewModel> T create(@NonNull Class<T> type) {
                return (T) new RoomPricesViewModel(listingId, createRepository());
            }
        }).get(RoomPricesViewModel.class);
    }

    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater,
            @Nullable ViewGroup container, @Nullable Bundle state) {
        binding = FragmentRoomPricesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle state) {
        binding.roomPricesBack.setOnClickListener(button -> leave());
        binding.roomPricesAdd.setOnClickListener(button -> new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.room_prices_add).setItems(new String[]{getString(R.string.room_prices_master),
                    getString(R.string.room_prices_medium), getString(R.string.room_prices_small)},
                    (dialog, which) -> model.add(new String[]{"master", "medium", "small"}[which])).show());
        binding.roomPricesApply.setOnClickListener(button -> new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.room_prices_replace_title).setMessage(R.string.room_prices_replace_body)
                .setNegativeButton(R.string.post_stay, null)
                .setPositiveButton(R.string.room_prices_apply, (dialog, which) -> model.applySplit()).show());
        binding.roomPricesSave.setOnClickListener(button -> {
            if (model.state() == RoomPricesViewModel.State.SAVED) {
                closeForm();
            } else {
                model.save();
            }
        });
        binding.roomPricesReload.setOnClickListener(button -> {
            if (!model.dirty()) {
                model.reload();
                return;
            }
            new MaterialAlertDialogBuilder(requireContext()).setTitle(R.string.room_prices_reload_title)
                    .setMessage(R.string.room_prices_reload_body).setNegativeButton(R.string.post_stay, null)
                    .setPositiveButton(R.string.room_prices_reload, (dialog, which) -> model.reload()).show();
        });
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override public void handleOnBackPressed() { leave(); }
        });
        ViewCompat.setOnApplyWindowInsetsListener(view, (target, insets) -> {
            androidx.core.graphics.Insets system = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            target.setPadding(system.left, system.top, system.right,
                    Math.max(system.bottom, insets.getInsets(WindowInsetsCompat.Type.ime()).bottom));
            return insets;
        });
        model.changes().observe(getViewLifecycleOwner(), ignored -> render());
        ViewCompat.requestApplyInsets(view);
    }

    private void render() {
        if (binding == null || leaving) { return; }
        if (model.state() == RoomPricesViewModel.State.AUTH) {
            exitSession();
            return;
        }
        rendering = true;
        boolean edit = model.editable();
        List<String> ids = new ArrayList<>();
        for (RoomPricesViewModel.Row row : model.rows()) { ids.add(row.id); }
        if (!ids.equals(renderedIds)) {
            binding.roomPriceRows.removeAllViews();
            rowViews.clear();
            renderedIds.clear();
            renderedIds.addAll(ids);
            for (RoomPricesViewModel.Row row : model.rows()) { addRow(row); }
        }
        for (int i = 0; i < rowViews.size(); i++) {
            ItemRoomPriceEditorBinding item = rowViews.get(i);
            RoomPricesViewModel.Row row = model.rows().get(i);
            item.roomEditorTitle.setText(getString(R.string.room_prices_row, i + 1, typeLabel(row.type)));
            item.roomEditorPriceLayout.setHint(getString(R.string.room_prices_row, i + 1, typeLabel(row.type))
                    + " · " + getString(R.string.room_prices_price));
            if (!item.roomEditorPrice.getText().toString().equals(row.price())) {
                item.roomEditorPrice.setText(row.price());
            }
            item.roomEditorPriceLayout.setError(!row.price().isEmpty() && Ringgit.toSen(row.price()) == null
                    ? getString(R.string.room_prices_price_error) : null);
            item.roomEditorOccupancy.check(row.occupied() ? R.id.room_editor_occupied : R.id.room_editor_vacant);
            item.roomEditorPrice.setEnabled(edit);
            item.roomEditorVacant.setEnabled(edit);
            item.roomEditorOccupied.setEnabled(edit);
            item.roomEditorRemove.setEnabled(edit);
        }
        binding.roomPricesRent.setText(getString(R.string.room_prices_rent,
                RoomPricesViewModel.money(BigInteger.valueOf(model.rent()))));
        binding.roomPricesEstimate.setText(model.configInvalid() ? R.string.room_prices_bad_config
                : model.configFallback() ? R.string.room_prices_fallback : R.string.room_prices_estimate);
        binding.roomPricesApply.setEnabled(edit && !model.configInvalid() && !model.rows().isEmpty());
        binding.roomPricesAdd.setEnabled(edit && model.rows().size() < RoomRepository.MAX_ROOMS);
        binding.roomPricesBack.setEnabled(model.state() != RoomPricesViewModel.State.SAVING);
        binding.roomPricesSave.setEnabled(model.canSave() || model.state() == RoomPricesViewModel.State.SAVED);
        binding.roomPricesSave.setText(model.state() == RoomPricesViewModel.State.SAVED
                ? R.string.room_prices_done : R.string.room_prices_save);
        BigInteger remaining = model.remainingSen();
        boolean over = remaining != null && remaining.signum() < 0;
        binding.roomPricesRemaining.setKind(over ? InfoBannerView.Kind.DANGER : InfoBannerView.Kind.INFO);
        binding.roomPricesRemaining.setText(remaining == null ? getString(R.string.room_prices_incomplete)
                : getString(over ? R.string.room_prices_over : R.string.room_prices_remaining,
                        RoomPricesViewModel.money(over ? remaining.negate() : remaining)));
        renderStatus();
        rendering = false;
    }

    private void renderStatus() {
        int message = model.rows().isEmpty() ? R.string.room_prices_empty
                : model.rows().size() == RoomRepository.MAX_ROOMS ? R.string.room_prices_limit : R.string.room_prices_ready;
        InfoBannerView.Kind kind = InfoBannerView.Kind.INFO;
        switch (model.state()) {
            case LOADING: message = R.string.room_prices_loading;
                break;
            case SAVING: message = R.string.room_prices_saving;
                break;
            case SAVED: message = R.string.room_prices_saved;
                kind = InfoBannerView.Kind.SUCCESS;
                break;
            case LOAD_ERROR: message = R.string.room_prices_load_error;
                kind = InfoBannerView.Kind.WARNING;
                break;
            case SAVE_ERROR:
                message = model.problem() == RoomRepository.Problem.CONFLICT
                        ? R.string.room_prices_conflict : R.string.room_prices_save_error;
                kind = InfoBannerView.Kind.WARNING;
                break;
            default: break;
        }
        binding.roomPricesStatus.setKind(kind);
        binding.roomPricesStatus.setText(allowsSampleData() ? getString(R.string.post_sample, getString(message))
                : getString(message));
        binding.roomPricesReload.setVisibility(model.state() == RoomPricesViewModel.State.LOAD_ERROR
                || model.state() == RoomPricesViewModel.State.SAVE_ERROR ? View.VISIBLE : View.GONE);
    }

    private void addRow(RoomPricesViewModel.Row row) {
        ItemRoomPriceEditorBinding item = ItemRoomPriceEditorBinding.inflate(getLayoutInflater(), binding.roomPriceRows, false);
        item.getRoot().setSaveFromParentEnabled(false);
        item.roomEditorPrice.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!rendering) { model.price(row.id, s.toString()); }
            }
            @Override public void afterTextChanged(Editable s) { }
        });
        item.roomEditorOccupancy.setOnCheckedStateChangeListener((group, checked) -> {
            if (!rendering) { model.occupied(row.id, checked.contains(R.id.room_editor_occupied)); }
        });
        item.roomEditorRemove.setVisibility(row.persisted ? View.GONE : View.VISIBLE);
        item.roomEditorRemove.setOnClickListener(button -> model.removeNew(row.id));
        rowViews.add(item);
        binding.roomPriceRows.addView(item.getRoot());
    }

    private String typeLabel(String type) {
        return getString("master".equals(type) ? R.string.room_prices_master
                : "medium".equals(type) ? R.string.room_prices_medium : R.string.room_prices_small);
    }

    private void leave() {
        if (model.state() == RoomPricesViewModel.State.SAVING) { return; }
        if (!model.dirty()) {
            closeForm();
            return;
        }
        new MaterialAlertDialogBuilder(requireContext()).setTitle(R.string.post_leave_title)
                .setMessage(R.string.post_leave_body).setNegativeButton(R.string.post_stay, null)
                .setPositiveButton(R.string.post_leave, (dialog, which) -> closeForm()).show();
    }

    protected void closeForm() {
        if (model.state() == RoomPricesViewModel.State.SAVED) {
            NavHostFragment.findNavController(this).popBackStack(R.id.postListingFragment, true);
        } else {
            NavHostFragment.findNavController(this).popBackStack();
        }
    }

    @Override public void onStart() {
        super.onStart();
        if (!allowsSampleData()) {
            FirebaseAuth.getInstance().addIdTokenListener(hostListener);
            checkHost();
        }
    }
    @Override public void onStop() {
        if (!allowsSampleData()) { FirebaseAuth.getInstance().removeIdTokenListener(hostListener); }
        super.onStop();
    }
    private void checkHost() {
        if (leaving || !isAdded()) { return; }
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (!VerifiedHostPolicy.permits(hostUid, user == null ? null : user.getUid(),
                user == null ? null : user.getEmail(), user != null && user.isEmailVerified())) {
            model.endSession();
            exitSession();
        }
    }
    private void exitSession() {
        if (leaving) { return; }
        leaving = true;
        if (binding != null) { binding.getRoot().setVisibility(View.INVISIBLE); }
        if (!allowsSampleData()) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null && !UmEmailPolicy.isUmEmail(user.getEmail())) { FirebaseAuth.getInstance().signOut(); }
        }
        startActivity(new Intent(requireContext(), LauncherActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }
    @Override public void onSaveInstanceState(@NonNull Bundle state) {
        super.onSaveInstanceState(state);
        state.putString("hostUid", hostUid);
    }
    @Override public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        rowViews.clear();
        renderedIds.clear();
    }
}
