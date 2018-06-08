package com.example.android.bakingtime.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.bakingtime.R;
import com.example.android.bakingtime.adapters.IngredientAdapter;
import com.example.android.bakingtime.adapters.RecipeStepAdapter;
import com.example.android.bakingtime.data.model.Recipe;

public class MasterListFragment extends Fragment {

    private OnStepClickListener mCallback;
    private Recipe mRecipe;

    public MasterListFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallback = (OnStepClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnStepClickListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mRecipe = getActivity().getIntent().getParcelableExtra(getString(R.string.recipe_key));

        final View rootView = inflater.inflate(R.layout.fragment_master_list, container, false);

        if (mRecipe != null) setupView(rootView);

        return rootView;
    }

    private void setupView(View rootView) {
        RecyclerView ingredientsRecyclerView = rootView.findViewById(R.id.rv_ingredients);
        IngredientAdapter ingredientAdapter = new IngredientAdapter(mRecipe.getIngredients());
        ingredientsRecyclerView.setAdapter(ingredientAdapter);
        ingredientsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        RecyclerView stepsRecyclerView = rootView.findViewById(R.id.rv_steps);
        RecipeStepAdapter stepAdapter = new RecipeStepAdapter(mRecipe.getSteps());
        stepsRecyclerView.setAdapter(stepAdapter);
        stepsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        DividerItemDecoration decoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        ingredientsRecyclerView.addItemDecoration(decoration);
        stepsRecyclerView.addItemDecoration(decoration);

        stepAdapter.setOnClickHandler(new RecipeStepAdapter.RecipeAdapterOnClickHandler() {
            @Override
            public void onClick(int selectedStepIndex) {
                mCallback.onStepSelected(selectedStepIndex);
            }
        });

        ViewCompat.setNestedScrollingEnabled(ingredientsRecyclerView, false);
        ViewCompat.setNestedScrollingEnabled(stepsRecyclerView, false);
    }

    public interface OnStepClickListener {
        void onStepSelected(int index);
    }


}
