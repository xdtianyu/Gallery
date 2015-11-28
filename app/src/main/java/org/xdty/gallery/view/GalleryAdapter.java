package org.xdty.gallery.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ty on 15-11-28.
 */
public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.MediaViewHolder> {

    @Override
    public GalleryAdapter.MediaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(MediaViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class MediaViewHolder extends RecyclerView.ViewHolder {

        public MediaViewHolder(View itemView) {
            super(itemView);
        }
    }
}
