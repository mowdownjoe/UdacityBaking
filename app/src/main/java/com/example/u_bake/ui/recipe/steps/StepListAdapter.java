package com.example.u_bake.ui.recipe.steps;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.u_bake.R;
import com.example.u_bake.data.Instruction;
import com.example.u_bake.databinding.StepListContentBinding;
import com.example.u_bake.ui.recipe.detail.StepDetailActivity;
import com.example.u_bake.ui.recipe.detail.StepDetailFragment;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StepListAdapter
        extends RecyclerView.Adapter<StepListAdapter.StepListViewHolder> {

    private final StepListActivity mParentActivity;
    private final List<Instruction> mInstructions;
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

    StepListAdapter(StepListActivity parent,
                    List<Instruction> items,
                    boolean twoPane) {
        mInstructions = items;
        mParentActivity = parent;
        mTwoPane = twoPane;
    }

    @NotNull
    @Override
    public StepListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.step_list_content, parent, false);
        return new StepListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final StepListViewHolder holder, int position) {
        holder.binding.idText.setText(mInstructions.get(position).id());
        holder.binding.content.setText(mInstructions.get(position).shortDescription());

        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(mOnClickListener);
    }

    @Override
    public int getItemCount() {
        return mInstructions.size();
    }

    static class StepListViewHolder extends RecyclerView.ViewHolder {
        final StepListContentBinding binding;

        StepListViewHolder(View view) {
            super(view);
            binding = StepListContentBinding.bind(view);
        }
    }
}
