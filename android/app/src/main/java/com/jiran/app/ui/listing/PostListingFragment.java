package com.jiran.app.ui.listing;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.jiran.app.data.AreaOption;
import com.jiran.app.data.ListingGateway;
import com.jiran.app.data.ListingPhotoReader;
import com.jiran.app.data.WorkerListingGateway;
import com.jiran.app.databinding.FragmentPostListingBinding;
import com.jiran.app.domain.auth.UmEmailPolicy;
import com.jiran.app.domain.auth.VerifiedHostPolicy;
import com.jiran.app.ui.auth.LauncherActivity;
import com.jiran.app.ui.common.InfoBannerView;
import com.jiran.app.ui.common.JiranSnackbar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/** FR2.1 step 1 only. No publication or Room pricing is performed here. */
public class PostListingFragment extends Fragment {
    private static final ExecutorService PHOTOS = Executors.newSingleThreadExecutor();
    private FragmentPostListingBinding binding;
    private PostListingViewModel model;
    private int renderedPhotos = -1;
    private String hostUid;
    private boolean leavingSession;
    private final FirebaseAuth.IdTokenListener hostListener = auth -> checkHostSession();
    private final ActivityResultLauncher<String[]> pickPhoto = registerForActivityResult(
    new ActivityResultContracts.OpenDocument(), uri -> {
        if (uri == null || !model.preparePhoto()) {
            return;
        }
        android.content.ContentResolver resolver = requireContext().getApplicationContext().getContentResolver();
        PostListingViewModel target = model;
        PHOTOS.execute(() -> {
            byte[] prepared;
            try {
                prepared = ListingPhotoReader.read(resolver, uri);
            }
            catch (Exception error) {
                prepared = null;
            }
            byte[] result = prepared;
            new Handler(Looper.getMainLooper()).post(() -> {
                target.photoPrepared(result);
                if (result == null && binding != null) {
                    JiranSnackbar.make(binding.getRoot(), getString(R.string.post_photo_error)).show();
                }
            });
        });
    });
    protected ListingGateway createGateway() {
        return new WorkerListingGateway();
    }
    protected BuildingPickerDialog createPicker() {
        return new BuildingPickerDialog();
    }
    protected boolean allowsSampleData() {
        return false;
    }
    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!allowsSampleData()) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            hostUid = savedInstanceState == null ? (user == null ? "" : user.getUid())
                    : savedInstanceState.getString("hostUid", "");
        }
        model = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull @Override @SuppressWarnings("unchecked")
            public <T extends ViewModel> T create(@NonNull Class<T> type) {
                PostListingViewModel created = new PostListingViewModel(createGateway());
                // Do not persist private form fields; interrupted writes need reconciliation, not resubmission.
                if (savedInstanceState != null && savedInstanceState.getBoolean("writeStarted")) {
                    created.markInterrupted();
                }
                return (T) created;
            }
        }).get(PostListingViewModel.class);
    }

    @Override public void onStart() {
        super.onStart();
        if (!allowsSampleData()) {
            FirebaseAuth.getInstance().addIdTokenListener(hostListener);
            checkHostSession();
        }
    }

    @Override public void onResume() {
        super.onResume();
        if (!allowsSampleData()) {
            checkHostSession();
        }
    }

    @Override public void onStop() {
        if (!allowsSampleData()) {
            FirebaseAuth.getInstance().removeIdTokenListener(hostListener);
        }
        super.onStop();
    }

    private void checkHostSession() {
        if (leavingSession || !isAdded()) {
            return;
        }
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (VerifiedHostPolicy.permits(hostUid, user == null ? null : user.getUid(),
                user == null ? null : user.getEmail(), user != null && user.isEmailVerified())) {
            return;
        }
        leavingSession = true;
        if (binding != null) {
            binding.getRoot().setVisibility(View.INVISIBLE);
        }
        model.endSession();
        // Non-UM sessions must not loop back through Launcher's generic verified-account route.
        if (user != null && !UmEmailPolicy.isUmEmail(user.getEmail())) {
            auth.signOut();
        }
        android.content.Intent restart = new android.content.Intent(requireContext(), LauncherActivity.class);
        restart.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(restart);
    }
    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater,
    @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPostListingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.address.setText(model.form.address);
        binding.rent.setText(model.form.rent);
        binding.deposit.setText(model.form.deposit);
        binding.address.addTextChangedListener(watch(value -> model.form.address = value));
        binding.rent.addTextChangedListener(watch(value -> model.form.rent = value));
        binding.deposit.addTextChangedListener(watch(value -> model.form.deposit = value));
        binding.gender.check("male_only".equals(model.form.gender) ? R.id.male
            : "female_only".equals(model.form.gender) ? R.id.female : R.id.any);
        binding.gender.setOnCheckedStateChangeListener((group, ids) -> {
            model.form.gender = ids.contains(R.id.male) ? "male_only"
                : ids.contains(R.id.female) ? "female_only" : "any";
        });
        bindAmenity(binding.wifi, "wifi");
        bindAmenity(binding.aircon, "aircon");
        bindAmenity(binding.washingMachine, "washing_machine");
        binding.building.setOnClickListener(button -> openPicker());
        getChildFragmentManager().setFragmentResultListener(BuildingPickerDialog.RESULT_KEY,
        getViewLifecycleOwner(), (key, result) -> {
            if (!model.editable() || (result.getBoolean("sampleData") && !allowsSampleData())
                || !"area_center_estimate".equals(result.getString("coordinateSource"))) {
                return;
            }
            try {
                model.form.area = new AreaOption(result.getString("areaId"), result.getString("areaName"),
                result.getDouble("areaLatitude", Double.NaN),
                result.getDouble("areaLongitude", Double.NaN));
                model.form.buildingName = result.getString("buildingName", "");
                renderBuilding();
            } catch (IllegalArgumentException error) {
                model.form.area = null;
            }
        });
        binding.addPhoto.setOnClickListener(button -> pickPhoto.launch(new String[]{"image/jpeg", "image/png"}));
        binding.save.setOnClickListener(button -> {
            binding.addressLayout.setError(null);
            binding.rentLayout.setError(null);
            binding.depositLayout.setError(null);
            String invalid = model.invalidField();
            if (!invalid.isEmpty()) {
                if (invalid.equals("address")) {
                    binding.addressLayout.setError(getString(R.string.post_required_address));
                }
                if (invalid.equals("rent")) {
                    binding.rentLayout.setError(getString(R.string.post_invalid_rent));
                }
                if (invalid.equals("deposit")) {
                    binding.depositLayout.setError(getString(R.string.post_invalid_deposit));
                }
                binding.status.setKind(InfoBannerView.Kind.WARNING);
                binding.status.setText(getString(R.string.post_invalid));
                binding.postScroll.smoothScrollTo(0, 0);
                return;
            }
            model.save();
        });
        binding.back.setOnClickListener(button -> leave());
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override public void handleOnBackPressed() {
                leave();
            }
        });
        int padding = getResources().getDimensionPixelSize(R.dimen.spacing_16);
        ViewCompat.setOnApplyWindowInsetsListener(view, (target, insets) -> {
            androidx.core.graphics.Insets system = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            int bottom = Math.max(system.bottom, insets.getInsets(WindowInsetsCompat.Type.ime()).bottom);
            target.setPadding(system.left, system.top, system.right, bottom);
            binding.content.setPadding(padding, padding, padding, padding);
            return insets;
        });
        renderBuilding();
        model.state().observe(getViewLifecycleOwner(), this::render);
        ViewCompat.requestApplyInsets(view);
    }
    private void bindAmenity(com.google.android.material.chip.Chip chip, String value) {
        chip.setChecked(model.form.amenities.contains(value));
        chip.setOnCheckedChangeListener((button, checked) -> {
            model.form.amenities.remove(value);
            if (checked) {
                model.form.amenities.add(value);
            }
        });
    }
    private void openPicker() {
        if (getChildFragmentManager().findFragmentByTag("building") != null) {
            return;
        }
        BuildingPickerDialog picker = createPicker();
        Bundle args = new Bundle();
        args.putString("buildingName", model.form.buildingName);
        if (model.form.area != null) {
            args.putString("areaId", model.form.area.id);
            args.putString("areaName", model.form.area.name);
        }
        picker.setArguments(args);
        picker.show(getChildFragmentManager(), "building");
    }
    private void renderBuilding() {
        binding.buildingSummary.setText(model.form.area == null ? getString(R.string.post_building_help)
            : getString(R.string.post_building_summary, model.form.buildingName, model.form.area.name));
    }
    private void render(PostListingViewModel.State state) {
        boolean edit = model.editable();
        for (View field : new View[]{binding.address, binding.rent, binding.deposit, binding.building,
            binding.any, binding.male, binding.female, binding.wifi, binding.aircon, binding.washingMachine}) {
            field.setEnabled(edit);
        }
        binding.addPhoto.setEnabled(edit && model.photos().size() < 5);
        binding.save.setEnabled(!model.busy() && state != PostListingViewModel.State.SAVED
            && state != PostListingViewModel.State.UNCERTAIN);
        binding.back.setEnabled(!model.busy());
        binding.save.setText(state == PostListingViewModel.State.ERROR ? R.string.post_retry : R.string.post_save);
        int message;
        switch (state) {
            case PREPARING: message = R.string.post_preparing;
            break;
            case SAVING: message = R.string.post_saving;
            break;
            case SAVED: message = R.string.post_saved;
            break;
            case UNCERTAIN: message = R.string.post_uncertain;
            break;
            case ERROR:
            message = model.problem() == ListingGateway.Problem.AUTH ? R.string.post_auth
                : model.problem() == ListingGateway.Problem.UNAVAILABLE ? R.string.post_unavailable
                : R.string.post_rejected;
            break;
            default: message = R.string.post_intro;
            break;
        }
        binding.status.setKind(state == PostListingViewModel.State.SAVED ? InfoBannerView.Kind.SUCCESS
            : state == PostListingViewModel.State.ERROR || state == PostListingViewModel.State.UNCERTAIN
            ? InfoBannerView.Kind.WARNING : InfoBannerView.Kind.INFO);
        String statusText = state == PostListingViewModel.State.UPLOADING
            ? getString(R.string.post_uploading, model.uploaded(), model.photos().size()) : getString(message);
        binding.status.setText(allowsSampleData() ? getString(R.string.post_sample, statusText) : statusText);
        renderPhotos(edit);
    }
    private void renderPhotos(boolean editable) {
        if (renderedPhotos != model.photos().size()) {
            binding.photos.removeAllViews();
            renderedPhotos = model.photos().size();
            for (int i = 0;
            i < renderedPhotos;
            i++) {
                byte[] bytes = model.photos().get(i);
                ImageView photo = new ImageView(requireContext());
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 4;
                photo.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options));
                photo.setAdjustViewBounds(true);
                photo.setContentDescription(getString(R.string.post_photo_description, i + 1));
                binding.photos.addView(photo, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
                MaterialButton remove = (MaterialButton) getLayoutInflater().inflate(
                R.layout.item_post_photo_remove, binding.photos, false);
                remove.setText(getString(R.string.post_remove_photo, i + 1));
                int index = i;
                remove.setOnClickListener(button -> model.removePhoto(index));
                binding.photos.addView(remove);
            }
        }
        for (int i = 0;
        i < binding.photos.getChildCount();
        i++) {
            binding.photos.getChildAt(i).setEnabled(editable);
        }
    }
    private void leave() {
        if (model.busy()) {
            return;
        }
        if (model.state().getValue() == PostListingViewModel.State.SAVED) {
            closeForm();
            return;
        }
        new MaterialAlertDialogBuilder(requireContext()).setTitle(R.string.post_leave_title)
            .setMessage(R.string.post_leave_body).setNegativeButton(R.string.post_stay, null)
            .setPositiveButton(R.string.post_leave, (dialog, which) -> closeForm()).show();
    }
    protected void closeForm() {
        NavHostFragment.findNavController(this).popBackStack();
    }
    @Override public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("hostUid", hostUid);
        outState.putBoolean("writeStarted", !model.listingId().isEmpty()
            || model.state().getValue() == PostListingViewModel.State.SAVING
            || model.state().getValue() == PostListingViewModel.State.UNCERTAIN);
    }
    private interface Changed {
        void accept(String value);
    }
    private static TextWatcher watch(Changed callback) {
        return new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence text, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence text, int start, int before, int count) {
                callback.accept(text.toString());
            }
            @Override public void afterTextChanged(Editable text) { }
        };
    }
    @Override public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        renderedPhotos = -1;
    }
}
