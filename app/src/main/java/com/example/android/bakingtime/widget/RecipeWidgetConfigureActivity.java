package com.example.android.bakingtime.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.android.bakingtime.R;
import com.example.android.bakingtime.data.model.Recipe;
import com.example.android.bakingtime.data.remote.RecipeService;
import com.example.android.bakingtime.utils.ApiUtils;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * The configuration screen for the {@link RecipeWidget RecipeWidget} AppWidget.
 */
public class RecipeWidgetConfigureActivity extends Activity {

    private static final String LOG_TAG = RecipeWidgetConfigureActivity.class.getSimpleName();
    private static final String PREFS_NAME = "com.example.android.bakingtime.widget.RecipeWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    private RecipeService mService;
    private List<Recipe> mRecipes;
    private RadioGroup mRadioGroup;

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = RecipeWidgetConfigureActivity.this;

            // When the button is clicked, store the string locally
            Recipe selectedRecipe = mRecipes.get(mRadioGroup.getCheckedRadioButtonId());
            saveRecipePref(context, mAppWidgetId, selectedRecipe);

            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            RecipeWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    public RecipeWidgetConfigureActivity() {
        super();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveRecipePref(Context context, int appWidgetId, Recipe recipe) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();

        Gson gson = new Gson();
        String recipeJson = gson.toJson(recipe);

        prefs.putString(PREF_PREFIX_KEY + appWidgetId, recipeJson);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    public static Recipe loadRecipePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String recipeJson = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        if (recipeJson != null) {
            Gson gson = new Gson();
            return gson.fromJson(recipeJson, Recipe.class);
        } else {
            return null;
        }
    }

    static void deleteRecipePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.recipe_widget_configure);

        mService = ApiUtils.getRecipeService();
        mRadioGroup = findViewById(R.id.radio_selectable_recipes);
        loadRecipes();

        final Button mButton = findViewById(R.id.add_button);
        mButton.setOnClickListener(mOnClickListener);

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mButton.setEnabled(true);
            }
        });


        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        //mAppWidgetText.setText(loadRecipePref(RecipeWidgetConfigureActivity.this, mAppWidgetId));
    }

    private void loadRecipes() {
        if (ApiUtils.isOnline(this)) {

            mService.getRecipes().enqueue(new Callback<List<Recipe>>() {
                @Override
                public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                    if (response.isSuccessful()) {
                        mRecipes = response.body();
                        addRadioButtons();
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

    private void addRadioButtons() {
        for (int i = 0; i < mRecipes.size(); i++) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setId(i);
            radioButton.setText(mRecipes.get(i).getName());
            mRadioGroup.addView(radioButton);
        }
    }
}

