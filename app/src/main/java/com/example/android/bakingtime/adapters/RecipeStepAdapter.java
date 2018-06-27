package com.example.android.bakingtime.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakingtime.R;
import com.example.android.bakingtime.data.model.RecipeStep;

import java.util.List;

public class RecipeStepAdapter extends RecyclerView.Adapter<RecipeStepAdapter.RecipeStepViewHolder> {

    private List<RecipeStep> mSteps;
    private RecipeAdapterOnClickHandler mOnClickHandler;

    public RecipeStepAdapter(List<RecipeStep> steps) {
        mSteps = steps;
    }

    @Override
    public int getItemCount() {
        if (mSteps == null) return 0;
        return mSteps.size();
    }

    public void setOnClickHandler(RecipeAdapterOnClickHandler handler) {
        mOnClickHandler = handler;
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeStepViewHolder holder, int position) {
        String stepIndex = String.valueOf(mSteps.get(position).getId());
        String stepDescription = mSteps.get(position).getShortDescription();

        holder.stepIndexTextView.setText(String.valueOf(stepIndex));
        holder.stepDescriptionTextView.setText(stepDescription);
    }

    @NonNull
    @Override
    public RecipeStepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutIdForListItem = R.layout.recipe_step_list_item;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new RecipeStepViewHolder(view);
    }

    public interface RecipeAdapterOnClickHandler {
        void onClick(int index);
    }

    class RecipeStepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView stepIndexTextView;
        final TextView stepDescriptionTextView;

        RecipeStepViewHolder(View view) {
            super(view);
            stepIndexTextView = view.findViewById(R.id.tv_step_index);
            stepDescriptionTextView = view.findViewById(R.id.tv_step_description);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnClickHandler != null) {
                mOnClickHandler.onClick(getAdapterPosition());
            }
        }
    }
}
