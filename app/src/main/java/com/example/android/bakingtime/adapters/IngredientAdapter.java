package com.example.android.bakingtime.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakingtime.R;
import com.example.android.bakingtime.data.model.Ingredient;

import java.text.DecimalFormat;
import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {

    private List<Ingredient> mIngredients;

    public IngredientAdapter(List<Ingredient> ingredients) {
        mIngredients = ingredients;
    }

    @Override
    public int getItemCount() {
        if (mIngredients == null) return 0;
        return mIngredients.size();
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutIdForListItem = R.layout.ingredient_list_item;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        String ingredientName = mIngredients.get(position).getIngredient();
        double quantity = mIngredients.get(position).getQuantity();
        DecimalFormat df = new DecimalFormat("0.#");
        String ingredientQuantity = df.format(quantity);
        String ingredientMeasure = mIngredients.get(position).getMeasure();
        ingredientMeasure = ingredientMeasure.trim();

        holder.ingredientNameTextView.setText(ingredientName);
        holder.ingredientQuantityTextView.setText(ingredientQuantity);
        holder.ingredientMeasureTextView.setText(ingredientMeasure);
    }


    class IngredientViewHolder extends RecyclerView.ViewHolder {

        final TextView ingredientNameTextView;
        final TextView ingredientQuantityTextView;
        final TextView ingredientMeasureTextView;

        IngredientViewHolder(View view) {
            super(view);
            ingredientNameTextView = view.findViewById(R.id.tv_ingredient_name);
            ingredientQuantityTextView = view.findViewById(R.id.tv_ingredient_quantity);
            ingredientMeasureTextView = view.findViewById(R.id.tv_ingredient_measure);
        }
    }
}
