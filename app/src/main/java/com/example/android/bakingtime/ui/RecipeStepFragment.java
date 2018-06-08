package com.example.android.bakingtime.ui;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakingtime.R;
import com.example.android.bakingtime.data.model.RecipeStep;

public class RecipeStepFragment extends Fragment {

    private static final String ARGS_RECIPE_STEP = "recipe-step";
    private RecipeStep mRecipeStep;
    private TextView mHeadingTextView;
    private TextView mBodyTextView;

    public RecipeStepFragment() {
    }

    public static RecipeStepFragment newInstance(RecipeStep recipeStep) {

        Bundle args = new Bundle();

        args.putParcelable(ARGS_RECIPE_STEP, recipeStep);
        RecipeStepFragment fragment = new RecipeStepFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mRecipeStep = args.getParcelable(ARGS_RECIPE_STEP);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_recipe_step, container, false);

        mHeadingTextView = rootView.findViewById(R.id.tv_recipe_step_heading);
        mBodyTextView = rootView.findViewById(R.id.tv_recipe_step_body);

        Log.d("YO", "Step: " + mRecipeStep.getShortDescription());
        if (mRecipeStep != null) {
            mHeadingTextView.setText(mRecipeStep.getShortDescription());
            mBodyTextView.setText(mRecipeStep.getDescription());
        }

        return rootView;
    }
}