package com.jiran.app.ui.gallery;

import com.jiran.app.data.AreaOption;
import com.jiran.app.data.AreaSource;
import com.jiran.app.ui.listing.BuildingPickerDialog;
import java.util.Arrays;

/** Debug-only fixture: no network, no seed writes and no real geographic claims. */
public final class SampleBuildingPickerDialog extends BuildingPickerDialog {
    @Override
    protected AreaSource createAreaSource() {
        return callback -> callback.onLoaded(Arrays.asList(
                new AreaOption("sample_a", "Sample area A", 0, 0),
                new AreaOption("sample_b", "Sample area B", 1, 1)));
    }

    @Override
    protected boolean isSamplePreview() { return true; }
}
