package org.xdty.gallery.fragment;

import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.xdty.gallery.R;
import org.xdty.gallery.activity.ViewerActivity;
import org.xdty.gallery.application.Application;
import org.xdty.gallery.data.MediaDataSource;
import org.xdty.gallery.di.DaggerViewerComponent;
import org.xdty.gallery.di.modules.AppModule;
import org.xdty.gallery.di.modules.ViewerModule;
import org.xdty.gallery.model.Media;

import java.io.InputStream;

import javax.inject.Inject;

import pl.droidsonroids.gif.GifDrawable;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

import static org.xdty.gallery.utils.Constants.URI;

public class ImageFragment extends Fragment {

    @Inject
    MediaDataSource mDataSource;
    @Inject
    RequestManager mRequestManager;

    @Inject
    GenericRequestBuilder<Media, InputStream, byte[], GifDrawable> mGifRequestBuilder;

    private boolean isOrientationUpdated = false;
    private boolean isVisibleToUser = false;
    private int width = -1;
    private int height = -1;
    private PhotoView image;

    private Handler mHandler;

    private Runnable mUpdateGifRunnable = new Runnable() {
        @Override
        public void run() {
            if (image != null && image.getDrawable() instanceof Animatable) {
                Animatable animatable = (Animatable) image.getDrawable();
                if (isVisibleToUser) {
                    animatable.start();
                } else {
                    animatable.stop();
                }
            }

        }
    };

    private Runnable mUpdateOrientationRunnable = new Runnable() {
        @Override
        public void run() {
            if (getActivity() != null) {
                ((ViewerActivity) getActivity()).updateOrientation(width, height);
            }
        }
    };

    public ImageFragment() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    public static ImageFragment newInstance(String uri) {
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putString(URI, uri);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        this.isVisibleToUser = isVisibleToUser;

        if (isVisibleToUser) {
            if (!isOrientationUpdated) {
                updateOrientation();
            }
        } else {
            isOrientationUpdated = false;
        }
        mHandler.removeCallbacks(mUpdateGifRunnable);
        mHandler.postDelayed(mUpdateGifRunnable, 10);

    }

    private void updateOrientation() {
        mHandler.removeCallbacks(mUpdateOrientationRunnable);
        mHandler.postDelayed(mUpdateOrientationRunnable, 10);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mHandler.removeCallbacks(mUpdateGifRunnable);
        mHandler.removeCallbacks(mUpdateOrientationRunnable);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        DaggerViewerComponent.builder()
                .appModule(new AppModule((Application) getActivity().getApplication()))
                .viewerModule(new ViewerModule((ViewerActivity) getActivity()))
                .build().inject(this);

        View view = inflater.inflate(R.layout.fragment_viewer, container, false);

        image = (PhotoView) view.findViewById(R.id.image);
        String uri = getArguments().getString(URI);

        if (uri != null && uri.toLowerCase().endsWith("gif")) {
            mGifRequestBuilder.load(mDataSource.getMedia(uri))
                    .listener(new MediaRequestListener<Media, GifDrawable>())
                    .into(image);
        } else {
            mRequestManager.load(mDataSource.getMedia(uri))
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .listener(new MediaRequestListener<Media, GlideDrawable>())
                    .into(image);

        }

        image.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                ViewerActivity out = (ViewerActivity) getActivity();
                if (!out.isSystemUIVisible()) {
                    out.showSystemUI(true);
                } else {
                    out.hideSystemUIDelayed(0);
                }
            }
        });

        return view;
    }

    private class MediaRequestListener<T, V extends Drawable> implements RequestListener<T, V> {

        @Override
        public boolean onException(Exception e, T model, Target<V> target,
                boolean isFirstResource) {
            return false;
        }

        @Override
        public boolean onResourceReady(final V resource, T model, Target<V> target,
                boolean isFromMemoryCache,
                boolean isFirstResource) {
            width = resource.getIntrinsicWidth();
            height = resource.getIntrinsicHeight();

            if (isVisibleToUser && !isOrientationUpdated) {
                updateOrientation();
            }

            if (resource instanceof Animatable) {
                mHandler.removeCallbacks(mUpdateGifRunnable);
                mHandler.postDelayed(mUpdateGifRunnable, 10);
            }
            return false;
        }
    }
}