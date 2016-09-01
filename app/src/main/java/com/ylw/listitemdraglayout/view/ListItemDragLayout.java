package com.ylw.listitemdraglayout.view;

import android.content.Context;
import android.graphics.PointF;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import static java.lang.Math.abs;

/**
 * 滑动删除 Created by 袁立位 on 2016/8/23 9:52.
 */
public class ListItemDragLayout extends FrameLayout {

    private static final String TAG = "ListItemDragLayout";
    View dragView;                        // 当前拖动的View
    View vLeft;                           // 左边的view
    View vRight;                          // 右边的view
    FrameLayout content;                  // 子view容器

    private static final float speed = 10; //滚动速度

    private ViewDragHelper mDragger;
    private int vLw;                     // 左边view的宽度
    private int vRw;                     // 右边View的宽度

    public ListItemDragLayout(Context context) {
        this(context, null);

    }

    public ListItemDragLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ListItemDragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mDragger = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                dragView = child;
                // 返回true 允许拖动
                if (child == vLeft) {
                    return true;
                }
                if (child == vRight) {
                    return true;
                }

                return false;
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                postInvalidate();
                if (child == vLeft) {
                    if (dx > 0) {
                        if (left < 0)
                            return left;
                        else
                            return 0;
                    } else {
                        if (left > -vRw)
                            return left;
                        else
                            return -vRw;
                    }
                } else {
                    if (dx > 0) {
                        if (left < vLw)
                            return left;
                        else
                            return vLw;
                    } else {
                        if (left > vLw - vRw)
                            return left;
                        else
                            return vLw - vRw;
                    }
                }
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                return 0;
            }

            @Override
            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
                super.onViewPositionChanged(changedView, left, top, dx, dy);
                if (changedView == vLeft) {                  //拖动左边的View
                    vRight.offsetLeftAndRight(dx);
                }

                if (changedView == vRight) {                 //拖动右边的View
                    vLeft.offsetLeftAndRight(dx);
                }
            }

            @Override
            public void onViewDragStateChanged(int state) {
                Log.d(TAG, "onViewDragStateChanged - " + state);
            }

            // 惯性滑动
            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {

                if (hasClick && vLeft.getLeft() < 0) {
                    if (releasedChild == vLeft) {
                        mDragger.settleCapturedViewAt(0, 0);
                        postInvalidate();
                    } else if (releasedChild == vRight) {
                        mDragger.settleCapturedViewAt(vLw, 0);
                        postInvalidate();
                    }
                    return;
                }

                if (abs(xvel) > 40) {
                    if (releasedChild == vLeft) {
                        int xPosition = 0;
                        if (xvel < 0) xPosition = -vRw;
                        mDragger.settleCapturedViewAt(xPosition, 0);
                        postInvalidate();
                    } else if (releasedChild == vRight) {
                        int xPosition = vLw;
                        if (xvel < 0) xPosition = vLw - vRw;
                        mDragger.settleCapturedViewAt(xPosition, 0);
                        postInvalidate();
                    }
                } else {
                    int pos = vRw / 2 + vLeft.getLeft();
                    if (releasedChild == vLeft) {
                        int xPosition = 0;
                        if (pos < 0) {
                            xPosition = -vRw;
                        }
                        mDragger.settleCapturedViewAt(xPosition, 0);
                        postInvalidate();
                    } else if (releasedChild == vRight) {
                        int xPosition = vLw;
                        if (pos < 0) xPosition = vLw - vRw;
                        mDragger.settleCapturedViewAt(xPosition, 0);
                        postInvalidate();
                    }
                }
            }
        });
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mDragger.continueSettling(true)) {
            postInvalidate();
        }
    }

    boolean hasClick = false;
    PointF downPos = new PointF();

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean intercept;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            initDragView(vLeft, event);
            initDragView(vRight, event);
            downPos.set(event.getX(), event.getY());
        }

        intercept = mDragger.shouldInterceptTouchEvent(event);
        return intercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 判断是不是一个点击事件
        hasClick = false;
        requestFocusFromTouch();
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (event.getEventTime() - event.getDownTime() < 200
                    && abs(downPos.x - event.getX()) + abs(downPos.y - event.getY()) < 10) {
                hasClick = true;
            }
        }

        if (abs(vLeft.getLeft()) > 5) {
            requestDisallowInterceptTouchEvent(true);
        }
        if (dragView == vLeft && mDragger.getActivePointerId() == ViewDragHelper.INVALID_POINTER) {
            dragView = vLeft;
            mDragger.captureChildView(dragView, 0);
        }
        try {
            mDragger.processTouchEvent(event);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return true;
    }


    /**
     * 初始化当前拖动的view
     */
    private void initDragView(View v, MotionEvent e) {
        if (mDragger.isViewUnder(v, (int) e.getX(), (int) e.getY())) {
            dragView = v;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getChildCount() > 2) {
            throw new IllegalStateException("ListItemDragLayout can't hold more than two childs!");
        }
        vLeft = getChildAt(0);
        vRight = getChildAt(1);
        vLw = vLeft.getMeasuredWidth();
        vRw = vRight.getMeasuredWidth();
        FrameLayout.LayoutParams lRp = (LayoutParams) vRight.getLayoutParams();
        lRp.setMargins(0, 0, -vRw, 0);
        requestLayout();
        setOnFocusChangeListener(onFocusChangeListener);
        setFocusableInTouchMode(true);
    }


    OnFocusChangeListener onFocusChangeListener = new OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            Log.d(TAG, "onFocusChange: " + hasFocus + "    " + v);
            if (!hasFocus && vLeft.getLeft() < -vRw / 2) {
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dragView = vLeft;
                        mDragger.smoothSlideViewTo(vLeft, 0, 0);
                        postInvalidate();
                    }
                }, 10);
            }
        }
    };

    public void resetDragView() {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                dragView = vLeft;
                mDragger.smoothSlideViewTo(vLeft, 0, 0);
                postInvalidate();
            }
        }, 10);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }


}
