package org.xdty.gallery.view;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.xdty.gallery.R;
import org.xdty.gallery.application.Application;
import org.xdty.gallery.model.Media;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class GalleryAdapter extends RecyclerView.Adapter<ViewHolder> {

    public final static String TAG = GalleryAdapter.class.getSimpleName();

    @Inject
    RequestManager mGlideRequest;

    private static final int TYPE_MEDIA = 1000;

    private List<Media> mMedias = new ArrayList<>();;
    private ItemClickListener mItemClickListener;

    public GalleryAdapter() {
        Application.getAppComponent().inject(this);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MediaViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_media, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ((IViewHolder) holder).bindViews(position);
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_MEDIA;
    }

    @Override
    public int getItemCount() {
        return mMedias.size();
    }

    public void clear() {
        mMedias.clear();
        notifyDataSetChanged();
    }

    public void replaceData(List<Media> mediaList) {
        mMedias = mediaList;
        notifyDataSetChanged();
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(int position, Media media);
    }

    private class MediaViewHolder extends RecyclerView.ViewHolder
            implements IViewHolder, View.OnClickListener {

        SquareImageView thumbnail;
        TextView name;
        int position;
        Media mediaFile;

        MediaViewHolder(View view) {
            super(view);

            thumbnail = (SquareImageView) view.findViewById(R.id.thumbnail);
            thumbnail.setOnClickListener(this);

            name = (TextView) view.findViewById(R.id.name);
        }

        @Override
        public void bindViews(int position) {
            this.position = position;
            mediaFile = mMedias.get(position);

            name.setText(mediaFile.getName());

            if (mediaFile.isImage()) {
                mGlideRequest.load(mediaFile)
                        .asBitmap()
                        .format(DecodeFormat.PREFER_RGB_565)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .fitCenter().centerCrop().into(thumbnail);
                name.setVisibility(View.GONE);
            } else {
                mGlideRequest.load(mediaFile)
                        .asBitmap()
                        .format(DecodeFormat.PREFER_RGB_565)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .placeholder(R.drawable.folder)
                        .error(R.drawable.folder)
                        .dontAnimate()
                        .fitCenter().centerCrop().into(thumbnail);
                name.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(position, mediaFile);
            }
        }
    }
}
