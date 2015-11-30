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
    final List<Media> mMedias = new ArrayList<>();
    @ViewById
    Toolbar toolbar;
    @ViewById
    FloatingActionButton fab;
    @ViewById(R.id.container)
    ViewPager viewPager;
    SectionsPagerAdapter mSectionsPagerAdapter;

    @AfterViews
    protected void initViews() {

        mPosition = getIntent().getIntExtra("position", 0);
        mUri = getIntent().getStringExtra("uri");
        mHost = getIntent().getStringExtra("host");

        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mSectionsPagerAdapter);

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
        mSectionsPagerAdapter.notifyDataSetChanged();
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

    public class PlaceholderFragment extends Fragment {
        private static final String POSITION = "position";

        public PlaceholderFragment() {
        }

        public PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();

            args.putInt(POSITION, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_viewer, container, false);
            PhotoView imageView = (PhotoView) rootView.findViewById(R.id.image);

            int position = getArguments().getInt(POSITION);

            Picasso.with(getContext()).load(mMedias.get(position).getUri())
                    .fit()
                    .centerInside()
                    .into(imageView);
//            textView.setText(
//                    getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }


    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        PlaceholderFragment placeholderFragment;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            placeholderFragment = new PlaceholderFragment();
        }

        @Override
        public Fragment getItem(int position) {
            return placeholderFragment.newInstance(position);
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
