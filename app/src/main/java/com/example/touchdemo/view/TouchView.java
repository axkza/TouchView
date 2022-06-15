package com.example.touchdemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 拖动、旋转、缩放
 * **/
public class TouchView extends androidx.appcompat.widget.AppCompatImageView {
    private ITouchViewListener listener;
    private float MAX_SCALE = 4.0f;
    private float MIN_SCALE = 1f;

    // 是否已经响应ACTION_DOWN事件，避免removeView和addView后又触发ACTION_DOWN事件
    private boolean isOnTouch = false;

    // 初始触摸的点
    private int touchX = 0;
    private int touchY = 0;
    // 初始margin
    private int currentLeftMargin = 0;
    private int currentTopMargin = 0;
    // 初始margin后的位移
    private int offsetX = 0;
    private int offsetY = 0;
    // 触摸时的角度
    private float touchRotation = 0;
    // 初始两点的距离
    private float originalDistance;
    // 多个手指时，是否有某个手指抬起
    private boolean multiPointerUp = false;

    public TouchView(@NonNull Context context) {
        super(context);
    }

    public TouchView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setTouchViewListener(ITouchViewListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN && isOnTouch) {
            return super.onTouchEvent(event);
        }
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (listener != null) listener.onTouch(true);
                isOnTouch = true;
                initTouchParams(event);
                break;
            case MotionEvent.ACTION_UP:
                isOnTouch = false;
                if (listener != null) listener.onTouch(false);
                multiPointerUp = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (listener != null) listener.onMove((int) event.getRawY());
                if (multiPointerUp) {
                    initTouchParams(event);
                    multiPointerUp = false;
                }
                if (event.getPointerCount() == 1) {
                    // 更新位移
                    offsetX = (int) (event.getRawX() - touchX);
                    offsetY = (int) (event.getRawY() - touchY);
                    updateMargins(currentLeftMargin + offsetX, currentTopMargin + offsetY);
                } else if (event.getPointerCount() >= 2) {
                    // 更新角度
                    setRotation(getRotation() + getRotation(event) - touchRotation);
                    // 更新缩放
                    float currentDistance = getDistance(event);
                    double space = currentDistance - originalDistance;
                    float scale = (float) (getScaleX() + space / getWidth());
                    scale = Math.min(scale, MAX_SCALE);
                    scale = Math.max(scale, MIN_SCALE);
                    this.setScaleX(scale);
                    this.setScaleY(scale);
                }
                break;

            case MotionEvent.ACTION_POINTER_UP:
                multiPointerUp = true;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (event.getPointerCount() == 2) {
                    originalDistance = getDistance(event);
                    touchRotation = getRotation(event);
                }
                break;
        }
        return true;
    }

    // 起始触摸的点和起始margin
    private void initTouchParams(MotionEvent event) {
        touchX = (int) event.getRawX();
        touchY = (int) event.getRawY();
        currentLeftMargin = ((FrameLayout.LayoutParams) getLayoutParams()).leftMargin;
        currentTopMargin = ((FrameLayout.LayoutParams) getLayoutParams()).topMargin;
    }

    // 重置margin
    public void resetMargins() {
        ViewGroup parent = (ViewGroup)getParent();
        if (parent != null) {
            // topMargin的最大值为Parent和Child高度的一半
            float halfParentAndChildY = parent.getHeight() / 2.0f + getHeight() / 2.0f * getScaleY();
            // topMargin的最大值为Parent和Child宽度的一半
            float halfParentAndChildX = parent.getWidth() / 2.0f + getWidth() / 2.0f * getScaleX();
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) getLayoutParams();
            if (halfParentAndChildY < Math.abs(lp.topMargin) || halfParentAndChildX < Math.abs(lp.leftMargin)) {
                updateMargins(0, 0);
            }
        }
    }

    // 更新自身的margin
    public void updateMargins(int left, int top) {
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) getLayoutParams();
        lp.leftMargin = left;
        lp.topMargin = top;
        setLayoutParams(lp);
    }

    // 获取两点角度
    private float getRotation(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }

    // 获取两点距离
    private float getDistance(MotionEvent event) {
        float x = Math.max(event.getX(0), event.getX(1)) - Math.min(event.getX(0), event.getX(1));
        float y = Math.max(event.getY(0), event.getY(1)) - Math.min(event.getY(0), event.getY(1));
        return (float) Math.sqrt(x * x + y * y);
    }
}
