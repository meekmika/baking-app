package com.example.android.bakingtime.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.example.android.bakingtime.R;
import com.example.android.bakingtime.data.model.Recipe;

import static com.example.android.bakingtime.ui.MainActivity.RECIPE_KEY;

public class RecipeActivity extends AppCompatActivity implements RecipeMasterListFragment.OnStepClickListener {

    public static final String RECIPE_STEP_INDEX_KEY = "recipe-step-index";
    private static final String LOG_TAG = RecipeActivity.class.getSimpleName();
    private Recipe mRecipe;
    private boolean mTwoPane;
    private RecipeStepFragment mRecipeStepFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        mRecipe = getIntent().getParcelableExtra(RECIPE_KEY);

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
            intent.putExtra(RECIPE_KEY, mRecipe);
            intent.putExtra(RECIPE_STEP_INDEX_KEY, index);
            startActivity(intent);
        }
    }
}
