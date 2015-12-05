package org.xdty.gallery.glide;

import android.graphics.BitmapFactory;
import android.net.Uri;

import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class MediaRequestListener implements RequestListener<Uri, BitmapFactory.Options> {
    @Override
    public boolean onException(Exception e, Uri model, Target<BitmapFactory.Options> target,
            boolean isFirstResource) {
        return false;
    }

    @Override
    public boolean onResourceReady(BitmapFactory.Options resource, Uri model,
            Target<BitmapFactory.Options> target, boolean isFromMemoryCache,
            boolean isFirstResource) {
        return false;
    }
}
