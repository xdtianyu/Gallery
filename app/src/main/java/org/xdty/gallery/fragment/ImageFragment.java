package org.xdty.gallery.fragment;

import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import org.xdty.gallery.view.gesture.RotateGestureDetector;

import java.io.InputStream;

import javax.inject.Inject;

import pl.droidsonroids.gif.GifDrawable;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

import static org.xdty.gallery.utils.Constants.URI;

public class ImageFragment extends Fragment implements ViewerActivity.TouchEventListener {

    private static final String TAG = ImageFragment.class.getSimpleName();

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
    private PhotoView mPhotoView;

    private String mUri;

    private Handler mHandler;

    private RotateGestureDetector mRotationDetector;

    private Runnable mUpdateGifRunnable = new Runnable() {
        @Override
        public void run() {
            if (mPhotoView != null && mPhotoView.getDrawable() instanceof Animatable) {
                Animatable animatable = (Animatable) mPhotoView.getDrawable();
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

    private RotateGestureDetector.OnRotateGestureListener mOnRotateGestureListener =
            new RotateGestureDetector.OnRotateGestureListener() {
                int rotate = 0;

                @Override
                public boolean onRotate(RotateGestureDetector detector) {

                    if (mPhotoView != null) {
                        float degree = detector.getRotationDegreesDelta();
                        mPhotoView.setRotation(-degree + rotate);
                    }
                    return false;
                }

                @Override
                public boolean onRotateBegin(RotateGestureDetector detector) {
                    rotate = mDataSource.getRotate(mUri);
                    return true;
                }

                @Override
                public void onRotateEnd(RotateGestureDetector detector) {
                    if (mPhotoView != null) {
                        // set rotation and save state
                        float degree = -detector.getRotationDegreesDelta() + rotate;

                        int n = Math.round(degree / 90);
                        n = n % 4;
                        if (n < 0) {
                            n += 4;
                        }
                        switch (n) {
                            case 0:
                                mPhotoView.setRotation(0);
                                break;
                            case 1:
                                mPhotoView.setRotation(90);
                                break;
                            case 2:
                                mPhotoView.setRotation(180);
                                break;
                            case 3:
                                mPhotoView.setRotation(270);
                                break;
                        }
                        mDataSource.setRotate(mUri, (int) mPhotoView.getRotation());
                        rotate = 0;
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
            if (getActivity() != null) {
                ((ViewerActivity) getActivity()).setTouchEventListener(this);
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

        mRotationDetector = new RotateGestureDetector(getActivity(), mOnRotateGestureListener);

        mPhotoView = (PhotoView) view.findViewById(R.id.image);

        mUri = getArguments().getString(URI);

        if (mUri != null && mUri.toLowerCase().endsWith("gif")) {
            mGifRequestBuilder.load(mDataSource.getMedia(mUri))
                    .listener(new MediaRequestListener<Media, GifDrawable>())
                    .into(mPhotoView);
        } else {
            mRequestManager.load(mDataSource.getMedia(mUri))
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .listener(new MediaRequestListener<Media, GlideDrawable>())
                    .into(mPhotoView);

        }

        mPhotoView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
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

        mPhotoView.setRotation(mDataSource.getRotate(mUri));
        return view;
    }

    @Override
    public boolean onDispatchTouchEvent(MotionEvent motionEvent) {
        Log.e(TAG, motionEvent.toString());
        return mRotationDetector.onTouchEvent(motionEvent);
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

                if (getActivity() != null) {
                    ((ViewerActivity) getActivity()).setTouchEventListener(ImageFragment.this);
                }
            }

            if (resource instanceof Animatable) {
                mHandler.removeCallbacks(mUpdateGifRunnable);
                mHandler.postDelayed(mUpdateGifRunnable, 10);
            }
            return false;
        }
    }
}