package org.xdty.gallery.glide;

import android.graphics.BitmapFactory;

import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.xdty.gallery.model.Media;

public class MediaRequestListener implements RequestListener<Media, BitmapFactory.Options> {
    @Override
    public boolean onException(Exception e, Media model, Target<BitmapFactory.Options> target,
            boolean isFirstResource) {
        return false;
    }

    @Override
    public boolean onResourceReady(BitmapFactory.Options resource, Media model,
            Target<BitmapFactory.Options> target, boolean isFromMemoryCache,
            boolean isFirstResource) {
        return false;
    }
}
