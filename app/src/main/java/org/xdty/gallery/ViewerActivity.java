package org.xdty.gallery;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.squareup.picasso.Picasso;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.xdty.gallery.model.Media;

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

    @ViewById
    AppBarLayout appBar;
    @ViewById
    Toolbar toolbar;
    @ViewById
    FloatingActionButton fab;
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

        loadData(uri, host, position);

        hideSystemUIDelayed(3000);
    }

    @Background
    void loadData(String uri, String host, int position) {
        try {
            mMedias.clear();
            mMedias.addAll(Arrays.asList(new Media(uri, host).listMedia()));
            notifyDataSetChanged();
        } catch (MalformedURLException | SmbException e) {
            e.printStackTrace();
        }
        setCurrentItem(position);
    }

    @UiThread
    void notifyDataSetChanged() {
        mPagerAdapter.notifyDataSetChanged();
    }

    @UiThread
    void setCurrentItem(int position) {
        viewPager.setCurrentItem(position);
    }

    @Click
    void fab(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @OptionsItem(R.id.action_settings)
    void settingSelected() {
        Snackbar.make(toolbar, "Settings", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @UiThread
    public void showSystemUI() {

        cancelHideSystemUIDelayed();

        appBar.setVisibility(View.VISIBLE);
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

    private void hideSystemUI() {

        appBar.setVisibility(View.GONE);
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

    public static class ImageFragment extends Fragment {
        private static final String URI = "uri";

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
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_viewer, container, false);

            PhotoView image = (PhotoView) view.findViewById(R.id.image);
            String uri = getArguments().getString(URI);
            Picasso.with(getContext()).load(uri).fit().centerInside().into(image);

            image.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float x, float y) {
                    ViewerActivity out = (ViewerActivity)getActivity();
                    if (!out.isSystemUIVisible()) {
                        out.showSystemUI();
                        out.hideSystemUIDelayed(3000);
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
