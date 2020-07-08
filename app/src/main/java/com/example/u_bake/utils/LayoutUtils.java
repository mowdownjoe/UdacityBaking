package com.example.u_bake.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.example.u_bake.R;
import com.example.u_bake.data.Ingredient;

import java.util.List;

public class LayoutUtils {
    public static int pxToDp(Context c, int v) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, v,
                c.getResources().getDisplayMetrics()));
    }

    public static int getScreenWidthPx(WindowManager manager){
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    public static String buildRecipeIngredientsCard(@NonNull final List<Ingredient> recipeIngredients){
        StringBuilder builder = new StringBuilder();
        for (Ingredient i: recipeIngredients) {
            float v = i.quantity();
            if (v % 1 == 0) {
                builder.append(Math.round(v)).append(' ');
            } else {
                builder.append(v).append(' ');
            }
            if (!i.measure().toLowerCase().equals("unit")){
                builder.append(i.measure()).append(' ');
            }
            builder.append(i.ingredient()).append('\n');
        }
        return builder.toString();
    }
}
