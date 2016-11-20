package org.xdty.gallery.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

public class DraggableRelativeLayout extends RelativeLayout {

    private static final String TAG = DraggableRelativeLayout.class.getSimpleName();

    private ViewDragHelper mViewDragHelper;

    private DragListener mDragListener;

    private boolean isDragEnabled = true;

    private int x, y;

    private ViewDragHelper.Callback mDragCallback = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return isDragEnabled;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            Log.e(TAG, changedView.toString());
        }

        @Override
        public void onViewDragStateChanged(int state) {
            super.onViewDragStateChanged(state);
            if (mDragListener != null && state == 0) {
                mDragListener.onViewDragFinished();
            }
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            float alpha = (child.getHeight() - Math.abs(top)) * 1f / child.getHeight();

            setAlpha(alpha);

            if (mDragListener != null) {
                mDragListener.onDraggedVertical(top, child.getHeight());
            }
            return top;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return left;
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return child.getHeight();
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);

            mViewDragHelper.settleCapturedViewAt(x, y);
            invalidate();

            if (mDragListener != null) {
                mDragListener.onViewReleased(xvel, yvel);
            }
        }
    };

    public DraggableRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DraggableRelativeLayout(Context context) {
        super(context);
    }

    public DraggableRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DraggableRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr,
            int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        x = getChildAt(0).getLeft();
        y = getChildAt(0).getTop();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mViewDragHelper = ViewDragHelper.create(this, 1.0f, mDragCallback);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mViewDragHelper.continueSettling(true)) {
            int top = getChildAt(0).getTop();
            int height = getChildAt(0).getHeight();
            float alpha = (height - Math.abs(top)) * 1f / height;

            setAlpha(alpha);

            if (mDragListener != null) {
                mDragListener.onDraggedVertical(top, getChildAt(0).getHeight());
            }
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public void setDraggable(boolean enabled) {
        isDragEnabled = enabled;
    }

    public void setDragListener(DragListener listener) {
        mDragListener = listener;
    }

    public interface DragListener {
        void onDraggedVertical(int top, int height);

        void onViewReleased(float xvel, float yvel);

        void onViewDragFinished();
    }
}
