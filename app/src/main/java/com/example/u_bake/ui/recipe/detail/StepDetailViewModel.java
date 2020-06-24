package com.example.u_bake.ui.recipe.detail;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.u_bake.data.Instruction;

import java.util.List;

public class StepDetailViewModel extends ViewModel {
    private List<Instruction> instructions;
    private MutableLiveData<Integer> instructionIndex;

    public StepDetailViewModel(List<Instruction> instructions, int instructionIndex) {
        this.instructions = instructions;
        this.instructionIndex = new MutableLiveData<>(instructionIndex);
    }

    public LiveData<Integer> getInstructionIndex(){
        return instructionIndex;
    }

    public List<Instruction> getInstructions() {
        return instructions;
    }

    public void incrementIndex(){
        instructionIndex.setValue(instructionIndex.getValue() +1);
    }

    public void decrementIndex(){
        instructionIndex.setValue(instructionIndex.getValue() -1);
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
