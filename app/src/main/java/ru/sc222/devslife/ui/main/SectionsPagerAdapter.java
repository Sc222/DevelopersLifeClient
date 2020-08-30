package ru.sc222.devslife.ui.main;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import ru.sc222.devslife.R;
import ru.sc222.devslife.ui.custom.ControllableFragment;


public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_random, R.string.tab_latest, R.string.tab_best, R.string.tab_hot};
    private final Context mContext;
    private ControllableFragment mCurrentFragment;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mContext = context;
    }

    public ControllableFragment getCurrentFragment() {
        return mCurrentFragment;
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        if (getCurrentFragment() != object) {
            mCurrentFragment = ((ControllableFragment) object);
        }
        super.setPrimaryItem(container, position, object);
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            //all fragments must be children of ControllableFragment
            case 0:
                return new RandomFragment();
            default:
                return PlaceholderFragment.newInstance(position + 1);
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        return 4;
    }
}