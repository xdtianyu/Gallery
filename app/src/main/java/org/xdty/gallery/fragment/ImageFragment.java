package org.xdty.gallery.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.xdty.gallery.R;
import org.xdty.gallery.activity.ViewerActivity;
import org.xdty.gallery.model.Media;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

import static org.xdty.gallery.utils.Constants.URI;

public class ImageFragment extends Fragment {

    private boolean isOrientationUpdated = false;
    private boolean isVisibleToUser = false;
    private int width = -1;
    private int height = -1;

    public ImageFragment() {
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
    }

    private void updateOrientation() {
        if (getActivity() != null) {
            ((ViewerActivity) getActivity()).updateOrientation(width, height);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_viewer, container, false);

        PhotoView image = (PhotoView) view.findViewById(R.id.image);
        final String uri = getArguments().getString(URI);

        Glide.with(getContext()).load(Media.Builder.uri(uri))
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .listener(new RequestListener<Media, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, Media model,
                            Target<GlideDrawable> target,
                            boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, Media model,
                            Target<GlideDrawable> target, boolean isFromMemoryCache,
                            boolean isFirstResource) {
                        width = resource.getIntrinsicWidth();
                        height = resource.getIntrinsicHeight();
                        if (isVisibleToUser && !isOrientationUpdated) {
                            updateOrientation();
                        }
                        return false;
                    }
                })
                .fitCenter().into(image);

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