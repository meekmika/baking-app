package com.example.android.bakingtime.ui;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.android.bakingtime.R;
import com.example.android.bakingtime.adapters.RecipeStepPagerAdapter;
import com.example.android.bakingtime.data.model.Recipe;
import com.example.android.bakingtime.data.model.RecipeStep;

import java.util.List;

public class RecipeStepActivity extends AppCompatActivity {

    private List<RecipeStep> mRecipeSteps;
    private ViewPager mViewPager;
    private RecipeStepPagerAdapter mAdapter;
    private TextView mNavigationIndicatorTextView;
    private ImageButton mLeftButton, mRightButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_step);

        Recipe recipe = getIntent().getParcelableExtra(getString(R.string.recipe_key));
        mRecipeSteps = recipe.getSteps();
        int selectedStepIndex = getIntent().getIntExtra(getString(R.string.recipe_step_index_key), 0);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(recipe.getName());
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mViewPager = findViewById(R.id.recipe_step_view_pager);
        mLeftButton = findViewById(R.id.btn_left);
        mRightButton = findViewById(R.id.btn_right);
        mNavigationIndicatorTextView = findViewById(R.id.tv_indicator);

        mAdapter = new RecipeStepPagerAdapter(getSupportFragmentManager(), mRecipeSteps);

        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(selectedStepIndex);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int state) {
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                setNavigationIndicatorText(position);
                toggleArrowVisibility(position);
            }
        });
        setNavigationIndicatorText(selectedStepIndex);
        toggleArrowVisibility(selectedStepIndex);
    }

    public void onLeftClick(View view) {
        mViewPager.arrowScroll(ViewPager.FOCUS_LEFT);
    }

    public void onRightClick(View view) {
        mViewPager.arrowScroll(ViewPager.FOCUS_RIGHT);
    }

    private void setNavigationIndicatorText(int pos) {
        int currentIndex = mRecipeSteps.get(pos).getId();
        int lastIndex = mRecipeSteps.get(mRecipeSteps.size() - 1).getId();
        if (pos == 0) mNavigationIndicatorTextView.setText(getString(R.string.lets_bake));
        else
            mNavigationIndicatorTextView.setText(getString(R.string.pager_navigation_indicator, currentIndex, lastIndex));
    }

    private void toggleArrowVisibility(int pos) {
        boolean isAtZeroIndex = pos == 0;
        boolean isAtLastIndex = pos == mRecipeSteps.size() - 1;
        if (isAtZeroIndex) mLeftButton.setVisibility(View.INVISIBLE);
        else mLeftButton.setVisibility(View.VISIBLE);

        if (isAtLastIndex) mRightButton.setVisibility(View.INVISIBLE);
        else mRightButton.setVisibility(View.VISIBLE);
    }
}
