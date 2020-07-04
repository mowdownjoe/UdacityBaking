package com.example.u_bake.ui.recipe.steps;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.u_bake.R;
import com.example.u_bake.data.Ingredient;
import com.example.u_bake.data.Instruction;
import com.example.u_bake.data.Recipe;
import com.example.u_bake.databinding.CardRecipeItemBinding;
import com.example.u_bake.databinding.StepListContentBinding;
import com.example.u_bake.ui.recipe.detail.StepDetailActivity;
import com.example.u_bake.ui.recipe.detail.StepDetailFragment;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class StepListAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_INGREDIENT_HEADER = 206;
    private static final int TYPE_STEP = 941;

    private final StepListActivity mParentActivity;
    private final List<Instruction> mInstructions;
    private final List<Ingredient> mIngredients;
    private final boolean mTwoPane;
    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int item = (int) view.getTag();
            Instruction[] instructions = new Instruction[mInstructions.size()];
            if (mTwoPane) {
                Bundle arguments = new Bundle();
                arguments.putInt(StepDetailFragment.ARG_ITEM_ID, item);
                arguments.putParcelableArray(StepDetailFragment.ARG_STEP_LIST,
                        mInstructions.toArray(instructions));

                StepDetailFragment fragment = new StepDetailFragment();
                fragment.setArguments(arguments);
                mParentActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.step_detail_container, fragment)
                        .commit();
            } else {
                Context context = view.getContext();

                Intent intent = new Intent(context, StepDetailActivity.class)
                        .putExtra(StepDetailFragment.ARG_ITEM_ID, item)
                        .putExtra(StepDetailFragment.ARG_STEP_LIST, mInstructions.toArray(instructions));

                context.startActivity(intent);
            }
        }
    };

    StepListAdapter(StepListActivity parent, Recipe recipe, boolean twoPane){
        mParentActivity = parent;
        mInstructions = Arrays.asList(recipe.getSteps());
        mIngredients = Arrays.asList(recipe.getIngredients());
        mTwoPane = twoPane;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && mIngredients != null){
            return TYPE_INGREDIENT_HEADER;
        } else {
            return TYPE_STEP;
        }
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_STEP) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.step_list_content, parent, false);
            return new StepViewHolder(view);
        } else if (viewType == TYPE_INGREDIENT_HEADER){
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_recipe_item, parent, false);
            return new IngredientCardViewHolder(view);
        }
        throw new IllegalArgumentException("Somehow received illegal ViewType");
    }

    @Override
    public void onBindViewHolder(@NotNull final RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        if (type == TYPE_STEP){
            ((StepViewHolder) holder).bind(position -1);
        } else if (type == TYPE_INGREDIENT_HEADER){
            ((IngredientCardViewHolder) holder).bind();
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (mIngredients != null){
            ++count;
        }
        if (mInstructions != null) {
            count += mInstructions.size();
        }
        return count;
    }

    class StepViewHolder extends RecyclerView.ViewHolder {
        final StepListContentBinding binding;

        StepViewHolder(View view) {
            super(view);
            binding = StepListContentBinding.bind(view);
        }

        void bind(int position){
            binding.idText.setText(""+mInstructions.get(position).id());
            binding.content.setText(mInstructions.get(position).shortDescription());

            itemView.setTag(position);
            itemView.setOnClickListener(mOnClickListener);
        }
    }

    class IngredientCardViewHolder extends RecyclerView.ViewHolder {

        final CardRecipeItemBinding binding;

        public IngredientCardViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = CardRecipeItemBinding.bind(itemView);
        }

        void bind(){
            StringBuilder builder = new StringBuilder().append(mParentActivity
                    .getString(R.string.ingredient_list_card_start));
            for (Ingredient i: mIngredients) {
                builder.append(i.quantity()).append(' ');
                if (!i.measure().toLowerCase().equals("unit")){
                    builder.append(i.measure()).append(' ');
                }
                builder.append(i.ingredient()).append('\n');
            }
            binding.tvRecipeName.setText(builder.toString());
        }
    }
}
