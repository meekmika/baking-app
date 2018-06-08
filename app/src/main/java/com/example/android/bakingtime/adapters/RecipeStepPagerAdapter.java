package com.example.android.bakingtime.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.android.bakingtime.data.model.RecipeStep;
import com.example.android.bakingtime.ui.RecipeStepFragment;

import java.util.List;

public class RecipeStepPagerAdapter extends FragmentPagerAdapter {

    private List<RecipeStep> mRecipeSteps;

    public RecipeStepPagerAdapter(FragmentManager fragmentManager, List<RecipeStep> recipeSteps) {
        super(fragmentManager);
        mRecipeSteps = recipeSteps;
    }

    @Override
    public Fragment getItem(int position) {
        return RecipeStepFragment.newInstance(mRecipeSteps.get(position));
    }

    @Override
    public int getCount() {
        if (mRecipeSteps == null) return 0;
        return mRecipeSteps.size();
    }
}
