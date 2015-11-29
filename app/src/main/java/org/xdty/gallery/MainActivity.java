package org.xdty.gallery;

import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

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
import org.xdty.gallery.view.GalleryAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jcifs.smb.SmbException;

@EActivity(R.layout.activity_main)
@OptionsMenu(R.menu.menu_main)
public class MainActivity extends AppCompatActivity implements GalleryAdapter.OnItemClickListener {

    @ViewById
    Toolbar toolbar;

    @ViewById
    SwipeRefreshLayout swipeRefreshLayout;

    @ViewById
    RecyclerView recyclerView;

    GalleryAdapter galleryAdapter;

    private List<Media> mMediaList = new ArrayList<>();

    @AfterViews
    protected void initViews() {
        setSupportActionBar(toolbar);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        galleryAdapter = new GalleryAdapter(this, mMediaList, this);
        recyclerView.setAdapter(galleryAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

        loadRootDir();
    }

    @Background
    void loadRootDir() {
        String localRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
        File root = new File(localRoot);
        if (root.isDirectory() || root.isFile()) {
            mMediaList.add(new Media(root));
        }

        Samba.add("192.168.2.150", "YOUR_SHARE_FOLDER", "YOUR_USER", "YOUR_PASSWORD");
        mMediaList.add(new Media(Samba.root("192.168.2.150")));

        notifyListChanged();
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
        } catch (SmbException e) {
            e.printStackTrace();
        }

        notifyListChanged();
    }

    @UiThread
    void notifyListChanged() {
        galleryAdapter.notifyDataSetChanged();
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
        }
    }
}
