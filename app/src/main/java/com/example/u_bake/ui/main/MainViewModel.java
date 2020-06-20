package com.example.u_bake.ui.main;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.u_bake.data.Recipe;
import com.example.u_bake.utils.JsonUtils;
import com.example.u_bake.utils.NetworkUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private MutableLiveData<LoadingStatus> status;
    private MutableLiveData<List<Recipe>> recipeList;

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
    private class FetchRecipesTask extends AsyncTask<Void, Void, Recipe[]>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            status.postValue(LoadingStatus.LOADING);
        }

        @Override
        protected Recipe[] doInBackground(Void... voids) {
            try {
                String rawJSON = NetworkUtils.getRawRecipeJSON();
                return JsonUtils.parseRecipeListJSON(rawJSON);
            } catch (IOException e) {
                e.printStackTrace();
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
                status.postValue(LoadingStatus.ERROR);
            }
        }
    }
}
