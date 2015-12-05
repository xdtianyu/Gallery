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
    private List<Media> mMedias;

    private Context mContext;

    private OnItemClickListener mOnItemClickListener;

    public GalleryAdapter(Context context, List<Media> medias, OnItemClickListener listener) {
        mContext = context;
        mMedias = medias;
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
        return mMedias.size();
    }

    public interface OnItemClickListener {
        void onItemClicked(int position, Media media);
    }

    public class MediaViewHolder extends RecyclerView.ViewHolder implements IViewHolder {

        SquareImageView thumbnail;
        TextView name;

        int position;
        Media media;

        public MediaViewHolder(View view) {
            super(view);
            thumbnail = (SquareImageView) view.findViewById(R.id.thumbnail);
            thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClicked(position, media);
                }
            });

            name = (TextView) view.findViewById(R.id.name);
        }

        @Override
        public void bindViews(int position) {
            this.position = position;
            media = mMedias.get(position);
//            Log.d(TAG, "path: " + media.getPath());

            name.setText(media.getName());

            if (media.isDirectory()) {
                Glide.with(mContext).load(R.drawable.folder).fitCenter().centerCrop().into(
                        thumbnail);
            } else {
                Glide.with(mContext).load(
                        media.getUri()).fitCenter().centerCrop().into(thumbnail);

            }
        }
    }
}
