package com.example.android.bakingtime.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.example.android.bakingtime.R;
import com.example.android.bakingtime.data.model.Recipe;

public class RecipeActivity extends AppCompatActivity implements RecipeMasterListFragment.OnStepClickListener {

    private static final String LOG_TAG = RecipeActivity.class.getSimpleName();

    private Recipe mRecipe;
    private boolean mTwoPane;
    private RecipeStepFragment mRecipeStepFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        mRecipe = getIntent().getParcelableExtra(getString(R.string.recipe_key));

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle(mRecipe.getName());
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mTwoPane = getResources().getBoolean(R.bool.isTablet);

        if (mTwoPane) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            mRecipeStepFragment = RecipeStepFragment.newInstance(this, mRecipe, 0);
            fragmentManager.beginTransaction()
                    .replace(R.id.recipe_step_details, mRecipeStepFragment)
                    .commit();
        }

    }

    @Override
    public void onStepSelected(int index) {
        if (mTwoPane) {
            mRecipeStepFragment.setStep(index);
        } else {
            final Intent intent = new Intent(this, RecipeStepActivity.class);
            intent.putExtra(getString(R.string.recipe_key), mRecipe);
            intent.putExtra(getString(R.string.recipe_step_index_key), index);
            startActivity(intent);
        }
    }
}
