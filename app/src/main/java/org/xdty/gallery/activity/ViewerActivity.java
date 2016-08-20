package org.xdty.gallery.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import org.xdty.gallery.R;
import org.xdty.gallery.application.Application;
import org.xdty.gallery.contract.ViewerContact;
import org.xdty.gallery.di.DaggerViewerComponent;
import org.xdty.gallery.di.modules.AppModule;
import org.xdty.gallery.di.modules.ViewerModule;
import org.xdty.gallery.model.Media;
import org.xdty.gallery.view.PagerAdapter;

import java.util.List;

import javax.inject.Inject;

import static org.xdty.gallery.utils.Constants.HOST;
import static org.xdty.gallery.utils.Constants.POSITION;
import static org.xdty.gallery.utils.Constants.URI;

public class ViewerActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener,
        ViewerContact.View {

    public static final String TAG = ViewerActivity.class.getSimpleName();

    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    @Inject
    protected ViewerContact.Presenter mPresenter;

    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private Handler mHandler = new Handler();
    private Runnable hideSystemUIRunnable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerViewerComponent.builder()
                .appModule(new AppModule((Application) getApplication()))
                .viewerModule(new ViewerModule(this))
                .build().inject(this);

        setContentView(R.layout.activity_viewer);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mViewPager = (ViewPager) findViewById(R.id.container);

        setSupportActionBar(mToolbar);

        int position = getIntent().getIntExtra(POSITION, 0);
        String uri = getIntent().getStringExtra(URI);
        String host = getIntent().getStringExtra(HOST);

        mPagerAdapter = new PagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(this);

        hideSystemUIDelayed(0);

        mPresenter.loadData(uri, host, position);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_viewer, menu);
        return true;
    }

    @Override
    public void replaceData(List<Media> medias) {
        mPagerAdapter.replaceData(medias);
    }

    @Override
    public void setCurrentItem(int position) {
        mViewPager.setCurrentItem(position, false);
    }

    @Override
    public void setTitle(String name) {
        super.setTitle(name);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Snackbar.make(mToolbar, "Settings", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
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

    @Override
    public void cancelHideSystemUIDelayed() {
        if (hideSystemUIRunnable != null) {
            mHandler.removeCallbacks(hideSystemUIRunnable);
        }
    }

    @Override
    public boolean isSystemUIVisible() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            return (getWindow().getDecorView().getSystemUiVisibility() &
                    View.SYSTEM_UI_FLAG_LOW_PROFILE) == 0;
        } else {
            return (getWindow().getDecorView().getSystemUiVisibility() &
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0;
        }
    }

    @Override
    public void showSystemUI(boolean autoHide) {

        cancelHideSystemUIDelayed();

        //        appBar.setVisibility(View.VISIBLE);

        if (mToolbar.getVisibility() == View.INVISIBLE) {
            mToolbar.setVisibility(View.VISIBLE);
        }

        mToolbar.animate()
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator())
                .start();

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
            hideSystemUIDelayed(AUTO_HIDE_DELAY_MILLIS);
        }
    }

    @Override
    public void hideSystemUI() {

        //        appBar.setVisibility(View.GONE);
        mToolbar.animate().translationY(-mToolbar.getBottom()).setInterpolator(
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
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(POSITION, mPresenter.getPosition());
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        mPagerAdapter.clear();
        mViewPager.clearOnPageChangeListeners();
        mViewPager.setAdapter(null);
        mPagerAdapter = null;
        mPresenter.clear();
        mPresenter = null;
        super.onDestroy();
    }

    @Override
    public void updateOrientation(int width, int height) {

        if (width == height) {
            return;
        }

        int orientation = getRequestedOrientation();
        if (width <= height && orientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else if (width > height && orientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        mPresenter.pageSelected(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void setPresenter(ViewerContact.Presenter presenter) {

    }

}
