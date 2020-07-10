package com.example.u_bake.ui.recipe.steps;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.contrib.RecyclerViewActions;

import com.example.u_bake.R;
import com.example.u_bake.data.Ingredient;
import com.example.u_bake.data.Instruction;
import com.example.u_bake.data.Recipe;

import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

public class StepListActivityTest {

    @Test
    public void stepListActivity_DisplayUi(){
        //GIVEN
        Ingredient[] ingredients = new Ingredient[]{
                Ingredient.create("Peanut butter", 1, "TSP"),
                Ingredient.create("Slice of Cheese", 1, "UNIT"),
                Ingredient.create("Milk", .5f, "CUP")
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
        onView(withId(R.id.step_list)).check(matches(isDisplayed()));
        scenario.onActivity(activity -> assertEquals(3, activity.adapter.getItemCount()));
        onView(withId(R.id.step_list)).perform(RecyclerViewActions.scrollToPosition(0));
        onView(withChild(withText(containsString("Ingredients:"))))
                .check(matches(not(isClickable())));
        onView(withChild(withText(containsString("Ingredients:"))))
                .check(matches(withChild(withText(containsString("1 Slice of Cheese")))));
        onView(withChild(withText(containsString("Ingredients:"))))
                .check(matches(withChild(withText(containsString(".5 CUP Milk")))));
        onView(withChild(withText(containsString("Ingredients:"))))
                .check(matches(withChild(withText(containsString("1 TSP Peanut butter")))));
        onView(withChild(withText("Test post"))).check(matches(isDisplayed()));
        onView(withChild(withText("Test post"))).check(matches(isClickable()));

        scenario.close();

    }

    @Test
    public void stepListActivity_ClickOnListItem_NavigateToDetail(){
        //GIVEN
        Ingredient[] ingredients = new Ingredient[]{
                Ingredient.create("Peanut butter", 1, "TSP"),
                Ingredient.create("Slice of Cheese", 1, "UNIT"),
                Ingredient.create("Milk", .5f, "CUP")
        };
        Instruction[] instructions = new Instruction[]{
                Instruction.create(1, "Test post", "Lorem ipsum dolor sit amet, consectetur adipiscing elit.", "", ""),
                Instruction.create(2, "pls ignore",
                        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.", "", "")
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
        scenario.onActivity(activity -> assertEquals(3, activity.adapter.getItemCount()));

        onView(withChild(withText("Test post"))).perform(click());

        onView(withId(R.id.tv_step_detail)).check(matches(isDisplayed()));
        onView(withId(R.id.tv_step_detail))
                .check(matches(withText(containsString("Lorem ipsum dolor sit amet, consectetur adipiscing elit"))));
        onView(withId(R.id.fl_media_holder)).check(matches(not(isDisplayed())));
    }
}