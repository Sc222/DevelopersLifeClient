package ru.sc222.devslife.ui.activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import ru.sc222.devslife.R;
import ru.sc222.devslife.ui.adapters.SectionsPagerAdapter;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private SectionsPagerAdapter sectionsPagerAdapter;
    private FloatingActionButton fabNext;
    private FloatingActionButton fabPrevious;

    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            fabNext.setEnabled(sectionsPagerAdapter.getCurrentFragment().isFabNextEnabled());
            fabPrevious.setEnabled(sectionsPagerAdapter.getCurrentFragment().isFabPreviousEnabled());
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // todo update buttons between entries

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.addOnPageChangeListener(onPageChangeListener);
        TabLayout tabs = findViewById(R.id.tabs);


        tabs.setupWithViewPager(viewPager);

        fabNext = findViewById(R.id.fab_next);
        fabNext.setOnClickListener(this);
        fabPrevious = findViewById(R.id.fab_previous);
        fabPrevious.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_next:
                sectionsPagerAdapter.getCurrentFragment().fabNextClicked();
                break;
            case R.id.fab_previous:
                sectionsPagerAdapter.getCurrentFragment().fabPreviousClicked();
                break;
        }
    }
}