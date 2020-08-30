package ru.sc222.devslife.ui.activities;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import ru.sc222.devslife.R;
import ru.sc222.devslife.ui.adapters.SectionsPagerAdapter;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private SectionsPagerAdapter sectionsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fabNext = findViewById(R.id.fab_next);
        fabNext.setOnClickListener(this);

        FloatingActionButton fabPrevious = findViewById(R.id.fab_previous);
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