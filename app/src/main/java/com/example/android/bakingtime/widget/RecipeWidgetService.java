package com.example.android.bakingtime.widget;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.bakingtime.R;
import com.example.android.bakingtime.data.model.Ingredient;
import com.example.android.bakingtime.data.model.Recipe;

import java.util.List;

public class RecipeWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Context context = this.getApplicationContext();
        int recipeWidgetId = Integer.valueOf(intent.getData().getSchemeSpecificPart());
        Recipe recipe = RecipeWidgetConfigureActivity.loadRecipePref(context, recipeWidgetId);
        return new RecipeWidgetRemoteViewsFactory(this.getApplicationContext(), recipe.getIngredients());
    }

    class RecipeWidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

        Context mContext;
        List<Ingredient> mIngredients;


        RecipeWidgetRemoteViewsFactory(Context applicationContext, List<Ingredient> ingredients) {
            mContext = applicationContext;
            mIngredients = ingredients;
        }

        @Override
        public void onCreate() {
        }

        @Override
        public void onDataSetChanged() {
        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return mIngredients == null ? 0 : mIngredients.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            Ingredient ingredient = mIngredients.get(position);
            RemoteViews remoteView = new RemoteViews(getPackageName(), R.layout.ingredient_list_item);
            remoteView.setTextViewText(R.id.tv_ingredient_name, ingredient.getIngredient());
            remoteView.setTextViewText(R.id.tv_ingredient_quantity, String.valueOf(ingredient.getQuantity()));
            remoteView.setTextViewText(R.id.tv_ingredient_measure, ingredient.getMeasure());
            return remoteView;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
    }
}
