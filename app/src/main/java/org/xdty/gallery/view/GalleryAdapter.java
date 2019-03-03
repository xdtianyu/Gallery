package org.xdty.gallery.view;

import android.app.Activity;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.xdty.gallery.R;
import org.xdty.gallery.model.Media;
import org.xdty.gallery.model.media.LocalMedia;

import java.util.ArrayList;
import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<ViewHolder> {

    private final static String TAG = GalleryAdapter.class.getSimpleName();

    private static final int TYPE_MEDIA = 1000;

    private List<Media> mMedias = new ArrayList<>();
    private ItemClickListener mItemClickListener;

    private RequestManager mRequestManager;

    public GalleryAdapter(Activity activity) {
        mRequestManager = Glide.with(activity);
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

            DiskCacheStrategy strategy = DiskCacheStrategy.ALL;

            if (mediaFile instanceof LocalMedia) {
                strategy = DiskCacheStrategy.RESULT;
            }

            Glide.clear(thumbnail);

            if (mediaFile.isImage()) {
                mRequestManager.load(mediaFile)
                        .asBitmap()
                        .format(DecodeFormat.PREFER_RGB_565)
                        .diskCacheStrategy(strategy)
                        .placeholder(R.color.gray_overlay)
                        .fitCenter().centerCrop().into(thumbnail);
                name.setVisibility(View.GONE);
                ViewCompat.setTransitionName(thumbnail, mediaFile.getName());
            } else {
                mRequestManager.load(mediaFile)
                        .asBitmap()
                        .format(DecodeFormat.PREFER_RGB_565)
                        .diskCacheStrategy(strategy)
                        .placeholder(R.drawable.folder)
                        .error(R.drawable.folder)
                        .dontAnimate()
                        .fitCenter().centerCrop().into(thumbnail);
                name.setVisibility(View.VISIBLE);
                ViewCompat.setTransitionName(thumbnail, null);
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
