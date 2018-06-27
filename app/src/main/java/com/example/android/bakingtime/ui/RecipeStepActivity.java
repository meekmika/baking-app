package com.example.android.bakingtime.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.example.android.bakingtime.R;
import com.example.android.bakingtime.data.model.Recipe;

public class RecipeStepActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_step);

        Recipe recipe = getIntent().getParcelableExtra(getString(R.string.recipe_key));
        int selectedStepIndex = getIntent().getIntExtra(getString(R.string.recipe_step_index_key), 0);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(recipe.getName());
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        RecipeStepFragment recipeStepFragment = RecipeStepFragment.newInstance(this, recipe, selectedStepIndex);
        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .replace(R.id.recipe_step_fragment, recipeStepFragment)
                .commit();
    }

}
