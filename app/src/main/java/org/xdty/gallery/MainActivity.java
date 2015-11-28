package org.xdty.gallery;

import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;
import org.xdty.gallery.view.GalleryAdapter;

@EActivity(R.layout.activity_main)
@OptionsMenu(R.menu.menu_main)
public class MainActivity extends AppCompatActivity {

    @ViewById
    Toolbar toolbar;

    @ViewById
    SwipeRefreshLayout swipeRefreshLayout;

    @ViewById
    RecyclerView recyclerView;

    GalleryAdapter galleryAdapter;

    @AfterViews
    protected void initViews() {
        setSupportActionBar(toolbar);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        galleryAdapter = new GalleryAdapter();
        recyclerView.setAdapter(galleryAdapter);
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
}
