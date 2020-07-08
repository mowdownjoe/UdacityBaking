package com.example.u_bake.data;

import android.content.Context;
import android.content.Intent;

import com.example.u_bake.ui.recipe.steps.StepListActivity;

import java.io.Serializable;

public class Recipe implements Serializable {
    public static String RECIPE_ID = "id";
    public static String RECIPE_NAME = "name";
    public static String RECIPE_INGREDIENTS = "ingredients";
    public static String RECIPE_STEPS = "steps";
    public static String RECIPE_SERVINGS = "servings";
    public static String RECIPE_IMAGE_URL = "image";

    private int id;
    private String name;
    private Ingredient[] ingredients;
    private Instruction[] steps;
    private int servings;
    private String image;

    public Recipe(int id, String name, Ingredient[] ingredients,
                  Instruction[] steps, int servings, String image) {
        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
        this.servings = servings;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Ingredient[] getIngredients() {
        return ingredients;
    }

    public void setIngredients(Ingredient[] ingredients) {
        this.ingredients = ingredients;
    }

    public Instruction[] getSteps() {
        return steps;
    }

    public void setSteps(Instruction[] steps) {
        this.steps = steps;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public static Intent buildRecipeIntent(Recipe recipe, Context c){
        return new Intent(c, StepListActivity.class)
                .putExtra(RECIPE_ID, recipe.getId())
                .putExtra(RECIPE_NAME, recipe.getName())
                .putExtra(RECIPE_IMAGE_URL, recipe.getImage())
                .putExtra(RECIPE_INGREDIENTS, recipe.getIngredients())
                .putExtra(RECIPE_STEPS, recipe.getSteps())
                .putExtra(RECIPE_SERVINGS, recipe.getServings());
    }
}
