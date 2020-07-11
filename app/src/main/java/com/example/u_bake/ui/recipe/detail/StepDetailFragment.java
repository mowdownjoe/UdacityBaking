package com.example.u_bake.ui.recipe.detail;

import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowInsetsController;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.u_bake.AppExecutors;
import com.example.u_bake.R;
import com.example.u_bake.data.Instruction;
import com.example.u_bake.databinding.StepDetailBinding;
import com.example.u_bake.ui.recipe.steps.StepListActivity;
import com.example.u_bake.utils.LayoutUtils;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.util.EventLogger;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;

/**
 * A fragment representing a single Step detail screen.
 * This fragment is either contained in a {@link StepListActivity}
 * in two-pane mode (on tablets) or a {@link StepDetailActivity}
 * on handsets.
 */
public class StepDetailFragment extends Fragment {
    private static final String KEY_PLAYER_POSITION = "player_position";

    //TODO Figure out why video doesn't load on Phones

    StepDetailBinding binding;
    StepDetailViewModel viewModel;
    SimpleExoPlayer player;
    OkHttpClient client;

    enum MediaVisibility{
        NO_MEDIA, VIDEO, THUMBNAIL
    }

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    public static final String ARG_STEP_LIST = "step_list";

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public StepDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null &&
                arguments.containsKey(ARG_ITEM_ID)
                && arguments.containsKey(ARG_STEP_LIST)) {

            setUpOkHttpClient();

            Instruction[] instructions = Instruction.convertIntentArray(Objects
                    .requireNonNull(arguments.getParcelableArray(ARG_STEP_LIST)));
            viewModel = new ViewModelProvider(getViewModelStore(),
                    new StepDetailViewModel.StepDetailViewModelFactory(
                            arguments.getInt(ARG_ITEM_ID),
                            Arrays.asList(instructions)
                    )).get(StepDetailViewModel.class);
        } else {
            //If Fragment is loaded without arguments, close out activity
            requireActivity().finish();
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = StepDetailBinding.inflate(inflater, container, false);

        if (binding.navButtonBar != null){
            binding.navButtonBar.btnNextStep.setOnClickListener(v -> {
                viewModel.incrementIndex();
                evaluateShouldNavButtonsBeEnabled();
            });
            binding.navButtonBar.btnPrevStep.setOnClickListener(v -> {
                viewModel.decrementIndex();
                evaluateShouldNavButtonsBeEnabled();
            });
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (binding.navButtonBar != null) {
            evaluateShouldNavButtonsBeEnabled();
        }

        //Set up Viewmodel observers for state machine
        viewModel.getInstructionIndex().observe(getViewLifecycleOwner(), i -> populateUi());

        if (binding.navButtonBar != null) { //Observer will only be set if not in two-pane
            viewModel.getFullscreenPlayerFlag().observe(getViewLifecycleOwner(), shouldBeFullscreen -> {
                Log.v("FullscreenObserver", "Fullscreen status: "+shouldBeFullscreen);
                ConstraintLayout.LayoutParams params =
                        (ConstraintLayout.LayoutParams) binding.flMediaHolder.getLayoutParams();
                ActionBar toolbar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
                if (shouldBeFullscreen){
                    binding.tvStepDetail.setVisibility(View.GONE);
                    binding.navButtonBar.btnNextStep.setVisibility(View.GONE);
                    binding.navButtonBar.btnPrevStep.setVisibility(View.GONE);

                    params.height = ConstraintLayout.LayoutParams.MATCH_PARENT;
                    binding.flMediaHolder.setLayoutParams(params);

                    if (toolbar != null){
                        toolbar.hide();
                    }


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        WindowInsetsController controller = view.getWindowInsetsController();
                        if (controller != null) {
                            controller.hide(WindowInsets.Type.statusBars());
                            controller.hide(WindowInsets.Type.navigationBars());
                        }
                    } else {
                        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN |
                                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
                    }
                } else {
                    binding.tvStepDetail.setVisibility(View.VISIBLE);
                    binding.navButtonBar.btnNextStep.setVisibility(View.VISIBLE);
                    binding.navButtonBar.btnPrevStep.setVisibility(View.VISIBLE);

                    params.height = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;
                    binding.flMediaHolder.setLayoutParams(params);

                    if (toolbar != null){
                        toolbar.show();
                    }


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        WindowInsetsController controller = view.getWindowInsetsController();
                        if (controller != null) {
                            controller.show(WindowInsets.Type.statusBars());
                            controller.show(WindowInsets.Type.navigationBars());
                        }
                    } else {
                        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    }
                }
            });
        }

        viewModel.getMediaState().observe(getViewLifecycleOwner(), mediaVisibility -> {
            Log.v("MediaTypeObserver", "Media type: "+mediaVisibility);
            if (mediaVisibility == MediaVisibility.NO_MEDIA){
                binding.flMediaHolder.setVisibility(View.GONE);
            } else {
                binding.flMediaHolder.setVisibility(View.VISIBLE);
            }

            switch (mediaVisibility){
                case NO_MEDIA:
                    releasePlayer();
                    viewModel.setIsPlayerFullscreen(false);
                    break;
                case VIDEO:
                    binding.pvVideo.setVisibility(View.VISIBLE);
                    binding.ivThumbnail.setVisibility(View.INVISIBLE);

                    if (!initPlayer()){
                        prepareMediaForPlayer();
                    }
                    evaluateIfVideoShouldBeFullscreen();
                    break;
                case THUMBNAIL:
                    binding.ivThumbnail.setVisibility(View.VISIBLE);
                    binding.pvVideo.setVisibility(View.INVISIBLE);

                    releasePlayer();
                    viewModel.setIsPlayerFullscreen(false);
                    Picasso.get()
                            .load(viewModel.getCurrentStep().getThumbnailUri())
                            .placeholder(R.drawable.loading_plate)
                            .error(R.drawable.ic_baseline_broken_image_24)
                            .into(binding.ivThumbnail);
                    break;
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (viewModel.getMediaState().getValue() == MediaVisibility.VIDEO){
            outState.putLong(KEY_PLAYER_POSITION, player.getContentPosition());
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (viewModel.getMediaState().getValue() == MediaVisibility.VIDEO
                && savedInstanceState != null){
            long position = savedInstanceState.getLong(KEY_PLAYER_POSITION, 0);
            if (player != null) {
                player.seekTo(position);
            } else {
                viewModel.setPlayerRestorePoint(position);
            }
        }
    }

    private void setUpOkHttpClient() {
        Dispatcher dispatcher = new Dispatcher((ExecutorService) AppExecutors.getInstance().networkIO());

        client = new OkHttpClient.Builder()
                .dispatcher(dispatcher)
                .build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        if (viewModel.getMediaState().getValue() == MediaVisibility.VIDEO
                && binding.navButtonBar != null //if not using TwoPane UI
                && newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            viewModel.setIsPlayerFullscreen(true);
        } else {
            viewModel.setIsPlayerFullscreen(false);
        }
        super.onConfigurationChanged(newConfig);
    }

    private void populateUi(){
        Instruction instruction = viewModel.getCurrentStep();
        binding.tvStepDetail.setText(instruction.description());

        viewModel.setMediaState(evaluateWhatMediaToShow());

        if (binding.navButtonBar != null){//If not using TwoPane UI
            ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(instruction.shortDescription());
            }
        }
    }

    private void evaluateIfVideoShouldBeFullscreen() {
        //Function sets flag in ViewModel and should trigger observer
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE
                && binding.navButtonBar != null){ //If Landscape AND TwoPane is not enabled
            viewModel.setIsPlayerFullscreen(true);
        } else {
            viewModel.setIsPlayerFullscreen(false);
        }
    }

    private MediaVisibility evaluateWhatMediaToShow() {
        if (!viewModel.shouldUseImage() && !viewModel.shouldUseVideo()){
            return MediaVisibility.NO_MEDIA;
        }
        if (viewModel.shouldUseVideo()) {
            return MediaVisibility.VIDEO;
        }
        if (viewModel.shouldUseImage()){
            return MediaVisibility.THUMBNAIL;
        }
        throw new IllegalStateException();
    }

    private boolean initPlayer() {
        if (player == null) {
            DefaultTrackSelector selector = new DefaultTrackSelector(requireContext());

            player = new SimpleExoPlayer.Builder(requireContext())
                    .setTrackSelector(selector)
                    .build();
            binding.pvVideo.setPlayer(player);

            //Uncomment for Logging
            //player.addAnalyticsListener(new EventLogger(selector));

            prepareMediaForPlayer();
            return true; //Player initialized.
        }
        return false; //Player already initialized.
    }

    private void prepareMediaForPlayer() {
        player.prepare(createMediaSource());
        player.seekTo(viewModel.getPlayerRestorePoint());
        player.setPlayWhenReady(true);
    }

    @NotNull
    private MediaSource createMediaSource() {
        String userAgent = Util.getUserAgent(requireContext(), "ubake");
        OkHttpDataSourceFactory dataSourceFactory = new OkHttpDataSourceFactory(
                request -> client.newCall(request), userAgent);
        return new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(viewModel.getCurrentStep().getVideoUri());
    }

    private void releasePlayer() {
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
    }

    private void evaluateShouldNavButtonsBeEnabled() {
        Integer index = viewModel.getInstructionIndex().getValue();
        if (index == null){
            Log.w(getClass().getSimpleName(), "ViewModel Index is somehow null.");
            return;
        }
        if (binding.navButtonBar == null){
            throw new IllegalStateException("Tried to access buttons when button bar does not exist.");
        }
        if (index <= 0){
            binding.navButtonBar.btnPrevStep.setEnabled(false);
        } else {
            binding.navButtonBar.btnPrevStep.setEnabled(true);
        }
        if (index >= viewModel.getInstructions().size() -1){
            binding.navButtonBar.btnNextStep.setEnabled(false);
        } else {
            binding.navButtonBar.btnNextStep.setEnabled(true);
        }
    }
}