package candybar.lib.fragments.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.afollestad.materialdialogs.MaterialDialog;

import org.jetbrains.annotations.NotNull;

import candybar.lib.R;
import candybar.lib.helpers.TypefaceHelper;
import candybar.lib.preferences.Preferences;
import codes.side.andcolorpicker.alpha.HSLAlphaColorPickerSeekBar;
import codes.side.andcolorpicker.group.PickerGroup;
import codes.side.andcolorpicker.model.IntegerHSLColor;
import codes.side.andcolorpicker.hsl.HSLColorPickerSeekBar;
import codes.side.andcolorpicker.view.picker.ColorSeekBar;

/*
 * CandyBar - Material Dashboard
 *
 * Copyright (c) 2014-2016 Dani Mahardhika
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class IconAppearanceFragment extends DialogFragment {

    private PickerGroup<IntegerHSLColor> colorPickerGroup = new PickerGroup<>();
    private ImageView iconPreview;
    private int currentColor;

    public static final String TAG = "candybar.dialog.icon.settings";

    private static IconAppearanceFragment newInstance() {
        return new IconAppearanceFragment();
    }

    public static void showIconAppearance(@NonNull FragmentManager fm) {
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag(TAG);
        if (prev != null) {
            ft.remove(prev);
        }

        try {
            DialogFragment dialog = IconAppearanceFragment.newInstance();
            dialog.show(ft, TAG);
        } catch (IllegalArgumentException | IllegalStateException ignored) {
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialDialog dialog = new MaterialDialog.Builder(requireActivity())
                .customView(R.layout.fragment_icon_appearance, false)
                .typeface(TypefaceHelper.getMedium(requireActivity()), TypefaceHelper.getRegular(requireActivity()))
                .title("Icon Appearance")
                .positiveText(R.string.save_setting)
                .onPositive((_dialog, which) -> {
                    Preferences.get(requireActivity()).setIconAppearanceColor(currentColor);
                    Toast.makeText(requireContext(), "Icon appearance changed successfully. Don't forget to click \"Apply\"", Toast.LENGTH_LONG);
                })
                .negativeText(R.string.close)
                .build();
        dialog.show();

        iconPreview = (ImageView) dialog.findViewById(R.id.iconSettingsPreviewImage);

        HSLColorPickerSeekBar hueSeekBar = (HSLColorPickerSeekBar) dialog.findViewById(R.id.iconSettingsHueSeekBar);
        HSLColorPickerSeekBar satSeekBar = (HSLColorPickerSeekBar) dialog.findViewById(R.id.iconSettingsSaturationSeekBar);
        HSLColorPickerSeekBar ligSeekBar = (HSLColorPickerSeekBar) dialog.findViewById(R.id.iconSettingsLightnessSeekBar);
        HSLAlphaColorPickerSeekBar opacSeekBar = (HSLAlphaColorPickerSeekBar) dialog.findViewById(R.id.iconSettingsOpacitySeekBar);

        colorPickerGroup.registerPicker(hueSeekBar);
        colorPickerGroup.registerPicker(satSeekBar);
        colorPickerGroup.registerPicker(ligSeekBar);
        colorPickerGroup.registerPicker(opacSeekBar);

        currentColor = Preferences.get(requireActivity()).getIconAppearanceColor();
        iconPreview.setColorFilter(currentColor, PorterDuff.Mode.SRC_IN);

        float[] hsl = new float[3];
        ColorUtils.colorToHSL(currentColor, hsl);


        IntegerHSLColor hslColor = new IntegerHSLColor();
        hslColor.setFloatH(hsl[0]);
        hslColor.setFloatS(hsl[1]);
        hslColor.setFloatL(hsl[2]);
        colorPickerGroup.setColor(hslColor);
        //hslColor.setIntA();

        this.colorPickerGroup.addListener(new ColorSeekBar.DefaultOnColorPickListener<ColorSeekBar<IntegerHSLColor>, IntegerHSLColor>() {
            @Override
            public void onColorChanged(@NotNull ColorSeekBar<IntegerHSLColor> picker, @NotNull IntegerHSLColor color, int value) {
                float[] hsl = new float[] {
                        color.getFloatH(),
                        color.getFloatS(),
                        color.getFloatL()
                };
                currentColor = ColorUtils.setAlphaComponent(ColorUtils.HSLToColor(hsl), color.getIntA());
                iconPreview.setColorFilter(ColorUtils.setAlphaComponent(ColorUtils.HSLToColor(hsl), color.getIntA()), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onColorPicked(@NotNull ColorSeekBar<IntegerHSLColor> picker, @NotNull IntegerHSLColor color, int value, boolean fromUser) {
                if(fromUser) {
                    float[] hsl = new float[] {
                            color.getFloatH(),
                            color.getFloatS(),
                            color.getFloatL()
                    };
                    currentColor = ColorUtils.setAlphaComponent(ColorUtils.HSLToColor(hsl), color.getIntA());
                    iconPreview.setColorFilter(ColorUtils.setAlphaComponent(ColorUtils.HSLToColor(hsl), color.getIntA()), PorterDuff.Mode.SRC_IN);
                }
            }
        });

        return dialog;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        //
        super.onDismiss(dialog);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PickerGroup<IntegerHSLColor> group = new PickerGroup<>();
        group.clearListeners();
    }
}
