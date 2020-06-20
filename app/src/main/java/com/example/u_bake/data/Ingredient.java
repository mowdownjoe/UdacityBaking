package com.example.u_bake.data;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Ingredient implements Parcelable {
    abstract String ingredient();
    abstract int quantity();
    abstract String measure();
}
