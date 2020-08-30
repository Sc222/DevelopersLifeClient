package ru.sc222.devslife.ui.custom;

import androidx.fragment.app.Fragment;

public abstract class ControllableFragment extends Fragment {

    public abstract void fabNextClicked();

    public abstract void fabPreviousClicked();

    public abstract boolean isFabNextEnabled();

    public abstract boolean isFabPreviousEnabled();

    public abstract void setIsVisible(boolean isVisible);
}
