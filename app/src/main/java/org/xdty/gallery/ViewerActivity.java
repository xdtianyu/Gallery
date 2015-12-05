package org.xdty.gallery;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.BitmapFactory.Options;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.StreamEncoder;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.xdty.gallery.glide.BitmapSizeDecoder;
import org.xdty.gallery.glide.MediaLoader;
import org.xdty.gallery.glide.MediaRequestListener;
import org.xdty.gallery.model.Media;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jcifs.smb.SmbException;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

@EActivity(R.layout.activity_viewer)
@OptionsMenu(R.menu.menu_viewer)
public class ViewerActivity extends AppCompatActivity {

    public static final String TAG = ViewerActivity.class.getSimpleName();

    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private static final int UI_ANIMATION_DELAY = 300;
    static GenericRequestBuilder<Media, InputStream, Options, Options> glideSizeRequest;
    //    @ViewById(R.id.main_content)
//    CoordinatorLayout coordinatorLayout;
//    @ViewById
//    AppBarLayout appBar;
    @ViewById
    Toolbar toolbar;
    //    @ViewById
//    FloatingActionButton fab;
    @ViewById(R.id.container)
    ViewPager viewPager;
    PagerAdapter mPagerAdapter;
    private List<Media> mMedias = new ArrayList<>();
    private Handler mHandler = new Handler();
    private Runnable hideSystemUIRunnable;

    @AfterViews
    protected void initViews() {

        int position = getIntent().getIntExtra("position", 0);
        String uri = getIntent().getStringExtra("uri");
        String host = getIntent().getStringExtra("host");

        setSupportActionBar(toolbar);

        mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), mMedias);
        viewPager.setAdapter(mPagerAdapter);

        glideSizeRequest = Glide.with(this)
                .using(new MediaLoader(this), InputStream.class)
                .from(Media.class)
                .as(Options.class)
                .sourceEncoder(new StreamEncoder())
                .cacheDecoder(new BitmapSizeDecoder())
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .listener(new MediaRequestListener());

        loadData(uri, host, position);

        hideSystemUIDelayed(0);
    }

    @Background
    void loadData(String uri, String host, int position) {
        try {
            notifyDataSetChanged(Arrays.asList(new Media(uri, host).listMedia()));
        } catch (MalformedURLException | SmbException e) {
            e.printStackTrace();
        }
        setCurrentItem(position);
    }

    @UiThread
    void notifyDataSetChanged(List<Media> medias) {
        mMedias.clear();
        mMedias.addAll(medias);
        mPagerAdapter.notifyDataSetChanged();
    }

//    @Click
//    void fab(View view) {
//        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show();
//    }

    @UiThread
    void setCurrentItem(int position) {
        viewPager.setCurrentItem(position, false);
    }

    @OptionsItem(R.id.action_settings)
    void settingSelected() {
        Snackbar.make(toolbar, "Settings", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @UiThread
    public void hideSystemUIDelayed(int timeout) {

        if (hideSystemUIRunnable == null) {
            hideSystemUIRunnable = new Runnable() {
                @Override
                public void run() {
                    hideSystemUI();
                }
            };
        }

        mHandler.removeCallbacks(hideSystemUIRunnable);
        mHandler.postDelayed(hideSystemUIRunnable, timeout);
    }

    @UiThread
    public void cancelHideSystemUIDelayed() {
        if (hideSystemUIRunnable != null) {
            mHandler.removeCallbacks(hideSystemUIRunnable);
        }
    }

    public boolean isSystemUIVisible() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            return (getWindow().getDecorView().getSystemUiVisibility() &
                    View.SYSTEM_UI_FLAG_LOW_PROFILE) == 0;
        } else {
            return (getWindow().getDecorView().getSystemUiVisibility() &
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0;
        }
    }

    @UiThread
    public void showSystemUI(boolean autoHide) {

        cancelHideSystemUIDelayed();

//        appBar.setVisibility(View.VISIBLE);

        if (toolbar.getVisibility() == View.INVISIBLE) {
            toolbar.setVisibility(View.VISIBLE);
        }

        toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();

        int flags = 0;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            flags = View.SYSTEM_UI_FLAG_VISIBLE;

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        }
        getWindow().getDecorView().setSystemUiVisibility(flags);

        if (autoHide) {
            hideSystemUIDelayed(3000);
        }
    }

    @UiThread
    public void hideSystemUI() {

//        appBar.setVisibility(View.GONE);
        toolbar.animate().translationY(-toolbar.getBottom()).setInterpolator(
                new AccelerateInterpolator()).start();

        int flags = 0;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            flags = View.SYSTEM_UI_FLAG_LOW_PROFILE;

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            flags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            flags = flags | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

        getWindow().getDecorView().setSystemUiVisibility(flags);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Background
    public void updateOrientation(int width, int height) {

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int orientation = getRequestedOrientation();
        if (width <= height && orientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else if (width > height && orientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    public static class ImageFragment extends Fragment {
        private static final String URI = "uri";

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
                    Log.d(TAG, "aaa: " + width + "x" + height);
                    updateOrientation();
                }
            } else {
                isOrientationUpdated = false;
            }
        }

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
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
            String uri = getArguments().getString(URI);

            Glide.with(getContext()).load(Media.fromUri(uri)).fitCenter().into(image);

            glideSizeRequest.load(Media.fromUri(uri)).into(new SimpleTarget<Options>() {
                @Override
                public void onResourceReady(Options resource,
                        GlideAnimation glideAnimation) {
                    width = resource.outWidth;
                    height = resource.outHeight;

                    if (isVisibleToUser && !isOrientationUpdated) {
                        updateOrientation();
                    }
                }
            })
            ;

            image.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float x, float y) {
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

    private class PagerAdapter extends FragmentStatePagerAdapter {

        List<Media> mediaList;

        public PagerAdapter(FragmentManager fm, List<Media> medias) {
            super(fm);
            mediaList = medias;
        }

        @Override
        public Fragment getItem(int position) {
            return ImageFragment.newInstance(mediaList.get(position).getUri());
        }

        @Override
        public int getCount() {
            return mMedias.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mMedias.get(position).getName();
        }
    }

}
