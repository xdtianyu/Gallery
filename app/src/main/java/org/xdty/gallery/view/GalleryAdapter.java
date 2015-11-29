package org.xdty.gallery.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.xdty.gallery.R;
import org.xdty.gallery.SambaRequestHandler;
import org.xdty.gallery.model.Media;

import java.util.List;

/**
 * Created by ty on 15-11-28.
 */
public class GalleryAdapter extends RecyclerView.Adapter<ViewHolder> {

    public final static String TAG = GalleryAdapter.class.getSimpleName();

    private static final int TYPE_MEDIA = 1000;

    private final LayoutInflater mLayoutInflater;
    private Picasso mPicasso;
    private List<Media> mMedias;

    private OnItemClickListener mOnItemClickListener;

    public GalleryAdapter(Context context, List<Media> medias, OnItemClickListener listener) {
        mMedias = medias;
        mOnItemClickListener = listener;

        mLayoutInflater = LayoutInflater.from(context);

        mPicasso = new Picasso.Builder(context)
                .addRequestHandler(new SambaRequestHandler())
                .build();
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
            Log.d(TAG, "path: " + media.getPath());

            name.setText(media.getName());

            if (media.isDirectory()) {
                mPicasso.load(R.drawable.folder)
                        .fit()
                        .centerCrop()
                        .into(thumbnail);
            } else {
                mPicasso.load(media.getUri())
                        .fit()
                        .centerCrop()
                        .into(thumbnail);
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClicked(int position, Media media);
    }
}
