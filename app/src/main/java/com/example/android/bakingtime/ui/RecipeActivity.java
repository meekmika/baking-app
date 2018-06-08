package com.example.android.bakingtime.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.example.android.bakingtime.R;
import com.example.android.bakingtime.data.model.Recipe;

public class RecipeActivity extends AppCompatActivity implements MasterListFragment.OnStepClickListener {

    private static final String LOG_TAG = RecipeActivity.class.getSimpleName();

    private Recipe mRecipe;
    private boolean mTwoPane = false;

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

    }

    @Override
    public void onStepSelected(int index) {
        if (mTwoPane) {
            // TODO: Implement tablet layout
        } else {
            final Intent intent = new Intent(this, RecipeStepActivity.class);
            intent.putExtra(getString(R.string.recipe_key), mRecipe);
            intent.putExtra(getString(R.string.recipe_step_index_key), index);
            startActivity(intent);
        }
    }
}
