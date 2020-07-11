package com.example.u_bake.ui.recipe.detail;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.u_bake.data.Instruction;

import java.util.List;

public class StepDetailViewModel extends ViewModel {
    private long playerRestorePoint = 0;
    private List<Instruction> instructions;
    private MutableLiveData<Integer> instructionIndex;
    private MutableLiveData<Boolean> fullscreenPlayerFlag;
    private MutableLiveData<StepDetailFragment.MediaVisibility> mediaState;

    public StepDetailViewModel(List<Instruction> instructions, int instructionIndex) {
        this.instructions = instructions;
        this.instructionIndex = new MutableLiveData<>(instructionIndex);
        fullscreenPlayerFlag = new MutableLiveData<>(false);
        mediaState = new MutableLiveData<>(StepDetailFragment.MediaVisibility.NO_MEDIA);
        Log.v(getClass().getSimpleName(), "Initialized");
    }

    public long getPlayerRestorePoint() {
        return playerRestorePoint;
    }

    public void setPlayerRestorePoint(long playerRestorePoint) {
        this.playerRestorePoint = playerRestorePoint;
    }

    LiveData<StepDetailFragment.MediaVisibility> getMediaState() {
        return mediaState;
    }

    public LiveData<Integer> getInstructionIndex(){
        return instructionIndex;
    }

    public List<Instruction> getInstructions() {
        return instructions;
    }

    public LiveData<Boolean> getFullscreenPlayerFlag() { return fullscreenPlayerFlag; }

    public void incrementIndex(){
        playerRestorePoint = 0;
        instructionIndex.setValue(instructionIndex.getValue() +1);
    }

    public void decrementIndex(){
        playerRestorePoint = 0;
        instructionIndex.setValue(instructionIndex.getValue() -1);
    }

    public void setIsPlayerFullscreen(boolean fullscreen){
        fullscreenPlayerFlag.setValue(fullscreen);
    }

    void setMediaState(StepDetailFragment.MediaVisibility newMediaState) {
        mediaState.setValue(newMediaState);
    }

    @NonNull
    public Instruction getCurrentStep(){
        Integer index = instructionIndex.getValue();
        if (index != null){
            return instructions.get(index);
        }
        throw new IllegalArgumentException("Somehow asked for a null Instruction.");
    }

    public boolean shouldUseVideo(){
        return !getCurrentStep().videoURL().isEmpty();
    }

    public boolean shouldUseImage(){
        return !getCurrentStep().thumbnailURL().isEmpty();
    }

    static class StepDetailViewModelFactory extends ViewModelProvider.NewInstanceFactory{
        private final int index;
        private final List<Instruction> instructions;

        public StepDetailViewModelFactory(int index, List<Instruction> instructions) {
            super();
            this.index = index;
            this.instructions = instructions;
        }

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(StepDetailViewModel.class)) {
                return (T) new StepDetailViewModel(instructions, index);
            }
            throw new IllegalArgumentException("Illegal class assigned");
        }
    }
}
