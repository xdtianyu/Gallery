package org.xdty.gallery;

import android.content.Intent;
import android.os.StrictMode;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.xdty.gallery.model.LocalMedia;
import org.xdty.gallery.model.Media;
import org.xdty.gallery.model.SambaMedia;
import org.xdty.gallery.model.WebDavMedia;
import org.xdty.gallery.view.GalleryAdapter;

import java.util.ArrayList;
import java.util.List;

import static android.os.Environment.getExternalStorageDirectory;

@EActivity(R.layout.activity_main)
@OptionsMenu(R.menu.menu_main)
public class MainActivity extends AppCompatActivity implements GalleryAdapter.OnItemClickListener {

    public final static String TAG = MainActivity.class.getSimpleName();
    public final static int REQUEST_POSITION = 1000;
    @ViewById
    Toolbar toolbar;
    @ViewById
    SwipeRefreshLayout swipeRefreshLayout;
    @ViewById
    RecyclerView recyclerView;
    GalleryAdapter galleryAdapter;
    GridLayoutManager gridLayoutManager;
    boolean isRoot = false;
    private List<Media> mMediaFileList = new ArrayList<>();

    @AfterViews
    protected void initViews() {

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());

        setSupportActionBar(toolbar);

        gridLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        galleryAdapter = new GalleryAdapter(this, mMediaFileList, this);
        recyclerView.setAdapter(galleryAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    Glide.with(recyclerView.getContext()).pauseRequests();
                } else {
                    Glide.with(recyclerView.getContext()).resumeRequests();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // can scroll up and disable refresh
                if (recyclerView.canScrollVertically(-1)) {
                    swipeRefreshLayout.setEnabled(false);
                } else {
                    swipeRefreshLayout.setEnabled(true);
                }

                // can not scroll down and load more
//                if (!recyclerView.canScrollVertically(1)) {
//
//                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isRoot) {
                    loadRootDir();
                } else {
                    loadDir(Media.Builder.getCurrent(), true);
                }
            }
        });

        try {
            Media.Builder.register(new LocalMedia());
            Media.Builder.register(new SambaMedia());
            Media.Builder.register(new WebDavMedia());

            Media.Builder.addRoot(getExternalStorageDirectory().getAbsolutePath(), null, null);
            Media.Builder.addRoot("smb://192.168.2.150/sdb1", "YOUR_USER", "YOUR_PASSWORD");
            Media.Builder.addRoot("smb://192.168.2.110/mnt", "YOUR_USER", "YOUR_PASSWORD");
            Media.Builder.addRoot("davs://www.example.com/usb", "YOUR_USER", "YOUR_PASSWORD");
        } catch (Exception e) {
            e.printStackTrace();
        }
        loadRootDir();
    }

    @Background
    void loadRootDir() {

        mMediaFileList.clear();
        mMediaFileList.addAll(Media.Builder.roots());

        Media.Builder.setCurrent(null);

        notifyListChanged();

        setTitle(getResources().getString(R.string.app_name));
        isRoot = true;
    }

    void loadDir(Media media) {
        loadDir(media, false);
    }

    @SuppressWarnings("unchecked")
    @Background
    void loadDir(Media media, boolean isRefresh) {
        long start = System.currentTimeMillis();

        if (isRefresh) {
            media.clear();
        }

        List<Media> medias = media.children();
        mMediaFileList.clear();

        for (Media m : medias) {
            if (m.isImage() || m.isDirectory()) {
                mMediaFileList.add(m);
            }
        }
        Log.e("aaa", "" + (System.currentTimeMillis() - start));

        notifyListChanged();

        isRoot = false;
        Media.Builder.setCurrent(media);

        setTitle(media.getName());
        scrollToPosition(media.getPosition());
    }

    @UiThread
    void scrollToPosition(int position) {
        Log.d(TAG, "position: " + position);
        gridLayoutManager.scrollToPositionWithOffset(position, 0);
    }

    @UiThread
    void notifyListChanged() {
        galleryAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Click
    void fab(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @OptionsItem(R.id.action_settings)
    void settingSelected() {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    @Override
    public void onItemClicked(int position, Media media) {
        if (media.isImage()) {
            Glide.with(this).pauseRequests();
            Intent intent = new Intent(this, ViewerActivity_.class);
            intent.putExtra("uri", media.getParent());
            intent.putExtra("host", media.getHost());
            intent.putExtra("position", position);
            startActivityForResult(intent, REQUEST_POSITION);
        } else {
            Media current = media.parent();
            if (current != null) {
                current.setPosition(gridLayoutManager.findFirstVisibleItemPosition());
            }

            loadDir(media);
        }
    }

    @OnActivityResult(REQUEST_POSITION)
    void onResult(@OnActivityResult.Extra int position) {
        Log.d(TAG, "position: " + position);
        int start = gridLayoutManager.findFirstCompletelyVisibleItemPosition();
        int end = gridLayoutManager.findLastCompletelyVisibleItemPosition();

        if (position < start || position > end) {
            scrollToPosition(position);
        }
    }

    @Override
    public void onBackPressed() {
        if (isRoot) {
            super.onBackPressed();
        } else {
            Media media = Media.Builder.getCurrent();
            if (media == null || media.parent() == null) {
                loadRootDir();
            } else {
                recyclerView.computeVerticalScrollOffset();
                media.setPosition(gridLayoutManager.findFirstVisibleItemPosition());
                loadDir(media.parent());
            }
        }
    }

    @UiThread
    void setTitle(String title) {
        super.setTitle(title);
    }
}
