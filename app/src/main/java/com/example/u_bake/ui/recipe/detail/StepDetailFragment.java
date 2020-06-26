package com.example.u_bake.ui.recipe.detail;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.u_bake.data.Instruction;
import com.example.u_bake.databinding.StepDetailBinding;
import com.example.u_bake.ui.recipe.steps.StepListActivity;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.squareup.picasso.Picasso;

import java.util.Arrays;

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

            viewModel = new ViewModelProvider(getViewModelStore(),
                    new StepDetailViewModel.StepDetailViewModelFactory(
                            arguments.getInt(ARG_ITEM_ID),
                            Arrays.asList((Instruction[]) arguments.getParcelableArray(ARG_STEP_LIST))
                    )).get(StepDetailViewModel.class);
            viewModel.getInstructionIndex().observe(getViewLifecycleOwner(), integer -> populateUi());
        } else {
            //If Fragment is loaded without arguments, close out activity
            requireActivity().finish();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = StepDetailBinding.inflate(inflater, container, false);


        if (binding.navButtonBar != null){
            binding.navButtonBar.btnNextStep.setOnClickListener(v -> {
                viewModel.incrementIndex();
                evaluateShouldButtonsBeEnabled();
            });
            binding.navButtonBar.btnPrevStep.setOnClickListener(v -> {
                viewModel.decrementIndex();
                evaluateShouldButtonsBeEnabled();
            });
        }

        //TODO Initialize ExoPlayer
        player = new SimpleExoPlayer.Builder(requireContext()).build();
        binding.pvVideo.setPlayer(player);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (binding.navButtonBar != null) {
            evaluateShouldButtonsBeEnabled();
        }
        populateUi();
    }

    private void populateUi(){
        Integer index = viewModel.getInstructionIndex().getValue();
        Instruction instruction = viewModel.getInstructions().get(index);
        binding.tvStepDetail.setText(instruction.description());
        if (instruction.thumbnailURL().isEmpty() && instruction.videoURL().isEmpty()){
            binding.flMediaHolder.setVisibility(View.GONE);
            return;
        }
        if (!instruction.thumbnailURL().isEmpty()){
            binding.flMediaHolder.setVisibility(View.VISIBLE);
            binding.pvVideo.setVisibility(View.INVISIBLE);
            binding.ivThumbnail.setVisibility(View.VISIBLE);
            //TODO Add placeholder and error arguments to Picasso chain
            Picasso.get()
                    .load(instruction.getThumbnailUri())
                    .into(binding.ivThumbnail);
        } else if (!instruction.videoURL().isEmpty()){
            binding.flMediaHolder.setVisibility(View.VISIBLE);
            binding.pvVideo.setVisibility(View.VISIBLE);
            binding.ivThumbnail.setVisibility(View.INVISIBLE);
            //TODO Load video
        }
    }

    private void evaluateShouldButtonsBeEnabled() {
        Integer index = viewModel.getInstructionIndex().getValue();
        if (index == null){
            Log.w(getClass().getSimpleName(), "ViewModel Index is somehow null.");
            return;
        }
        if (binding.navButtonBar == null){
            throw new RuntimeException("Tried to access buttons when button bar does not exist.");
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