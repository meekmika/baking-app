package com.example.android.bakingtime.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.example.android.bakingtime.R;
import com.example.android.bakingtime.data.model.Recipe;

import static com.example.android.bakingtime.ui.MainActivity.RECIPE_KEY;
import static com.example.android.bakingtime.ui.RecipeActivity.RECIPE_STEP_INDEX_KEY;

public class RecipeStepActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_step);

        Recipe recipe = getIntent().getParcelableExtra(RECIPE_KEY);
        int selectedStepIndex = getIntent().getIntExtra(RECIPE_STEP_INDEX_KEY, 0);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(recipe.getName());
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            RecipeStepFragment recipeStepFragment = RecipeStepFragment.newInstance(recipe, selectedStepIndex);
            FragmentManager fragmentManager = getSupportFragmentManager();

            fragmentManager.beginTransaction()
                    .replace(R.id.recipe_step_fragment, recipeStepFragment)
                    .commit();
        }
    }
}
