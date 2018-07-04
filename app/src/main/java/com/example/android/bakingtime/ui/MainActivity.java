package com.example.android.bakingtime.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.android.bakingtime.R;
import com.example.android.bakingtime.adapters.RecipeAdapter;
import com.example.android.bakingtime.data.model.Recipe;
import com.example.android.bakingtime.data.remote.RecipeService;
import com.example.android.bakingtime.idlingResource.SimpleIdlingResource;
import com.example.android.bakingtime.utils.ApiUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements RecipeAdapter.RecipeAdapterOnClickHandler {

    public static final String EXTRA_RECIPE = "recipe-key";

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String RECIPES = "recipes";
    private static final int COLUMN_WIDTH = 600;
    private static final int MIN_COLUMNS = 1;

    private RecipeService mService;
    private ArrayList<Recipe> mRecipes;
    private LinearLayout mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;
    private RecipeAdapter mRecipeAdapter;
    private RecyclerView mRecipeRecyclerView;

    @Nullable
    private SimpleIdlingResource mIdlingResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mService = ApiUtils.getRecipeService();

        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);
        mErrorMessageDisplay = findViewById(R.id.error_message_display);

        mRecipeAdapter = new RecipeAdapter(this, this);
        mRecipeRecyclerView = findViewById(R.id.rv_recipes);
        int numColumns = numberOfColumns();
        mRecipeRecyclerView.setLayoutManager(new GridLayoutManager(this, numColumns));
        mRecipeRecyclerView.setAdapter(mRecipeAdapter);

        getIdlingResource();
        if (savedInstanceState != null) {
            mRecipes = savedInstanceState.getParcelableArrayList(RECIPES);
            showRecipes();
        } else loadRecipes();
    }

    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        return width / COLUMN_WIDTH;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mRecipes != null) {
            outState.putParcelableArrayList(RECIPES, mRecipes);
        }
    }

    private void loadRecipes() {
        if (ApiUtils.isOnline(this)) {
            mLoadingIndicator.setVisibility(View.VISIBLE);

            if (mIdlingResource != null) {
                mIdlingResource.setIdleState(false);
            }
            mService.getRecipes().enqueue(new Callback<List<Recipe>>() {
                @Override
                public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                    mLoadingIndicator.setVisibility(View.INVISIBLE);
                    if (response.isSuccessful()) {
                        mRecipes = (ArrayList<Recipe>) response.body();
                        showRecipes();
                        Log.d(LOG_TAG, "recipes loaded from API");
                    } else {
                        int statusCode = response.code();
                        // handle request errors depending on status code
                        Log.v(LOG_TAG, "Request error. Status code: " + statusCode);
                        showErrorMessage();
                    }

                    if (mIdlingResource != null) {
                        mIdlingResource.setIdleState(true);
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
        mRecipeAdapter.setRecipeData(mRecipes);
        mRecipeRecyclerView.setVisibility(View.VISIBLE);
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
    }

    private void showErrorMessage() {
        mRecipeRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(Recipe selectedRecipe) {
        final Intent intentToStartRecipeActivity =
                new Intent(this, RecipeActivity.class);
        intentToStartRecipeActivity.putExtra(EXTRA_RECIPE, selectedRecipe);
        startActivity(intentToStartRecipeActivity);
    }

    public List<Recipe> getRecipes() {
        return mRecipes;
    }

    /**
     * Only called from test, creates and returns a new {@link SimpleIdlingResource}.
     */
    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }
}
