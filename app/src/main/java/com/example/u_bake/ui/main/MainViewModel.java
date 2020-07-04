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
import com.example.u_bake.utils.json.JsonUtils;
import com.example.u_bake.utils.NetworkUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

    public void initializeRecipes(){
        fetchRecipesTask = new FetchRecipesTask();
        fetchRecipesTask.execute(!isCacheFresh());
    }

    //Checks if Cache is older than a day.
    private boolean isCacheFresh(){
        File cache = new File(getApplication().getCacheDir(), jsonCache);
        if (cache.exists()){
            Date lastModified = new Date(cache.lastModified());
            Date now = new Date();
            long elapsedMillis = Math.abs(now.getTime() - lastModified.getTime());
            long elapsedDays = TimeUnit.DAYS.convert(elapsedMillis, TimeUnit.MILLISECONDS);
            return elapsedDays < 1;
        } else {
            return false;
        }
    }

    @Override
    protected void onCleared() {
        if (fetchRecipesTask != null){
            fetchRecipesTask.cancel(true);
        }
        super.onCleared();
    }

    @SuppressLint("StaticFieldLeak")
    private class FetchRecipesTask extends AsyncTask<Boolean, Void, Recipe[]> {

        public static final String TAG = "FetchRecipesTask";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            status.postValue(LoadingStatus.LOADING);
        }

        @SuppressWarnings("ResultOfMethodCallIgnored")
        @Override
        protected Recipe[] doInBackground(Boolean... args) {
            boolean shouldCache = args[0];

            try {
                String rawJSON = NetworkUtils.getRawRecipeJSON();

                if (shouldCache && rawJSON != null && !rawJSON.isEmpty()) {
                    //Cache the retrieved JSON.
                    File cache = new File(getApplication().getCacheDir(), jsonCache);
                    if (!cache.exists()){
                        cache.createNewFile();
                    }
                    FileWriter writer = new FileWriter(cache);
                    writer.write(rawJSON);
                    writer.close();
                }

                return JsonUtils.parseRecipeListJSON(rawJSON);
            } catch (IOException e) {
                Log.e(TAG, "Error when retrieving and caching recipes.", e);
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
                        if (recipesFromCache != null) {
                            recipeList.postValue(Arrays.asList(recipesFromCache));
                            status.postValue(LoadingStatus.DONE);
                        } else {
                            status.postValue(LoadingStatus.ERROR);
                        }
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
