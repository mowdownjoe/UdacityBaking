package com.example.u_bake.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.u_bake.R;
import com.example.u_bake.data.Recipe;
import com.example.u_bake.databinding.SingleRecipeCacheWidgetConfigureBinding;
import com.example.u_bake.ui.main.RecipeAdapter;
import com.example.u_bake.utils.LayoutUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;

/**
 * The configuration screen for the {@link SingleRecipeCacheWidget SingleRecipeCacheWidget} AppWidget.
 */
public class SingleRecipeCacheWidgetConfigureActivity extends AppCompatActivity
        implements RecipeAdapter.RecipeOnClickListener {

    private static final String PREFS_NAME = "com.example.u_bake.widget.SingleRecipeCacheWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    static final String FILE_PREFIX = "recipe_";
    SingleRecipeCacheWidgetConfigureBinding binding;
    WidgetConfigureViewModel viewModel;
    RecipeAdapter adapter;
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    public SingleRecipeCacheWidgetConfigureActivity() {
        super();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        binding = SingleRecipeCacheWidgetConfigureBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            Toast.makeText(this, R.string.widget_error, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        binding.rvRecipeList.setHasFixedSize(true);

        if (LayoutUtils.pxToDp(this, LayoutUtils.getScreenWidthPx(getWindowManager())) >= 900){
            GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
            binding.rvRecipeList.setLayoutManager(layoutManager);
        } else {
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            binding.rvRecipeList.setLayoutManager(layoutManager);
        }

        adapter = new RecipeAdapter(this);
        binding.rvRecipeList.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(WidgetConfigureViewModel.class);
        viewModel.getLoadingStatus().observe(this, loadingStatus -> {
            switch (loadingStatus){
                case LOADING:
                    binding.pbLoadingSpinner.setVisibility(View.VISIBLE);
                    binding.tvErrorText.setVisibility(View.INVISIBLE);
                    binding.rvRecipeList.setVisibility(View.INVISIBLE);
                    break;
                case DONE:
                    binding.rvRecipeList.setVisibility(View.VISIBLE);
                    binding.pbLoadingSpinner.setVisibility(View.INVISIBLE);
                    binding.tvErrorText.setVisibility(View.INVISIBLE);
                    break;
                case ERROR:
                    binding.tvErrorText.setVisibility(View.VISIBLE);
                    binding.rvRecipeList.setVisibility(View.INVISIBLE);
                    binding.pbLoadingSpinner.setVisibility(View.INVISIBLE);
                    break;
            }
        });
        viewModel.getRecipeList().observe(this, recipes -> {
            if (recipes != null){
                adapter.setRecipeData(recipes);
            }
        });

        viewModel.initializeRecipes();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveIngredientsPref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);
        prefs.apply();
    }

    static String loadIngredientsPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        if (titleValue != null) {
            return titleValue;
        } else {
            return context.getString(R.string.appwidget_text);
        }
    }

    static void deleteIngredientsPref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void onListItemClick(Recipe recipe) {
        final Context context = SingleRecipeCacheWidgetConfigureActivity.this;

        // When the button is clicked, store the string locally
        String widgetText = "Ingredients for ".concat(recipe.getName()).concat("\n")
                .concat(LayoutUtils.buildRecipeIngredientsCard(Arrays.asList(recipe.getIngredients())));
        saveIngredientsPref(context, mAppWidgetId, widgetText);

        //Store the recipe to launch in local storage.
        try {
            File file = new File(context.getFilesDir(), FILE_PREFIX + mAppWidgetId);
            if (!file.exists()){
                file.createNewFile();
            }
            FileOutputStream fileStream = new FileOutputStream(file);
            ObjectOutputStream outputStream = new ObjectOutputStream(fileStream);
            outputStream.writeObject(recipe);
            outputStream.close();
            fileStream.close();
        } catch (IOException e) {
            Log.e("WidgetActivity", "Could not write linking file to storage.", e);
            Toast.makeText(context, R.string.widget_error_toast, Toast.LENGTH_LONG).show();
            deleteIngredientsPref(context, mAppWidgetId);
            finish(); //Widget should not be created if recipe is not written to storage.
        }

        // It is the responsibility of the configuration activity to update the app widget
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        SingleRecipeCacheWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

        // Make sure we pass back the original appWidgetId
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }
}

