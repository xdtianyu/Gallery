package org.xdty.gallery.glide;

import android.content.Context;
import android.net.Uri;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.module.GlideModule;

import java.io.InputStream;

public class GlideSetup implements GlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
//        builder.setMemoryCache(new LruResourceCache(64*1024*1024));
//        builder.setBitmapPool(new LruBitmapPool(32 * 1024 * 1024));
        builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        glide.register(Uri.class, InputStream.class, new MediaLoader.Factory());
    }
}
