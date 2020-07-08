package com.example.u_bake.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.u_bake.AppExecutors;
import com.example.u_bake.R;
import com.example.u_bake.data.Recipe;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link SingleRecipeCacheWidgetConfigureActivity SingleRecipeCacheWidgetConfigureActivity}
 */
public class SingleRecipeCacheWidget extends AppWidgetProvider {

    private static final String TAG = "RecipeWidget";
    private static final int REQUEST_CODE = 61;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = SingleRecipeCacheWidgetConfigureActivity.loadIngredientsPref(context, appWidgetId);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.single_recipe_cache_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);

        //Create and set the widget's PendingIntent from file.
        File file = new File(context.getFilesDir(),
                SingleRecipeCacheWidgetConfigureActivity.FILE_PREFIX + appWidgetId);
        if (file.exists()){
            try {
                FileInputStream fileStream = new FileInputStream(file);
                ObjectInputStream inputStream = new ObjectInputStream(fileStream);

                Recipe recipe = (Recipe) inputStream.readObject();

                inputStream.close();
                fileStream.close();

                Intent intent = Recipe.buildRecipeIntent(recipe, context).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, REQUEST_CODE, intent, 0);
                views.setOnClickPendingIntent(R.id.widget_root, pendingIntent);
            } catch (IOException e) {
                //Toast.makeText(context, R.string.generic_widget_error_toast, Toast.LENGTH_LONG).show();
                Log.e(TAG, "Issue reading from linking file.", e);
            } catch (ClassNotFoundException e) {
                //Toast.makeText(context, R.string.generic_widget_error_toast, Toast.LENGTH_LONG).show();
                Log.e(TAG, "Cannot create recipe from linking file.", e);
            }
        } else {
            //Toast.makeText(context, R.string.widget_data_gone_error_toast, Toast.LENGTH_LONG).show();
            Log.w(TAG, "Linking file not found. Widget needs to be regenerated.");
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            SingleRecipeCacheWidgetConfigureActivity.deleteIngredientsPref(context, appWidgetId);
            File file = new File(context.getFilesDir(),
                    SingleRecipeCacheWidgetConfigureActivity.FILE_PREFIX+appWidgetId);
            if (file.exists()){
                //noinspection Convert2MethodRef
                AppExecutors.getInstance().diskIO().execute(() -> file.delete());
            }
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        super.onDisabled(context);
    }
}

