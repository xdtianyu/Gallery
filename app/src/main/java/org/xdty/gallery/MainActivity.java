package org.xdty.gallery;

import android.content.Intent;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

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
import org.xdty.gallery.model.Samba;
import org.xdty.gallery.model.WebDav;
import org.xdty.gallery.view.GalleryAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jcifs.smb.SmbException;

@EActivity(R.layout.activity_main)
@OptionsMenu(R.menu.menu_main)
public class MainActivity extends AppCompatActivity implements GalleryAdapter.OnItemClickListener {

    public final static String TAG = MainActivity.class.getSimpleName();

    static Picasso.Builder mPicassoBuilder;

    @ViewById
    Toolbar toolbar;
    @ViewById
    SwipeRefreshLayout swipeRefreshLayout;
    @ViewById
    RecyclerView recyclerView;
    GalleryAdapter galleryAdapter;
    boolean isRoot = false;
    private List<Media> mMediaList = new ArrayList<>();
    private List<Media> mHistoryTree = new ArrayList<>();

    @AfterViews
    protected void initViews() {
        setSupportActionBar(toolbar);

        if (mPicassoBuilder == null) {
            mPicassoBuilder = new Picasso.Builder(this)
                    .addRequestHandler(new SambaRequestHandler())
                    .addRequestHandler(new DavRequestHandler());
            Picasso.setSingletonInstance(mPicassoBuilder.build());
        }

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        galleryAdapter = new GalleryAdapter(this, mMediaList, this);
        recyclerView.setAdapter(galleryAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Picasso.with(recyclerView.getContext()).resumeTag(recyclerView.getContext());
                } else {
                    Picasso.with(recyclerView.getContext()).pauseTag(recyclerView.getContext());
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

        Samba.add("192.168.2.150", "YOUR_SHARE_FOLDER", "YOUR_USER", "YOUR_PASSWORD");
        WebDav.add("davs://www.example.com/usb/", "YOUR_USER", "YOUR_PASSWORD");

        loadRootDir();
    }

    @Background
    void loadRootDir() {

        mMediaList.clear();

        String localRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
        File root = new File(localRoot);

        mMediaList.add(new Media(root));
        mMediaList.add(new Media(Samba.root("192.168.2.150")));
        mMediaList.add(new Media(Samba.root("192.168.2.110")));
        mMediaList.add(new Media(WebDav.root("davs://www.example.com")));

        notifyListChanged();

        isRoot = true;
    }

    @Background
    void loadDir(Media media) {
        try {
            List<Media> medias = Arrays.asList(media.listMedia());
            mMediaList.clear();

            for (Media m : medias) {
                if (m.isDirectory() || m.isImage()) {
                    mMediaList.add(m);
                }
            }

            notifyListChanged();

            if (!mHistoryTree.contains(media)) {
                mHistoryTree.add(media);
            }

            isRoot = false;
        } catch (SmbException e) {
            e.printStackTrace();
            Snackbar.make(toolbar, "Error", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
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
    public void onItemClicked(int position, Media media) {
        if (media.isDirectory()) {
            loadDir(media);
        } else {
            Intent intent = new Intent(this, ViewerActivity_.class);
            intent.putExtra("uri", media.getParent());
            intent.putExtra("host", media.getHost());
            intent.putExtra("position", position);
            startActivity(intent);
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
                Media media = mHistoryTree.get(mHistoryTree.size() - 1);
                loadDir(media);
            }
        }
    }
}
