package com.example.u_bake.data;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonClass;

import java.io.Serializable;

@AutoValue
public abstract class Ingredient implements Parcelable, Serializable {
    public abstract String ingredient();
    public abstract float quantity();
    public abstract String measure();

    public static Ingredient create(String name, float quantity, String measure){
        return new AutoValue_Ingredient(name, quantity, measure);
    }

    public static Ingredient[] convertIntentArray(Parcelable[] array){
        AutoValue_Ingredient[] ingredients = new AutoValue_Ingredient[array.length];
        for (int i=0; i < ingredients.length; i++){
            ingredients[i] = (AutoValue_Ingredient) array[i];
        }
        return ingredients;
    }
}
