package com.example.android.bakingtime.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.example.android.bakingtime.R;
import com.example.android.bakingtime.data.model.Recipe;

public class RecipeActivity extends AppCompatActivity {

    private static final String LOG_TAG = RecipeActivity.class.getSimpleName();

    private Recipe mRecipe;
    private boolean mTwoPane;

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

        // TODO: Implement tablet layout


    }
}
