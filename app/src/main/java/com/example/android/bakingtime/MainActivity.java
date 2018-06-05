package com.example.android.bakingtime;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.example.android.bakingtime.data.model.Recipe;
import com.example.android.bakingtime.data.remote.RecipeService;
import com.example.android.bakingtime.utils.ApiUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private RecipeService mService;
    private List<Recipe> mRecipes;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mService = ApiUtils.getRecipeService();

        mTextView = findViewById(R.id.text_view);

        loadRecipes();
    }

    private void loadRecipes() {
        if (ApiUtils.isOnline(this)) {
            mService.getRecipes().enqueue(new Callback<List<Recipe>>() {
                @Override
                public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                    if (response.isSuccessful()) {
                        mRecipes = response.body();
                        setTextViewText();
                        Log.d(LOG_TAG, "recipes loaded from API");
                    } else {
                        int statusCode = response.code();
                        // handle request errors depending on status code
                        Log.v(LOG_TAG, "Request error. Status code: " + statusCode);
                    }
                }

                @Override
                public void onFailure(Call<List<Recipe>> call, Throwable t) {
                    Log.d(LOG_TAG, "error loading from API " + t.getMessage());
                }
            });
        } else {
            Log.d(LOG_TAG, "Not online");
        }
    }

    private void setTextViewText() {
        StringBuilder text = new StringBuilder();
        text.append("Recipes:");
        for (Recipe recipe : mRecipes) {
            text.append("\n");
            text.append(recipe.getName());
        }
        mTextView.setText(text.toString());
    }
}
