package com.example.u_bake.ui.recipe.detail;

import android.os.Bundle;

import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import com.example.u_bake.R;
import com.example.u_bake.data.Instruction;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

/*
 * Tests in this class depend on UI elements only available when the UI is not in TwoPane mode.
 * As such, they should only be run on a phone or phone-sized AVD.
 */

@MediumTest
@RunWith(AndroidJUnit4.class)
public class StepDetailFragmentPhoneTest {

    @Test
    public void recipeStepDetails_DisplayedInUi(){
        //GIVEN
        Instruction instruction = Instruction.create(
                0,
                "Lorem ipsum",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit",
                "",
                ""
        );

        //WHEN
        Bundle bundle = new Bundle();
        bundle.putParcelableArray(StepDetailFragment.ARG_STEP_LIST, new Instruction[]{instruction});
        bundle.putInt(StepDetailFragment.ARG_ITEM_ID, 0);
        FragmentScenario.launchInContainer(StepDetailFragment.class, bundle, R.style.AppTheme, new FragmentFactory());

        //THEN
        onView(withId(R.id.tv_step_detail)).check(matches(isDisplayed()));
        onView(withId(R.id.tv_step_detail))
                .check(matches(withText("Lorem ipsum dolor sit amet, consectetur adipiscing elit")));
        onView(withId(R.id.fl_media_holder)).check(matches(not(isDisplayed())));
        onView(withId(R.id.btn_next_step)).check(matches(not(isEnabled())));
        onView(withId(R.id.btn_prev_step)).check(matches(not(isEnabled())));
    }

    @Test
    public void recipeStepDetails_ClickNavButton_UiChanged(){
        //GIVEN
        Instruction instruction = Instruction.create(
                0,
                "Lorem ipsum",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit",
                "",
                ""
        );
        Instruction instruction1 = Instruction
                .create(1, "test post", "pls ignore", "", "");

        //WHEN
        Bundle bundle = new Bundle();
        bundle.putParcelableArray(StepDetailFragment.ARG_STEP_LIST,
                new Instruction[]{instruction, instruction1});
        bundle.putInt(StepDetailFragment.ARG_ITEM_ID, 0);
        FragmentScenario.launchInContainer(StepDetailFragment.class, bundle, R.style.AppTheme, new FragmentFactory());

        //THEN
        onView(withId(R.id.tv_step_detail)).check(matches(isDisplayed()));
        onView(withId(R.id.tv_step_detail))
                .check(matches(withText("Lorem ipsum dolor sit amet, consectetur adipiscing elit")));
        onView(withId(R.id.fl_media_holder)).check(matches(not(isDisplayed())));;
        onView(withId(R.id.btn_next_step)).check(matches(isEnabled()));
        onView(withId(R.id.btn_prev_step)).check(matches(not(isEnabled())));

        onView(withId(R.id.btn_next_step)).perform(click());
        onView(withId(R.id.tv_step_detail)).check(matches(isDisplayed()));
        onView(withId(R.id.tv_step_detail)).check(matches(withText("pls ignore")));
        onView(withId(R.id.fl_media_holder)).check(matches(not(isDisplayed())));;
        onView(withId(R.id.btn_next_step)).check(matches(not(isEnabled())));
        onView(withId(R.id.btn_prev_step)).check(matches(isEnabled()));
    }
}