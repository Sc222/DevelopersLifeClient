package ru.sc222.devslife.utils;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.core.graphics.ColorUtils;
import androidx.palette.graphics.Palette;

import ru.sc222.devslife.custom.UiColorSet;

public class PaletteUtils {

    public static UiColorSet getColorsFromBitmap(Bitmap bitmap, final UiColorSet defaultColorSet) {
        UiColorSet result = defaultColorSet;
        Palette p = Palette.from(bitmap).generate();
        Palette.Swatch vibrantSwatch = p.getVibrantSwatch();
        if (vibrantSwatch != null) {
            Log.d("Palette", "Generated successfully");
            int bgColor = ColorUtils.setAlphaComponent(vibrantSwatch.getRgb(), 204);
            int titleColor = vibrantSwatch.getTitleTextColor();
            int subTitleColor = vibrantSwatch.getBodyTextColor();
            result = new UiColorSet(bgColor, titleColor, subTitleColor);
        } else
            Log.e("Error", "Palette Vibrant Swatch not available");
        return result;
    }
}
