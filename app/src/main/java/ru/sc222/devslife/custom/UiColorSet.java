package ru.sc222.devslife.custom;

import android.content.Context;

import androidx.core.content.ContextCompat;

import ru.sc222.devslife.R;

public class UiColorSet {

    private final int bgColor;
    private final int titleColor;
    private final int subtitleColor;

    public UiColorSet(int bgColor, int titleColor, int subtitleColor) {
        this.bgColor = bgColor;
        this.titleColor = titleColor;
        this.subtitleColor = subtitleColor;
    }

    public UiColorSet(Context context) {
        this.bgColor = ContextCompat.getColor(context, R.color.default_bg_color);
        this.titleColor = ContextCompat.getColor(context, R.color.default_title_color);
        this.subtitleColor = ContextCompat.getColor(context, R.color.default_subtitle_color);
    }

    public int getBgColor() {
        return bgColor;
    }

    public int getTitleColor() {
        return titleColor;
    }

    public int getSubtitleColor() {
        return subtitleColor;
    }
}
