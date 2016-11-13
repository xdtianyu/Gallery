package org.xdty.gallery.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import org.xdty.gallery.fragment.ImageFragment;
import org.xdty.gallery.model.Media;

import java.util.ArrayList;
import java.util.List;

public class PagerAdapter extends FragmentStatePagerAdapter {

    private final List<Media> mMedias;

    public PagerAdapter(FragmentManager fm) {
        super(fm);
        mMedias = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int position) {
        Log.e("xxx", "getItem: " + position + " -> " + mMedias.get(position).getUri());
        return ImageFragment.newInstance(mMedias.get(position).getUri());
    }

    @Override
    public int getCount() {
        return mMedias.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mMedias.get(position).getName();
    }

    public void load(Media media) {
        mMedias.clear();
        mMedias.add(media);
    }

    public void replaceData(List<Media> medias) {
        mMedias.clear();
        mMedias.addAll(medias);
    }

    public void clear() {
        mMedias.clear();
    }
}