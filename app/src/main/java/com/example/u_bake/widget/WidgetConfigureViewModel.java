package com.example.u_bake.widget;

import android.app.Application;

import androidx.annotation.NonNull;

import com.example.u_bake.AppExecutors;
import com.example.u_bake.ui.main.LoadingStatus;
import com.example.u_bake.ui.main.MainViewModel;

import java.io.File;

public class WidgetConfigureViewModel extends MainViewModel {
    public WidgetConfigureViewModel(@NonNull Application application) {
        super(application);
    }

    @Override
    public void initializeRecipes() {
        File cache = new File(getApplication().getCacheDir(), jsonCache);
        if (cache.exists()){
            AppExecutors.getInstance().diskIO().execute(() -> {
                setStatus(LoadingStatus.LOADING);
                postFromCache(cache);
            });
        } else {
            super.initializeRecipes();
        }
    }
}
