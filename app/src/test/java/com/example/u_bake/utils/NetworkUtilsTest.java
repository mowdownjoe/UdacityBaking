package com.example.u_bake.utils;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.u_bake.OkHttpException;
import com.google.common.truth.Truth;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static com.google.common.truth.Truth.*;
import static org.junit.Assert.*;

public class NetworkUtilsTest {

    MockWebServer testServerPlsIgnore;

    @Before
    public void setUp() throws IOException {
        testServerPlsIgnore = new MockWebServer();
        testServerPlsIgnore.start();
    }

    @After
    public void tearDown() throws Exception {
        testServerPlsIgnore.shutdown();
    }

    @Test
    public void getRawRecipeJSON_errorResponse_expectError() {
        //GIVEN
        MockResponse response = new MockResponse().setResponseCode(404);
        testServerPlsIgnore.enqueue(response);

        //WHEN
        try {
            String output = NetworkUtils.getRawRecipeJSON();
        } catch (OkHttpException e) {
            //THEN
            System.out.println("Error expected and caught.");
            return;
        } catch (IOException e) {
            fail("General IO Exception caught.");
            return;
        }

        fail("Error not caught.");
    }

    @Test
    public void getRawRecipeJSON_emptyResponse(){
        //GIVEN
        MockResponse response = new MockResponse().setBody("");
        testServerPlsIgnore.enqueue(response);

        //WHEN
        try {
            String output = NetworkUtils.getRawRecipeJSON();

            //THEN
            assertThat(output).isEmpty();
        } catch (IOException e) {
            fail("Unexpected IO Exception.");
            e.printStackTrace();
        }
    }
}