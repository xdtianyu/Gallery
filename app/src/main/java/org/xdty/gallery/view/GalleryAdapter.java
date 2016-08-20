package org.xdty.gallery.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.xdty.gallery.R;
import org.xdty.gallery.contract.MainContact;
import org.xdty.gallery.model.Media;

import java.util.ArrayList;
import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<ViewHolder> {

    public final static String TAG = GalleryAdapter.class.getSimpleName();

    private static final int TYPE_MEDIA = 1000;

    private List<Media> mMedias;
    private MainContact.Presenter mPresenter;

    public GalleryAdapter(MainContact.Presenter presenter) {
        mMedias = new ArrayList<>();
        mPresenter = presenter;
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

    public void replaceData(List<Media> mediaList) {
        mMedias = mediaList;
        notifyDataSetChanged();
    }

    private class MediaViewHolder extends RecyclerView.ViewHolder implements IViewHolder {

        SquareImageView thumbnail;
        TextView name;
        int position;
        Media mediaFile;

        private Context mContext;

        MediaViewHolder(View view) {
            super(view);

            mContext = view.getContext();

            thumbnail = (SquareImageView) view.findViewById(R.id.thumbnail);
            thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPresenter.clickItem(position, mediaFile);
                }
            });

            name = (TextView) view.findViewById(R.id.name);
        }

        @Override
        public void bindViews(int position) {
            this.position = position;
            mediaFile = mMedias.get(position);

            name.setText(mediaFile.getName());

            if (mediaFile.isImage()) {
                Glide.with(mContext).load(mediaFile)
                        .asBitmap()
                        .format(DecodeFormat.PREFER_RGB_565)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .fitCenter().centerCrop().into(thumbnail);
                name.setVisibility(View.GONE);
            } else {
                Glide.with(mContext).load(mediaFile)
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
    }
}
