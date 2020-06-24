package com.example.u_bake.data;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonClass;

@AutoValue
public abstract class Ingredient implements Parcelable {
    public abstract String ingredient();
    public abstract int quantity();
    public abstract String measure();
}
