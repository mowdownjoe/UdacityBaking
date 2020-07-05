package com.example.u_bake.ui.recipe.steps;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.u_bake.data.Ingredient;
import com.example.u_bake.data.Instruction;
import com.example.u_bake.data.Recipe;
import com.example.u_bake.databinding.ActivityStepListBinding;
import com.example.u_bake.ui.recipe.detail.StepDetailActivity;

import java.util.ArrayList;

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
    StepListAdapter adapter; //For testing purposes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStepListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        if (binding.localStepList.stepDetailContainer != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        Recipe recipe = getRecipeFromIntent();
        if (recipe == null){
            finish();
            return;
        }

        adapter = new StepListAdapter(this, recipe, mTwoPane);

        binding.localStepList.stepList.setHasFixedSize(true);
        binding.localStepList.stepList.setLayoutManager(new LinearLayoutManager(this));
        binding.localStepList.stepList.setAdapter(adapter);

        binding.toolbar.setTitle(recipe.getName());
    }

    private Recipe getRecipeFromIntent(){
        Intent intent = getIntent();
        int id = intent.getIntExtra(Recipe.RECIPE_ID, -1);
        if (id == -1) {
            return null;
        }
        String name = intent.getStringExtra(Recipe.RECIPE_NAME);

        Parcelable[] ingredientsFromIntent = intent.getParcelableArrayExtra(Recipe.RECIPE_INGREDIENTS);
        if (ingredientsFromIntent == null){
            return null;
        }
        Ingredient[] ingredients = new Ingredient[ingredientsFromIntent.length];
        for (int i = 0; i < ingredients.length; i++) {
            ingredients[i] = (Ingredient) ingredientsFromIntent[i];
        }

        Parcelable[] instructionsFromIntent = intent.getParcelableArrayExtra(Recipe.RECIPE_STEPS);
        if (instructionsFromIntent == null){
            return null;
        }
        Instruction[] instructions = new Instruction[instructionsFromIntent.length];
        for (int i = 0; i < instructions.length; i++) {
            instructions[i] = (Instruction) instructionsFromIntent[i];
        }

        int servings = intent.getIntExtra(Recipe.RECIPE_SERVINGS, 0);
        String image = intent.getStringExtra(Recipe.RECIPE_IMAGE_URL);
        return new Recipe(id, name, ingredients, instructions, servings, image);
    }

}