package org.xdty.gallery.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.xdty.gallery.R;
import org.xdty.gallery.model.Media;
import org.xdty.gallery.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
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

        Target target;

        Handler mHandler = new Handler();

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

            // FIXME: use thread pool
            target = new Target() {
                @Override
                public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                File file = new File(media.getCachePath());

                                if (file.createNewFile()) {
                                    FileOutputStream os = new FileOutputStream(file);
                                    Bitmap bm = Utils.getResizedBitmap(bitmap,
                                            thumbnail.getMeasuredWidth() * 2,
                                            thumbnail.getMeasuredHeight() * 2);
                                    bm.compress(Bitmap.CompressFormat.JPEG, 80, os);
                                    os.close();
                                    bm.recycle();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Picasso.with(mContext).load(media.getCacheUri()).tag(mContext)
                                            .fit().centerCrop().into(thumbnail);
                                }
                            });
                        }
                    }).start();
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };
        }

        @Override
        public void bindViews(int position) {
            this.position = position;
            media = mMedias.get(position);
//            Log.d(TAG, "path: " + media.getPath());

            name.setText(media.getName());

            if (media.isDirectory()) {
                Picasso.with(mContext).load(R.drawable.folder).fit().centerCrop().into(thumbnail);
            } else {
                if (media.hasCache()) {
                    Picasso.with(mContext).load(media.getCacheUri()).tag(mContext)
                            .fit().centerCrop().into(thumbnail);
                } else {
                    Picasso.with(mContext).load((String)null).fit().centerCrop().into(
                            thumbnail);
                    Picasso.with(mContext).load(media.getUri()).tag(mContext).into(target);
                }

            }
        }
    }
}
