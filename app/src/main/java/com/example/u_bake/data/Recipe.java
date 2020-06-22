package com.example.u_bake.data;

public class Recipe {
    public static String RECIPE_ID = "id";
    public static String RECIPE_NAME = "name";
    public static String RECIPE_INGREDIENTS = "ingredients";
    public static String RECIPE_STEPS = "steps";
    public static String RECIPE_SERVINGS = "servings";
    public static String RECIPE_IMAGE_URL = "image";

    private int id;
    private String name;
    private AutoValue_Ingredient[] ingredients;
    private AutoValue_Instruction[] steps;
    private int servings;
    private String image;

    public Recipe(int id, String name, AutoValue_Ingredient[] ingredients,
                  AutoValue_Instruction[] steps, int servings, String image) {
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

    public AutoValue_Ingredient[] getIngredients() {
        return ingredients;
    }

    public void setIngredients(AutoValue_Ingredient[] ingredients) {
        this.ingredients = ingredients;
    }

    public AutoValue_Instruction[] getSteps() {
        return steps;
    }

    public void setSteps(AutoValue_Instruction[] steps) {
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
}
