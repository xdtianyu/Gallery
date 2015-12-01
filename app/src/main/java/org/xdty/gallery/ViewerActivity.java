package org.xdty.gallery;

import android.os.Bundle;
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

@EActivity(R.layout.activity_viewer)
@OptionsMenu(R.menu.menu_viewer)
public class ViewerActivity extends AppCompatActivity {

    private static String mUri = "";
    private static String mHost = "";
    private static int mPosition = 0;
    @ViewById
    Toolbar toolbar;
    @ViewById
    FloatingActionButton fab;
    @ViewById(R.id.container)
    ViewPager viewPager;
    PagerAdapter mPagerAdapter;
    private List<Media> mMedias = new ArrayList<>();

    @AfterViews
    protected void initViews() {

        mPosition = getIntent().getIntExtra("position", 0);
        mUri = getIntent().getStringExtra("uri");
        mHost = getIntent().getStringExtra("host");

        setSupportActionBar(toolbar);
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), mMedias);

        viewPager.setAdapter(mPagerAdapter);

        loadData();
    }

    @Background
    void loadData() {
        try {
            mMedias.clear();
            mMedias.addAll(Arrays.asList(new Media(mUri, mHost).listMedia()));
            notifyDataSetChanged();
        } catch (MalformedURLException | SmbException e) {
            e.printStackTrace();
        }
        setCurrentItem(mPosition);
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
            View rootView = inflater.inflate(R.layout.fragment_viewer, container, false);
            PhotoView imageView = (PhotoView) rootView.findViewById(R.id.image);

            String uri = getArguments().getString(URI);
            Picasso.with(getContext()).load(uri)
                    .fit()
                    .centerInside()
                    .into(imageView);
//            textView.setText(
//                    getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
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
            return "" + position;
        }
    }
}
