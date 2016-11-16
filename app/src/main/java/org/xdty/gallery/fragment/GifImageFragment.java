package org.xdty.gallery.fragment;

import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.StreamEncoder;
import com.bumptech.glide.load.resource.file.FileToStreamDecoder;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.xdty.gallery.R;
import org.xdty.gallery.activity.ViewerActivity;
import org.xdty.gallery.application.Application;
import org.xdty.gallery.data.MediaDataSource;
import org.xdty.gallery.di.DaggerViewerComponent;
import org.xdty.gallery.di.modules.AppModule;
import org.xdty.gallery.di.modules.ViewerModule;
import org.xdty.gallery.glide.GifDrawableBytesTranscoder;
import org.xdty.gallery.glide.MediaLoader;
import org.xdty.gallery.glide.StreamByteArrayResourceDecoder;
import org.xdty.gallery.model.Media;

import java.io.InputStream;

import javax.inject.Inject;

import pl.droidsonroids.gif.GifDrawable;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

import static org.xdty.gallery.utils.Constants.URI;

public class GifImageFragment extends Fragment {
    @Inject
    MediaDataSource mDataSource;
    @Inject
    RequestManager mRequestManager;
    GenericRequestBuilder<Media, InputStream, byte[], GifDrawable> glide;
    private boolean isOrientationUpdated = false;
    private boolean isVisibleToUser = false;
    private int width = -1;
    private int height = -1;
    private PhotoView image;

    public GifImageFragment() {
    }

    public static GifImageFragment newInstance(String uri) {
        GifImageFragment fragment = new GifImageFragment();
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

        if (image != null) {
            if (image.getDrawable() instanceof Animatable) {
                Animatable animatable = (Animatable) image.getDrawable();
                if (isVisibleToUser) {
                    animatable.start();
                } else {
                    animatable.stop();
                }
            }
        }
    }

    private void updateOrientation() {
        if (getActivity() != null) {
            ((ViewerActivity) getActivity()).updateOrientation(width, height);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        DaggerViewerComponent.builder()
                .appModule(new AppModule((Application) getActivity().getApplication()))
                .viewerModule(new ViewerModule((ViewerActivity) getActivity()))
                .build().inject(this);

        glide = Glide
                .with(this)
                .using(new MediaLoader(getActivity()), InputStream.class)
                .from(Media.class)
                .as(byte[].class)
                .transcode(new GifDrawableBytesTranscoder(), GifDrawable.class)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .decoder(new StreamByteArrayResourceDecoder())
                .sourceEncoder(new StreamEncoder())
                .cacheDecoder(new FileToStreamDecoder<>(new StreamByteArrayResourceDecoder()));

        View view = inflater.inflate(R.layout.fragment_viewer, container, false);

        image = (PhotoView) view.findViewById(R.id.image);
        String uri = getArguments().getString(URI);

        glide.load(mDataSource.getMedia(uri))
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .listener(new RequestListener<Media, GifDrawable>() {
                    @Override
                    public boolean onException(Exception e, Media model,
                            Target<GifDrawable> target,
                            boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(final GifDrawable resource, Media model,
                            Target<GifDrawable> target, boolean isFromMemoryCache,
                            boolean isFirstResource) {
                        width = resource.getIntrinsicWidth();
                        height = resource.getIntrinsicHeight();

                        image.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (!isVisibleToUser) {
                                    resource.stop();
                                }
                            }
                        }, 100);

                        if (isVisibleToUser && !isOrientationUpdated) {
                            updateOrientation();
                        }
                        return false;
                    }
                }).into(image);

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
}
