package com.example.u_bake.utils;

import android.content.Context;
import android.content.Intent;

import com.example.u_bake.data.Recipe;
import com.example.u_bake.ui.recipe.steps.StepListActivity;

public class MiscUtils {

    public static Intent buildRecipeIntent(Recipe recipe, Context c){
        return new Intent(c, StepListActivity.class)
                .putExtra(Recipe.RECIPE_ID, recipe.getId())
                .putExtra(Recipe.RECIPE_NAME, recipe.getName())
                .putExtra(Recipe.RECIPE_IMAGE_URL, recipe.getImage())
                .putExtra(Recipe.RECIPE_INGREDIENTS, recipe.getIngredients())
                .putExtra(Recipe.RECIPE_STEPS, recipe.getSteps())
                .putExtra(Recipe.RECIPE_SERVINGS, recipe.getServings());
    }
}
