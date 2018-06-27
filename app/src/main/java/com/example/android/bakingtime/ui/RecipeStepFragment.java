package com.example.android.bakingtime.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatImageButton;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.bakingtime.R;
import com.example.android.bakingtime.adapters.RecipeStepPagerAdapter;
import com.example.android.bakingtime.data.model.Recipe;
import com.example.android.bakingtime.data.model.RecipeStep;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class RecipeStepFragment extends Fragment {
    private Recipe mRecipe;
    private int mSelectedStepIndex;
    private ViewPager mViewPager;
    private AppCompatImageButton mLeftButton;
    private AppCompatImageButton mRightButton;
    private TextView mNavigationIndicatorTextView;
    private RecipeStepPagerAdapter mAdapter;
    private PlayerView mPlayerView;
    private SimpleExoPlayer mPlayer;
    private ImageView mPlaceholder;

    private RecipeStep mCurrentStep;
    private Dialog mFullscreenDialog;
    private boolean mPlayerFullscreen;
    private FrameLayout mVideoContainer;
    private ImageButton mFullscreenButton;

    public RecipeStepFragment() {
    }

    public static RecipeStepFragment newInstance(Context context, Recipe recipe, int selectedStepIndex) {

        Bundle args = new Bundle();
        args.putParcelable(context.getString(R.string.recipe_key), recipe);
        args.putInt(context.getString(R.string.recipe_step_index_key), selectedStepIndex);

        RecipeStepFragment fragment = new RecipeStepFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mRecipe = args.getParcelable(getString(R.string.recipe_key));
        mSelectedStepIndex = args.getInt(getString(R.string.recipe_step_index_key));
        mCurrentStep = mRecipe.getSteps().get(mSelectedStepIndex);

        OrientationEventListener orientationEventListener =
                new OrientationEventListener(getActivity()) {
                    @Override
                    public void onOrientationChanged(int orientation) {
                        int epsilon = 10;
                        int leftLandscape = 90;
                        int rightLandscape = 270;
                        if (epsilonCheck(orientation, leftLandscape, epsilon) ||
                                epsilonCheck(orientation, rightLandscape, epsilon)) {
                            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                            openFullscreenDialog();
                        } else {
                            closeFullscreenDialog();
                        }
                    }

                    private boolean epsilonCheck(int a, int b, int epsilon) {
                        return a > b - epsilon && a < b + epsilon;
                    }
                };
        if (!getResources().getBoolean(R.bool.isTablet)) orientationEventListener.enable();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_recipe_step, container, false);

        mViewPager = rootView.findViewById(R.id.recipe_step_view_pager);
        mLeftButton = rootView.findViewById(R.id.btn_left);
        mRightButton = rootView.findViewById(R.id.btn_right);
        mNavigationIndicatorTextView = rootView.findViewById(R.id.tv_indicator);

        mAdapter = new RecipeStepPagerAdapter(getChildFragmentManager(), mRecipe.getSteps());

        mPlayerView = rootView.findViewById(R.id.recipe_step_video_view);
        mPlaceholder = rootView.findViewById(R.id.video_placeholder);

        toggleArrowVisibility(mSelectedStepIndex);
        setNavigationIndicatorText(mSelectedStepIndex);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int state) {
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                setStep(position);
            }
        });

        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(mSelectedStepIndex);

        mVideoContainer = rootView.findViewById(R.id.video_container);

        mFullscreenDialog = new Dialog(getActivity(), android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
            public void onBackPressed() {
                if (mPlayerFullscreen) closeFullscreenDialog();
                super.onBackPressed();
            }
        };

        mFullscreenButton = rootView.findViewById(R.id.exo_fullscreen);
        mFullscreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mPlayerFullscreen) openFullscreenDialog();
                else closeFullscreenDialog();
            }
        });

        mLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.arrowScroll(ViewPager.FOCUS_LEFT);
            }
        });

        mRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.arrowScroll(ViewPager.FOCUS_RIGHT);
            }
        });

        return rootView;
    }

    public void setStep(int stepIndex) {
        setNavigationIndicatorText(stepIndex);
        toggleArrowVisibility(stepIndex);
        mCurrentStep = mRecipe.getSteps().get(stepIndex);
        setVideoSource();
        if (mViewPager.getCurrentItem() != stepIndex) mViewPager.setCurrentItem(stepIndex);
    }

    @Override
    public void onResume() {
        super.onResume();
        initPlayer();
    }

    @Override
    public void onPause() {
        super.onPause();
        releasePlayer();
    }

    private void initPlayer() {
        if (mPlayer == null) {
            mPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), new DefaultTrackSelector());
        }

        setVideoSource();
        mPlayerView.setPlayer(mPlayer);
    }

    private void releasePlayer() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    private void setVideoSource() {
        String videoUrl = mCurrentStep.getVideoURL();

        if (videoUrl.isEmpty()) {
            useImageInsteadOfVideo();
        } else {
            String userAgent = Util.getUserAgent(getActivity(), getString(R.string.app_name));
            HttpDataSource.Factory dataSourceFactory = new DefaultHttpDataSourceFactory(userAgent);
            MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(videoUrl));
            mPlayer.prepare(videoSource);
            mPlayerView.setVisibility(VISIBLE);
            mPlaceholder.setVisibility(INVISIBLE);
        }
    }


    private void useImageInsteadOfVideo() {
        mPlayerView.setVisibility(INVISIBLE);
        mPlaceholder.setVisibility(VISIBLE);
        String thumbnailUrl = mCurrentStep.getThumbnailURL();
        Picasso.Builder builder = new Picasso.Builder(getActivity());
        builder.listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                mPlaceholder.setImageResource(R.drawable.video_placeholder);
            }
        });
        builder.build().load(Uri.parse(thumbnailUrl)).placeholder(R.drawable.video_placeholder).into(mPlaceholder);
    }

    private void openFullscreenDialog() {
        ((ViewGroup) mPlayerView.getParent()).removeView(mPlayerView);
        mFullscreenDialog.addContentView(mPlayerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mFullscreenButton.setImageResource(R.drawable.ic_fullscreen_exit_24dp);
        mPlayerFullscreen = true;
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        mFullscreenDialog.show();
    }

    private void closeFullscreenDialog() {
        ((ViewGroup) mPlayerView.getParent()).removeView(mPlayerView);
        mVideoContainer.addView(mPlayerView);
        mPlayerFullscreen = false;
        mFullscreenDialog.dismiss();
        mFullscreenButton.setImageResource(R.drawable.ic_fullscreen_24dp);
        if (!getResources().getBoolean(R.bool.isTablet))
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void setNavigationIndicatorText(int pos) {
        int currentIndex = mRecipe.getSteps().get(pos).getId();
        int lastIndex = mRecipe.getSteps().get(mRecipe.getSteps().size() - 1).getId();
        if (pos == 0) mNavigationIndicatorTextView.setText(getString(R.string.lets_bake));
        else
            mNavigationIndicatorTextView.setText(getString(R.string.pager_navigation_indicator, currentIndex, lastIndex));
    }

    private void toggleArrowVisibility(int pos) {
        boolean isAtZeroIndex = pos == 0;
        boolean isAtLastIndex = pos == mRecipe.getSteps().size() - 1;
        mLeftButton.setVisibility(isAtZeroIndex ? INVISIBLE : VISIBLE);
        mRightButton.setVisibility(isAtLastIndex ? INVISIBLE : VISIBLE);
    }
}
