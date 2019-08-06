package org.xdty.gallery.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.xdty.gallery.R;
import org.xdty.gallery.application.Application;
import org.xdty.gallery.contract.MainContact;
import org.xdty.gallery.di.DaggerMainComponent;
import org.xdty.gallery.di.modules.AppModule;
import org.xdty.gallery.di.modules.MainModule;
import org.xdty.gallery.model.Media;
import org.xdty.gallery.utils.Constants;
import org.xdty.gallery.utils.Utils;
import org.xdty.gallery.view.GalleryAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity implements MainContact.View,
        GalleryAdapter.ItemClickListener {

    public final static String TAG = MainActivity.class.getSimpleName();
    public final static int REQUEST_POSITION = 1000;

    @Inject
    MainContact.Presenter mMainPresenter;
    @Inject
    RequestManager mGlideRequest;

    private Toolbar mToolbar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private FloatingActionButton mFab;
    private GalleryAdapter mGalleryAdapter;
    private GridLayoutManager mGridLayoutManager;
    private Runnable mRefreshDelayedRunnable = new Runnable() {
        @Override
        public void run() {
            mSwipeRefreshLayout.setRefreshing(true);
            mGalleryAdapter.clear();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerMainComponent.builder()
                .appModule(new AppModule((Application) getApplication()))
                .mainModule(new MainModule(MainActivity.this))
                .build()
                .inject(this);

        Utils.checkLocale(getBaseContext());

        setupViews();

        mMainPresenter.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGlideRequest.resumeRequests();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGlideRequest.pauseRequests();
    }

    private void setupViews() {

        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mFab = (FloatingActionButton) findViewById(R.id.fab);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddServerDialog();
            }
        });

        mGridLayoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mGalleryAdapter = new GalleryAdapter(this);
        mGalleryAdapter.setItemClickListener(this);
        mRecyclerView.setAdapter(mGalleryAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    mGlideRequest.pauseRequests();
                } else {
                    mGlideRequest.resumeRequests();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // can scroll up and disable refresh
                if (recyclerView.canScrollVertically(-1)) {
                    mSwipeRefreshLayout.setEnabled(false);
                } else {
                    mSwipeRefreshLayout.setEnabled(true);
                }

                // can not scroll down and load more
                //                if (!mRecyclerView.canScrollVertically(1)) {
                //
                //                }
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mMainPresenter.reFresh();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void replaceData(List<Media> mediaList) {
        mGalleryAdapter.replaceData(mediaList);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void startViewer(int position, Media media) {
        Glide.with(this).pauseRequests();
        Intent intent = new Intent(this, ViewerActivity.class);
        intent.putExtra(Constants.URI, media.getUri());
        intent.putExtra(Constants.PARENT, media.getParent());
        intent.putExtra(Constants.HOST, media.getHost());
        intent.putExtra(Constants.POSITION, position);

        int first = mGridLayoutManager.findFirstVisibleItemPosition();
        int last = mGridLayoutManager.findLastVisibleItemPosition();

        List<Pair<View, String>> views = new ArrayList<>();

        for (int i = first; i <= last; i++) {
            View thumbnail = mRecyclerView.findViewHolderForAdapterPosition(i).itemView.findViewById(R.id.thumbnail);
            String name = ViewCompat.getTransitionName(thumbnail);
            if (name != null) {
                views.add(new Pair<>(thumbnail, name));
            }
        }
        Pair<?, ?>[] paris = new Pair<?, ?>[views.size()];
        views.toArray(paris);

        @SuppressWarnings("unchecked")
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                (Pair<View, String>[]) paris);

        startActivityForResult(intent, REQUEST_POSITION, options.toBundle());
    }

    @Override
    public void showLoading(boolean isLoading) {
        if (isLoading) {
            mSwipeRefreshLayout.postDelayed(mRefreshDelayedRunnable, 200);
        } else {
            mSwipeRefreshLayout.removeCallbacks(mRefreshDelayedRunnable);
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    void showAddServerDialog() {
        View layout = View.inflate(this, R.layout.dialog_add_server, null);

        final EditText uriText = (EditText) layout.findViewById(R.id.uri);
        final EditText userText = (EditText) layout.findViewById(R.id.username);
        final EditText passText = (EditText) layout.findViewById(R.id.password);

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(R.string.add_server)
                .setView(layout)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // save server info
                        String uri = uriText.getText().toString();
                        String user = userText.getText().toString();
                        String pass = passText.getText().toString();

                        mMainPresenter.addServer(uri, user, pass);

                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .setNeutralButton(R.string.add_server_help, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.data_source:
                startActivity(new Intent(this, DataSourceActivity.class));
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_POSITION:
                int position = data.getIntExtra("position", 0);
                scrollToPosition(position);
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void scrollToPosition(int position) {
        Log.d(TAG, "position: " + position);
        int start = mGridLayoutManager.findFirstCompletelyVisibleItemPosition();
        int end = mGridLayoutManager.findLastCompletelyVisibleItemPosition();

        if (position < start || position > end) {
            mGridLayoutManager.scrollToPositionWithOffset(position, 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMainPresenter.clear();
        mGlideRequest.onLowMemory();
        System.gc();
    }

    @Override
    public void onBackPressed() {

        mRecyclerView.computeVerticalScrollOffset();

        if (!mMainPresenter.loadParent(mGridLayoutManager.findFirstVisibleItemPosition())) {
            super.onBackPressed();
        }
    }

    @Override
    public void setTitle(String title) {
        if (title == null) {
            title = getResources().getString(R.string.app_name);
        }
        super.setTitle(title);
    }

    @Override
    public void setPresenter(MainContact.Presenter presenter) {

    }

    @Override
    public void onItemClick(int position, Media media) {
        mMainPresenter.clickItem(position, media,
                mGridLayoutManager.findFirstVisibleItemPosition());
    }
}
