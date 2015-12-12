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
import java.util.Arrays;
import java.util.List;

import static android.os.Environment.getExternalStorageDirectory;

@EActivity(R.layout.activity_main)
@OptionsMenu(R.menu.menu_main)
public class MainActivity extends AppCompatActivity implements GalleryAdapter.OnItemClickListener {

    public final static String TAG = MainActivity.class.getSimpleName();

    @ViewById
    Toolbar toolbar;
    @ViewById
    SwipeRefreshLayout swipeRefreshLayout;
    @ViewById
    RecyclerView recyclerView;
    GalleryAdapter galleryAdapter;
    boolean isRoot = false;
    private List<Media> mMediaFileList = new ArrayList<>();
    private List<Media> mHistoryTree = new ArrayList<>();

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

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        galleryAdapter = new GalleryAdapter(this, mMediaFileList, this);
        recyclerView.setAdapter(galleryAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Glide.with(recyclerView.getContext()).resumeRequests();
                } else {
                    Glide.with(recyclerView.getContext()).pauseRequests();
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
                    loadDir(mHistoryTree.get(mHistoryTree.size() - 1));
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
        mMediaFileList.addAll(Arrays.asList(Media.Builder.roots()));

        notifyListChanged();

        isRoot = true;
    }

    @Background
    void loadDir(Media media) {
        long start = System.currentTimeMillis();
        List<Media> medias = Arrays.asList((Media[]) media.listMedia());
        mMediaFileList.clear();

        for (Media m : medias) {
            if (m.isImage() || m.isDirectory()) {
                mMediaFileList.add(m);
            }
        }
        Log.e("aaa", "" + (System.currentTimeMillis() - start));

        notifyListChanged();

        if (!mHistoryTree.contains(media)) {
            mHistoryTree.add(media);
        }

        isRoot = false;
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
        Snackbar.make(toolbar, "Settings", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Override
    public void onItemClicked(int position, Media mediaFile) {
        if (mediaFile.isImage()) {
            Intent intent = new Intent(this, ViewerActivity_.class);
            intent.putExtra("uri", mediaFile.getParent());
            intent.putExtra("host", mediaFile.getHost());
            intent.putExtra("position", position);
            startActivity(intent);
        } else {
            loadDir(mediaFile);
        }
    }

    @Override
    public void onBackPressed() {
        if (isRoot) {
            super.onBackPressed();
        } else {
            mHistoryTree.remove(mHistoryTree.size() - 1);
            if (mHistoryTree.size() == 0) {
                loadRootDir();
            } else {
                Media mediaFile = mHistoryTree.get(mHistoryTree.size() - 1);
                loadDir(mediaFile);
            }
        }
    }
}
