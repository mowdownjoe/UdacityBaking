package com.example.u_bake.data;

import android.net.Uri;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Instruction implements Parcelable {
    abstract int id();
    abstract String shortDescription();
    abstract String description();
    abstract String videoURL();
    abstract String thumbnailURL();

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
}
