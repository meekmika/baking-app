package com.example.android.bakingtime.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.example.android.bakingtime.R;
import com.example.android.bakingtime.data.model.Recipe;

import static com.example.android.bakingtime.ui.MainActivity.EXTRA_RECIPE;

public class RecipeActivity extends AppCompatActivity implements RecipeMasterListFragment.OnStepClickListener {

    private static final String LOG_TAG = RecipeActivity.class.getSimpleName();
    public static final String EXTRA_RECIPE_STEP_INDEX = "recipe-step-index";
    private Recipe mRecipe;
    private boolean mTwoPane;
    private RecipeStepFragment mRecipeStepFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        mRecipe = getIntent().getParcelableExtra(EXTRA_RECIPE);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle(mRecipe.getName());
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mTwoPane = getResources().getBoolean(R.bool.isTablet);

        if (mTwoPane && savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            mRecipeStepFragment = RecipeStepFragment.newInstance(mRecipe, 0);
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
            intent.putExtra(EXTRA_RECIPE, mRecipe);
            intent.putExtra(EXTRA_RECIPE_STEP_INDEX, index);
            startActivity(intent);
        }
    }
}
