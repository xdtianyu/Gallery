package org.xdty.gallery.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.xdty.gallery.R;
import org.xdty.gallery.model.Media;

import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<ViewHolder> {

    public final static String TAG = GalleryAdapter.class.getSimpleName();

    private static final int TYPE_MEDIA = 1000;

    private final LayoutInflater mLayoutInflater;
    private List<Media> mMediaFiles;

    private Context mContext;

    private OnItemClickListener mOnItemClickListener;

    public GalleryAdapter(Context context, List<Media> mediaFiles, OnItemClickListener listener) {
        mContext = context;
        mMediaFiles = mediaFiles;
        mOnItemClickListener = listener;

        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MediaViewHolder(mLayoutInflater.inflate(R.layout.item_media, parent, false));
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
        return mMediaFiles.size();
    }

    public interface OnItemClickListener {
        void onItemClicked(int position, Media mediaFile);
    }

    public class MediaViewHolder extends RecyclerView.ViewHolder implements IViewHolder {

        SquareImageView thumbnail;
        TextView name;

        int position;
        Media mediaFile;

        public MediaViewHolder(View view) {
            super(view);
            thumbnail = (SquareImageView) view.findViewById(R.id.thumbnail);
            thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClicked(position, mediaFile);
                }
            });

            name = (TextView) view.findViewById(R.id.name);
        }

        @Override
        public void bindViews(int position) {
            this.position = position;
            mediaFile = mMediaFiles.get(position);
//            Log.d(TAG, "path: " + mediaFile.getPath());

            name.setText(mediaFile.getName());

            if (mediaFile.isImage()) {
                Glide.with(mContext).load(
                        mediaFile).fitCenter().centerCrop().into(thumbnail);
                name.setVisibility(View.GONE);
            } else {
                Glide.with(mContext).load(mediaFile)
                        .placeholder(R.drawable.folder)
                        .error(R.drawable.folder)
                        .dontAnimate()
                        .fitCenter().centerCrop().into(thumbnail);
                name.setVisibility(View.VISIBLE);
            }
        }
    }
}
