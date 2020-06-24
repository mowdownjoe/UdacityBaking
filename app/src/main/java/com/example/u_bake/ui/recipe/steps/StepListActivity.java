package com.example.u_bake.ui.recipe.steps;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.u_bake.R;
import com.example.u_bake.data.Ingredient;
import com.example.u_bake.data.Instruction;
import com.example.u_bake.data.Recipe;
import com.example.u_bake.databinding.ActivityStepListBinding;
import com.example.u_bake.databinding.StepListContentBinding;
import com.example.u_bake.ui.dummy.DummyContent;
import com.example.u_bake.ui.recipe.detail.StepDetailActivity;
import com.example.u_bake.ui.recipe.detail.StepDetailFragment;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * An activity representing a list of Steps. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link StepDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class StepListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    ActivityStepListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStepListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        binding.toolbar.setTitle(getTitle());

        if (binding.localStepList.stepDetailContainer != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        setupRecyclerView(binding.localStepList.stepList);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this,
                Arrays.asList(getRecipeFromIntent().getSteps()), mTwoPane));
    }

    private Recipe getRecipeFromIntent(){
        Intent intent = getIntent();
        int id = intent.getIntExtra(Recipe.RECIPE_ID, -1);
        if (id == -1) {
            return null;
        }
        String name = intent.getStringExtra(Recipe.RECIPE_NAME);
        Ingredient[] ingredients = (Ingredient[]) intent
                .getParcelableArrayExtra(Recipe.RECIPE_INGREDIENTS);
        Instruction[] instructions = (Instruction[]) intent.getParcelableArrayExtra(Recipe.RECIPE_STEPS);
        int servings = intent.getIntExtra(Recipe.RECIPE_SERVINGS, 0);
        String image = intent.getStringExtra(Recipe.RECIPE_IMAGE_URL);
        return new Recipe(id, name, ingredients, instructions, servings, image);
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final StepListActivity mParentActivity;
        private final List<Instruction> mInstructions;
        private final boolean mTwoPane;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DummyContent.DummyItem item = (DummyContent.DummyItem) view.getTag();
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putString(StepDetailFragment.ARG_ITEM_ID, item.id);
                    StepDetailFragment fragment = new StepDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.step_detail_container, fragment)
                            .commit();
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, StepDetailActivity.class);
                    intent.putExtra(StepDetailFragment.ARG_ITEM_ID, item.id);

                    context.startActivity(intent);
                }
            }
        };

        SimpleItemRecyclerViewAdapter(StepListActivity parent,
                                      List<Instruction> items,
                                      boolean twoPane) {
            mInstructions = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @NotNull
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.step_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.binding.idText.setText(mInstructions.get(position).id());
            holder.binding.content.setText(mInstructions.get(position).shortDescription());

            holder.itemView.setTag(mInstructions.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return mInstructions.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            final StepListContentBinding binding;

            ViewHolder(View view) {
                super(view);
                binding = StepListContentBinding.bind(view);
            }
        }
    }
}