package com.example.android.bakingtime.adapters;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.example.android.bakingtime.data.model.RecipeStep;
import com.example.android.bakingtime.ui.RecipeStepFragment;

import java.util.List;

public class RecipeStepPagerAdapter extends FragmentPagerAdapter {

    SparseArray<Fragment> registeredFragments = new SparseArray<>();

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

    @NonNull
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }
}
