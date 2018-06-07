package com.example.android.bakingtime.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.android.bakingtime.R;
import com.example.android.bakingtime.adapters.RecipeAdapter;
import com.example.android.bakingtime.data.model.Recipe;
import com.example.android.bakingtime.data.remote.RecipeService;
import com.example.android.bakingtime.utils.ApiUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements RecipeAdapter.RecipeAdapterOnClickHandler {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private RecipeService mService;
    private List<Recipe> mRecipes;
    private LinearLayout mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;
    private RecipeAdapter mRecipeAdapter;
    private RecyclerView mRecipeRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mService = ApiUtils.getRecipeService();

        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);
        mErrorMessageDisplay = findViewById(R.id.error_message_display);

        mRecipeAdapter = new RecipeAdapter(this, this);
        mRecipeRecyclerView = findViewById(R.id.rv_recipes);
        mRecipeRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecipeRecyclerView.setAdapter(mRecipeAdapter);

        loadRecipes();
    }

    private void loadRecipes() {
        if (ApiUtils.isOnline(this)) {
            mLoadingIndicator.setVisibility(View.VISIBLE);
            mService.getRecipes().enqueue(new Callback<List<Recipe>>() {
                @Override
                public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                    mLoadingIndicator.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        mRecipes = response.body();
                        mRecipeAdapter.setRecipeData(mRecipes);
                        showRecipes();
                        Log.d(LOG_TAG, "recipes loaded from API");
                    } else {
                        int statusCode = response.code();
                        // handle request errors depending on status code
                        Log.v(LOG_TAG, "Request error. Status code: " + statusCode);
                        showErrorMessage();
                    }
                }

                @Override
                public void onFailure(Call<List<Recipe>> call, Throwable t) {
                    Log.d(LOG_TAG, "error loading from API " + t.getMessage());
                }
            });
        } else {
            Log.d(LOG_TAG, "Not online");
            showErrorMessage();
        }
    }

    private void showRecipes() {
        mRecipeRecyclerView.setVisibility(View.VISIBLE);
        mErrorMessageDisplay.setVisibility(View.GONE);
    }

    private void showErrorMessage() {
        mRecipeRecyclerView.setVisibility(View.GONE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(Recipe selectedRecipe) {
        Log.d(LOG_TAG, "Clicked recipe: " + selectedRecipe.getName());

        final Intent intentToStartRecipeActivity = new Intent(this, RecipeActivity.class);
        intentToStartRecipeActivity.putExtra(getString(R.string.recipe_key), selectedRecipe);
        startActivity(intentToStartRecipeActivity);
    }
}
