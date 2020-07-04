package com.example.u_bake.data;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonClass;

@AutoValue
public abstract class Ingredient implements Parcelable {
    public abstract String ingredient();
    public abstract float quantity();
    public abstract String measure();

    public static AutoValue_Ingredient create(String name, float quantity, String measure){
        return new AutoValue_Ingredient(name, quantity, measure);
    }
}
