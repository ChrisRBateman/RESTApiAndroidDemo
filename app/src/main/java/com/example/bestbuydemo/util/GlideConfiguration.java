package com.example.bestbuydemo.util;

import android.content.Context;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.module.AppGlideModule;

import com.bumptech.glide.annotation.GlideModule;

/**
 * Configuration class for Glide library
 */
@GlideModule
@SuppressWarnings("unused")
public class GlideConfiguration extends AppGlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        // Apply options to the builder here.

        // This is a fix for the off-white background for some images on Android 2.3.3
        builder.setDefaultRequestOptions(
            new RequestOptions()
                .format(DecodeFormat.PREFER_ARGB_8888));
    }

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
}
