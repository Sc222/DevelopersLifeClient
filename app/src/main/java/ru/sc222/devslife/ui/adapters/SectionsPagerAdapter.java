package ru.sc222.devslife.ui.adapters;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import ru.sc222.devslife.R;
import ru.sc222.devslife.ui.custom.ControllableFragment;
import ru.sc222.devslife.ui.fragments.NewsfeedFragment;
import ru.sc222.devslife.ui.fragments.RandomFragment;


public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_random, R.string.tab_latest, R.string.tab_top, R.string.tab_hot};

    private static final int NEWSFEED_FIRST_INDEX = 1;
    private static final String[] NEWSFEED_TYPES = new String[]{
            NewsfeedFragment.NEWSFEED_TYPE_LATEST,
            NewsfeedFragment.NEWSFEED_TYPE_TOP,
            NewsfeedFragment.NEWSFEED_TYPE_HOT
    };
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
            ControllableFragment mPrevSelectedFragment = mCurrentFragment;
            if (mPrevSelectedFragment != null)
                mPrevSelectedFragment.setIsVisible(false);
            mCurrentFragment = ((ControllableFragment) object);
            mCurrentFragment.setIsVisible(true);
        }
        super.setPrimaryItem(container, position, object);
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        Log.e("get item", "position: " + position);
        switch (position) {
            //all fragments must be children of ControllableFragment
            case 0:
                return new RandomFragment();
            default:
                return NewsfeedFragment.newInstance(NEWSFEED_TYPES[position - NEWSFEED_FIRST_INDEX]);
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