package com.example.u_bake.ui.recipe.steps;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import com.example.u_bake.R;
import com.example.u_bake.data.Ingredient;
import com.example.u_bake.data.Instruction;
import com.example.u_bake.data.Recipe;

import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

public class StepListActivityTest {

    @Test
    public void stepListActivity_DisplayUi(){
        //GIVEN
        Ingredient[] ingredients = new Ingredient[]{
                Ingredient.create("Peanut butter", 1, "TSP"),
                Ingredient.create("Cheese", 1, "UNIT")
        };
        Instruction[] instructions = new Instruction[]{
                Instruction.create(1, "Test post", "", "", ""),
                Instruction.create(2, "pls ignore", "", "", "")
        };
        int id = 42;
        String name = "Peanut Cheese Bar";
        int servings = 1;


        //WHEN
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), StepListActivity.class)
                .putExtra(Recipe.RECIPE_ID, id)
                .putExtra(Recipe.RECIPE_NAME, name)
                .putExtra(Recipe.RECIPE_INGREDIENTS, ingredients)
                .putExtra(Recipe.RECIPE_STEPS, instructions)
                .putExtra(Recipe.RECIPE_SERVINGS, servings)
                .putExtra(Recipe.RECIPE_IMAGE_URL, "");

        ActivityScenario<StepListActivity> scenario = ActivityScenario.launch(intent);

        //THEN
        onView(withId(R.id.local_step_list)).check(matches(isDisplayed()));
        scenario.close();

    }
}