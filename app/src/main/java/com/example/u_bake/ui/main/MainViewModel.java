package com.example.u_bake.ui.main;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.u_bake.data.Recipe;
import com.example.u_bake.utils.JsonUtils;
import com.example.u_bake.utils.NetworkUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private static String jsonCache = "recipes.json";

    private MutableLiveData<LoadingStatus> status;
    private MutableLiveData<List<Recipe>> recipeList;
    private FetchRecipesTask fetchRecipesTask;

    public LiveData<List<Recipe>> getRecipeList(){
        return recipeList;
    }
    public LiveData<LoadingStatus> getLoadingStatus() { return status; }

    public MainViewModel(@NonNull Application application) {
        super(application);
        status = new MutableLiveData<>(LoadingStatus.INIT);
        recipeList = new MutableLiveData<>();
    }



    @SuppressWarnings("deprecation")
    @SuppressLint("StaticFieldLeak")
    private class FetchRecipesTask extends AsyncTask<Boolean, Void, Recipe[]> {

        public static final String TAG = "FetchRecipesTask";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            status.postValue(LoadingStatus.LOADING);
        }

        @Override
        protected Recipe[] doInBackground(Boolean... args) {
            boolean shouldCache = args[0];

            try {
                String rawJSON = NetworkUtils.getRawRecipeJSON();

                if (shouldCache && rawJSON != null && !rawJSON.isEmpty()) {
                    //Cache the retrieved JSON.
                    File cache = File.createTempFile(jsonCache, null, getApplication().getCacheDir());
                    FileWriter writer = new FileWriter(cache);
                    writer.write(rawJSON);
                    writer.close();
                }

                return JsonUtils.parseRecipeListJSON(rawJSON);
            } catch (IOException e) {
                Log.e(TAG, "Error when ", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(Recipe[] recipes) {
            super.onPostExecute(recipes);

            if (recipes != null){
                recipeList.postValue(Arrays.asList(recipes));
                status.postValue(LoadingStatus.DONE);
            } else {
                //Try to grab from cache in case of error.
                File cachedJson = new File(getApplication().getCacheDir(), jsonCache);
                if (cachedJson.exists()){
                    try {
                        List<String> lines = Files.readAllLines(cachedJson.toPath());
                        if (lines.isEmpty()){
                            status.postValue(LoadingStatus.ERROR);
                            return;
                        }
                        String rawFromCache = String.join("\n", lines);
                        Recipe[] recipesFromCache = JsonUtils.parseRecipeListJSON(rawFromCache);
                        recipeList.postValue(Arrays.asList(recipesFromCache));
                        status.postValue(LoadingStatus.DONE);
                    } catch (IOException e) {
                        Log.e(TAG, "No cache found.", e);
                        status.postValue(LoadingStatus.ERROR);
                    }
                } else {
                    status.postValue(LoadingStatus.ERROR);
                }

            }
        }
    }
}
