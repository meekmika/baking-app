package com.example.android.bakingtime.ui;

import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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

import java.util.List;

public class RecipeStepActivity extends AppCompatActivity {

    private Recipe mRecipe;
    private List<RecipeStep> mRecipeSteps;
    private ViewPager mViewPager;
    private RecipeStepPagerAdapter mAdapter;
    private TextView mNavigationIndicatorTextView;
    private ImageButton mLeftButton, mRightButton;
    private String mVideoUri;
    private PlayerView mPlayerView;
    private SimpleExoPlayer mPlayer;
    private RecipeStep mCurrentStep;
    private ImageView mPlaceholder;
    private int mSelectedStepIndex;

    private Dialog mFullscreenDialog;
    private boolean mPlayerFullscreen;
    private ImageButton mFullscreenButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_step);

        mRecipe = getIntent().getParcelableExtra(getString(R.string.recipe_key));
        mRecipeSteps = mRecipe.getSteps();
        mSelectedStepIndex = getIntent().getIntExtra(getString(R.string.recipe_step_index_key), 0);
        mCurrentStep = mRecipeSteps.get(mSelectedStepIndex);

        setupView();

        OrientationEventListener orientationEventListener =
                new OrientationEventListener(this) {
                    @Override
                    public void onOrientationChanged(int orientation) {
                        int epsilon = 10;
                        int leftLandscape = 90;
                        int rightLandscape = 270;
                        if (epsilonCheck(orientation, leftLandscape, epsilon) ||
                                epsilonCheck(orientation, rightLandscape, epsilon)) {
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                            openFullscreenDialog();
                        } else {
                            closeFullscreenDialog();
                        }
                    }

                    private boolean epsilonCheck(int a, int b, int epsilon) {
                        return a > b - epsilon && a < b + epsilon;
                    }
                };
        orientationEventListener.enable();

        if (mCurrentStep != null) mVideoUri = mCurrentStep.getVideoURL();
        if (mCurrentStep != null && mVideoUri == null) mVideoUri = mCurrentStep.getThumbnailURL();
    }

    private void setupView() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(mRecipe.getName());
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mViewPager = findViewById(R.id.recipe_step_view_pager);
        mLeftButton = findViewById(R.id.btn_left);
        mRightButton = findViewById(R.id.btn_right);
        mNavigationIndicatorTextView = findViewById(R.id.tv_indicator);

        mAdapter = new RecipeStepPagerAdapter(getSupportFragmentManager(), mRecipeSteps);

        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(mSelectedStepIndex);

        mPlayerView = findViewById(R.id.recipe_step_video_view);
        mPlaceholder = findViewById(R.id.video_placeholder);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int state) {
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                setNavigationIndicatorText(position);
                toggleArrowVisibility(position);
                RecipeStepFragment recipeStepFragment = (RecipeStepFragment) mAdapter.getRegisteredFragment(position);
                mCurrentStep = recipeStepFragment.getRecipeStep();
                setVideoSource();
            }
        });
        setNavigationIndicatorText(mSelectedStepIndex);
        toggleArrowVisibility(mSelectedStepIndex);

        mFullscreenDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
            public void onBackPressed() {
                if (mPlayerFullscreen) closeFullscreenDialog();
                super.onBackPressed();
            }
        };

        mFullscreenButton = findViewById(R.id.exo_fullscreen);
        mFullscreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mPlayerFullscreen) openFullscreenDialog();
                else closeFullscreenDialog();
            }
        });
    }

    private void openFullscreenDialog() {
        ((ViewGroup) mPlayerView.getParent()).removeView(mPlayerView);
        mFullscreenDialog.addContentView(mPlayerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mFullscreenButton.setImageResource(R.drawable.ic_fullscreen_exit_24dp);
        mPlayerFullscreen = true;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        mFullscreenDialog.show();
    }

    private void closeFullscreenDialog() {
        ((ViewGroup) mPlayerView.getParent()).removeView(mPlayerView);
        ((FrameLayout) findViewById(R.id.video_container)).addView(mPlayerView);
        mPlayerFullscreen = false;
        mFullscreenDialog.dismiss();
        mFullscreenButton.setImageResource(R.drawable.ic_fullscreen_24dp);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void initPlayer() {
        if (mPlayer == null) {
            mPlayer = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector());
        }

        setVideoSource();
        mPlayerView.setPlayer(mPlayer);
    }

    private void setVideoSource() {
        if (mCurrentStep != null) mVideoUri = mCurrentStep.getVideoURL();
        if (mCurrentStep != null && mVideoUri == null) mVideoUri = mCurrentStep.getThumbnailURL();
        if (mVideoUri.isEmpty() || mVideoUri == null) {
            mPlayerView.setVisibility(View.INVISIBLE);
            mPlaceholder.setVisibility(View.VISIBLE);
            mPlayer.stop();
            return;
        }
        String userAgent = Util.getUserAgent(this, getString(R.string.app_name));
        HttpDataSource.Factory dataSourceFactory = new DefaultHttpDataSourceFactory(userAgent);

        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(mVideoUri));
        mPlayer.prepare(videoSource);
        mPlayerView.setVisibility(View.VISIBLE);
        mPlaceholder.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onStart() {
        super.onStart();
        initPlayer();
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

    private void releasePlayer() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    public void onLeftClick(View view) {
        mViewPager.arrowScroll(ViewPager.FOCUS_LEFT);
    }

    public void onRightClick(View view) {
        mViewPager.arrowScroll(ViewPager.FOCUS_RIGHT);
    }

    private void setNavigationIndicatorText(int pos) {
        int currentIndex = mRecipeSteps.get(pos).getId();
        int lastIndex = mRecipeSteps.get(mRecipeSteps.size() - 1).getId();
        if (pos == 0) mNavigationIndicatorTextView.setText(getString(R.string.lets_bake));
        else
            mNavigationIndicatorTextView.setText(getString(R.string.pager_navigation_indicator, currentIndex, lastIndex));
    }

    private void toggleArrowVisibility(int pos) {
        boolean isAtZeroIndex = pos == 0;
        boolean isAtLastIndex = pos == mRecipeSteps.size() - 1;
        if (isAtZeroIndex) mLeftButton.setVisibility(View.INVISIBLE);
        else mLeftButton.setVisibility(View.VISIBLE);

        if (isAtLastIndex) mRightButton.setVisibility(View.INVISIBLE);
        else mRightButton.setVisibility(View.VISIBLE);
    }
}
