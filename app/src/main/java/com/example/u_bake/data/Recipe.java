package com.example.u_bake.data;

import java.util.List;

public class Recipe {
    private int id;
    private String name;
    private List<AutoValue_Ingredient> ingredients;
    private List<AutoValue_Instruction> steps;
    private int servings;
    //TODO Parse empty image tag in each Recipe?

    public Recipe(int id, String name, List<AutoValue_Ingredient> ingredients, List<AutoValue_Instruction> steps, int servings) {
        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
        this.servings = servings;
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

    public List<AutoValue_Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<AutoValue_Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public List<AutoValue_Instruction> getSteps() {
        return steps;
    }

    public void setSteps(List<AutoValue_Instruction> steps) {
        this.steps = steps;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }
}
