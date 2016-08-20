package org.xdty.gallery.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.xdty.gallery.fragment.ImageFragment;
import org.xdty.gallery.model.Media;

import java.util.List;

public class PagerAdapter extends FragmentStatePagerAdapter {

    private final List<Media> mMediaFiles;

    public PagerAdapter(FragmentManager fm, List<Media> medias) {
        super(fm);
        mMediaFiles = medias;
    }

    @Override
    public Fragment getItem(int position) {
        return ImageFragment.newInstance(mMediaFiles.get(position).getUri());
    }

    @Override
    public int getCount() {
        return mMediaFiles.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mMediaFiles.get(position).getName();
    }
}