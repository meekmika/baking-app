package com.example.android.bakingtime.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.bakingtime.R;
import com.example.android.bakingtime.data.model.Recipe;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private final RecipeAdapterOnClickHandler mOnClickHandler;
    private List<Recipe> mRecipes;
    private Context mContext;

    public RecipeAdapter(Context context, RecipeAdapterOnClickHandler onClickHandler) {
        mContext = context;
        mOnClickHandler = onClickHandler;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutIdForListItem = R.layout.recipe_list_item;
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        String recipeNameString = mRecipes.get(position).getName();
        int recipeServings = mRecipes.get(position).getServings();
        String recipeServingsString = mContext.getString(R.string.servings, recipeServings);
        holder.recipeNameTextView.setText(recipeNameString);
        holder.recipeServingsTextView.setText(recipeServingsString);
    }

    @Override
    public int getItemCount() {
        if (mRecipes == null) return 0;
        return mRecipes.size();
    }

    public void setRecipeData(List<Recipe> recipes) {
        mRecipes = recipes;
        notifyDataSetChanged();
    }

    public interface RecipeAdapterOnClickHandler {
        void onClick(Recipe selectedRecipe);
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView recipeNameTextView;
        final TextView recipeServingsTextView;
        final Button showRecipeButton;

        RecipeViewHolder(View view) {
            super(view);
            recipeNameTextView = view.findViewById(R.id.tv_recipe_name);
            recipeServingsTextView = view.findViewById(R.id.tv_recipe_servings);
            showRecipeButton = view.findViewById(R.id.btn_show_recipe);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mOnClickHandler.onClick(mRecipes.get(adapterPosition));
        }
    }
}
