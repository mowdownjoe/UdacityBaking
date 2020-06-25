package com.example.u_bake.ui.recipe.steps;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.u_bake.data.Ingredient;
import com.example.u_bake.data.Instruction;
import com.example.u_bake.data.Recipe;
import com.example.u_bake.databinding.ActivityStepListBinding;
import com.example.u_bake.ui.recipe.detail.StepDetailActivity;

import java.util.Arrays;

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
    StepListViewModel viewModel;

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
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(new StepListAdapter(this,
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

}