package com.example.android.bakingtime.ui;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ProcessLifecycleOwner;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatImageButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.bakingtime.R;
import com.example.android.bakingtime.adapters.RecipeStepPagerAdapter;
import com.example.android.bakingtime.data.model.Recipe;
import com.example.android.bakingtime.data.model.RecipeStep;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
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
import static com.example.android.bakingtime.ui.MainActivity.EXTRA_RECIPE;
import static com.example.android.bakingtime.ui.RecipeActivity.EXTRA_RECIPE_STEP_INDEX;

public class RecipeStepFragment extends Fragment implements LifecycleObserver {

    private static final String LOG_TAG = RecipeStepFragment.class.getSimpleName();

    private static final String CURRENT_WINDOW_INDEX = "current_window_index";
    private static final String PLAYBACK_POSITION = "playback_position";
    private static final String CURRENT_STEP = "current_step_index";
    private static final String AUTO_PLAY = "auto-play";

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

    private long mPlaybackPosition;
    private int mCurrentWindow;
    private boolean mAutoPlay = false;

    public RecipeStepFragment() {
    }

    public static RecipeStepFragment newInstance(Recipe recipe, int selectedStepIndex) {

        Bundle args = new Bundle();
        args.putParcelable(EXTRA_RECIPE, recipe);
        args.putInt(EXTRA_RECIPE_STEP_INDEX, selectedStepIndex);

        RecipeStepFragment fragment = new RecipeStepFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mRecipe = args.getParcelable(EXTRA_RECIPE);
        mSelectedStepIndex = args.getInt(EXTRA_RECIPE_STEP_INDEX);
        mCurrentStep = mRecipe.getSteps().get(mSelectedStepIndex);

        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_recipe_step, container, false);

        if (savedInstanceState != null) {
            mPlaybackPosition = savedInstanceState.getLong(PLAYBACK_POSITION, 0);
            mCurrentWindow = savedInstanceState.getInt(CURRENT_WINDOW_INDEX, 0);
            mAutoPlay = savedInstanceState.getBoolean(AUTO_PLAY, false);
            mCurrentStep = savedInstanceState.getParcelable(CURRENT_STEP);
        }

        mPlayerView = rootView.findViewById(R.id.recipe_step_video_view);
        mPlaceholder = rootView.findViewById(R.id.video_placeholder);

        int orientation = getActivity().getResources().getConfiguration().orientation;
        if (getResources().getBoolean(R.bool.isTablet) || orientation == Configuration.ORIENTATION_PORTRAIT) {

            mViewPager = rootView.findViewById(R.id.recipe_step_view_pager);
            mLeftButton = rootView.findViewById(R.id.btn_left);
            mRightButton = rootView.findViewById(R.id.btn_right);
            mNavigationIndicatorTextView = rootView.findViewById(R.id.tv_indicator);

            mAdapter = new RecipeStepPagerAdapter(getChildFragmentManager(), mRecipe.getSteps());

            toggleArrowVisibility(mSelectedStepIndex);
            setNavigationIndicatorText(mSelectedStepIndex);
            if (mViewPager != null) {
                mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrollStateChanged(int state) {
                    }

                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    }

                    @Override
                    public void onPageSelected(int position) {
                        mPlaybackPosition = 0;
                        if (mPlayer != null) mPlayer.stop();
                        mCurrentStep = mRecipe.getSteps().get(position);
                        setStep(position);
                    }
                });
            }

            mViewPager.setAdapter(mAdapter);
            mViewPager.setCurrentItem(mSelectedStepIndex);

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
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            View decorView = getActivity().getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            // Set the content to appear under the system bars so that the
                            // content doesn't resize when the system bars hide and show.
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            // Hide the nav bar and status bar
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        initPlayer();
    }

    @Override
    public void onStop() {
        super.onStop();
        releasePlayer();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onEnterForeground() {
        mAutoPlay = false;
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mPlayer != null) {
            mPlaybackPosition = mPlayer.getCurrentPosition();
            mCurrentWindow = mPlayer.getCurrentWindowIndex();
            mAutoPlay = mPlayer.getPlayWhenReady();

            outState.putLong(PLAYBACK_POSITION, mPlaybackPosition);
            outState.putInt(CURRENT_WINDOW_INDEX, mCurrentWindow);
            outState.putBoolean(AUTO_PLAY, mAutoPlay);
            outState.putParcelable(CURRENT_STEP, mCurrentStep);
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

    }

    public void setStep(int stepIndex) {
        setNavigationIndicatorText(stepIndex);
        toggleArrowVisibility(stepIndex);
        mCurrentStep = mRecipe.getSteps().get(stepIndex);
        preparePlayer(buildMediaSource(mCurrentStep.getVideoURL()));
        if (mViewPager.getCurrentItem() != stepIndex) mViewPager.setCurrentItem(stepIndex);
    }

    private void initPlayer() {
        mPlayer = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(getActivity()),
                new DefaultTrackSelector(),
                new DefaultLoadControl());

        MediaSource videoSource = buildMediaSource(mCurrentStep.getVideoURL());
        preparePlayer(videoSource);
    }

    private void preparePlayer(MediaSource videoSource) {
        if (videoSource == null) useImageInsteadOfVideo();
        else {
            if (mPlayer != null) {
                mPlayer.prepare(videoSource);
                mPlayerView.setPlayer(mPlayer);
                mPlayer.setPlayWhenReady(mAutoPlay);
                mPlayer.seekTo(mPlaybackPosition);
                mPlayerView.setVisibility(VISIBLE);
                mPlaceholder.setVisibility(INVISIBLE);
            }
        }
    }

    private void releasePlayer() {
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    private MediaSource buildMediaSource(String videoUrl) {
        if (videoUrl.isEmpty()) {
            return null;
        } else {
            String userAgent = Util.getUserAgent(getActivity(), getString(R.string.app_name));
            HttpDataSource.Factory dataSourceFactory = new DefaultHttpDataSourceFactory(userAgent);
            return new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(videoUrl));
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
        builder.build()
                .load(Uri.parse(thumbnailUrl))
                .placeholder(R.drawable.video_placeholder)
                .into(mPlaceholder);
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
