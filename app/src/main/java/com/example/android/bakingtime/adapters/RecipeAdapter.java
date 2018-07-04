package com.example.android.bakingtime.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.bakingtime.R;
import com.example.android.bakingtime.data.model.Recipe;
import com.example.android.bakingtime.data.model.RecipeStep;

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
        setBackgroundImage(holder, position);
        //new BackgroundImageTask().execute(new Pair<>(holder, position));
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

    private void setBackgroundImage(@NonNull RecipeViewHolder holder, int position) {
        List<RecipeStep> recipeSteps = mRecipes.get(position).getSteps();
        for (int i = recipeSteps.size() - 1; i > 0; i--) {
            String url = recipeSteps.get(i).getVideoURL();
            if (!url.isEmpty()) {
                Uri uri = Uri.parse(url);
                Glide.with(mContext)
                        .load(uri)
                        .thumbnail(0.1f)
                        .into(holder.backgroundImageView);
                return;
            }
        }
        holder.backgroundImageView.setImageResource(R.drawable.video_placeholder);
    }

    public interface RecipeAdapterOnClickHandler {
        void onClick(Recipe selectedRecipe);
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView backgroundImageView;
        final TextView recipeNameTextView;
        final TextView recipeServingsTextView;

        RecipeViewHolder(View view) {
            super(view);
            backgroundImageView = view.findViewById(R.id.iv_background);
            recipeNameTextView = view.findViewById(R.id.tv_recipe_name);
            recipeServingsTextView = view.findViewById(R.id.tv_recipe_servings);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mOnClickHandler.onClick(mRecipes.get(adapterPosition));
        }
    }
}
