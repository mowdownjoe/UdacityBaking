package com.example.u_bake.ui.recipe.detail;

import android.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.u_bake.AppExecutors;
import com.example.u_bake.data.Instruction;
import com.example.u_bake.databinding.StepDetailBinding;
import com.example.u_bake.ui.recipe.steps.StepListActivity;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
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

    StepDetailBinding binding;
    StepDetailViewModel viewModel;
    SimpleExoPlayer player;
    OkHttpClient client;

    private enum MediaVisibility{
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

            viewModel = new ViewModelProvider(getViewModelStore(),
                    new StepDetailViewModel.StepDetailViewModelFactory(
                            arguments.getInt(ARG_ITEM_ID),
                            Arrays.asList((Instruction[]) Objects
                                    .requireNonNull(arguments.getParcelableArray(ARG_STEP_LIST)))
                    )).get(StepDetailViewModel.class);
        } else {
            //If Fragment is loaded without arguments, close out activity
            requireActivity().finish();
        }
    }

    private void setUpOkHttpClient() {
        //TODO Configure client through builder.
        Dispatcher dispatcher = new Dispatcher((ExecutorService) AppExecutors.getInstance().networkIO());

        client = new OkHttpClient.Builder()
                .dispatcher(dispatcher)
                .build();
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
        viewModel.getInstructionIndex().observe(getViewLifecycleOwner(), integer -> populateUi());
    }

    private void populateUi(){
        Instruction instruction = viewModel.getCurrentStep();
        binding.tvStepDetail.setText(instruction.description());

        switch (evaluateWhatMediaToShow()){
            case NO_MEDIA:
                releasePlayer();
                break;
            case VIDEO:
                if (!initPlayer()){
                    prepareMediaForPlayer();
                }
                break;
            case THUMBNAIL:
                releasePlayer();
                Picasso.get()
                        .load(instruction.getThumbnailUri())
                        .into(binding.ivThumbnail);
                break;
        }

        if (binding.navButtonBar != null){//If not using TwoPane UI
            ActionBar actionBar = requireActivity().getActionBar();
            if (actionBar != null) {
                actionBar.setTitle(instruction.shortDescription());
            }
        }
    }

    private MediaVisibility evaluateWhatMediaToShow() {
        if (!viewModel.shouldUseImage() && !viewModel.shouldUseVideo()){
            binding.flMediaHolder.setVisibility(View.GONE);
            return MediaVisibility.NO_MEDIA;
        } else {
            binding.flMediaHolder.setVisibility(View.VISIBLE);
        }
        if (viewModel.shouldUseVideo()) {
            binding.pvVideo.setVisibility(View.VISIBLE);
            binding.ivThumbnail.setVisibility(View.INVISIBLE);
            return MediaVisibility.VIDEO;
        }
        if (viewModel.shouldUseImage()){
            binding.ivThumbnail.setVisibility(View.VISIBLE);
            binding.pvVideo.setVisibility(View.INVISIBLE);
            return MediaVisibility.THUMBNAIL;
        }
        throw new IllegalStateException();
    }

    private boolean initPlayer() {
        if (player == null) {
            player = new SimpleExoPlayer.Builder(requireContext()).setUseLazyPreparation(true).build();
            binding.pvVideo.setPlayer(player);

            prepareMediaForPlayer();
            return true; //Player initialized.
        }
        return false; //Player already initialized.
    }

    private void prepareMediaForPlayer() {
        player.prepare(createMediaSource());
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
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