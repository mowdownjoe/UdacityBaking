package com.example.u_bake.data;

import android.net.Uri;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonClass;
import com.squareup.moshi.Moshi;

@AutoValue
//@JsonClass(generateAdapter = true, generator = "avm")
public abstract class Instruction implements Parcelable {
    public abstract int id();
    public abstract String shortDescription();
    public abstract String description();
    public abstract String videoURL();
    public abstract String thumbnailURL();

    @Nullable
    public Uri getThumbnailUri() {
        if (thumbnailURL() != null && !thumbnailURL().isEmpty()) {
            return Uri.parse(thumbnailURL());
        }
        return null;
    }

    public Uri getVideoUri(){
        if (videoURL() != null && !videoURL().isEmpty()){
            return Uri.parse(videoURL());
        }
        return null;
    }

    public static Instruction create(int id, String shortDescription, String description, String videoURL, String thumbnailURL) {
        return new AutoValue_Instruction(id, shortDescription, description, videoURL, thumbnailURL);
    }

}
