package com.example.u_bake.utils;

import com.example.u_bake.OkHttpException;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetworkUtils {

    private static final String JSON_URL = "http://go.udacity.com/android-baking-app-json";

    public static String getRawRecipeJSON() throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(JSON_URL)
                .get()
                .build();

        Response response = client.newCall(request).execute();

        int code = response.code();

        if (code == 200){
            return response.body().string();
        } else if (code >= 400){
            throw new OkHttpException(response);
        } else {
            throw new RuntimeException("Strange or null response received.");
        }
    }
}
