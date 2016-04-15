package com.huxq17.swipecardsview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class SwipeCardsView extends LinearLayout {
    private List<View> viewList = new ArrayList<>(); // 存放的是每一层的view，从顶到底
    private List<View> releasedViewList = new ArrayList<>(); // 手指松开后存放的view列表

    /**
     * 这个跟原生的ViewDragHelper差不多，不过解决了偶现的pointIndex out of range异常和修改了Interpolator
     */
    private final ViewDragHelper mDragHelper;
    private int initCenterViewX = 0, initCenterViewY = 0; // 最初时，中间View的x位置,y位置
    private int allWidth = 0; // 面板的宽度
    private int allHeight = 0; // 面板的高度
    private int childWith = 0; // 每一个子View对应的宽度

    private static final int MAX_SLIDE_DISTANCE_LINKAGE = 400; // 水平距离+垂直距离

    private int yOffsetStep = 0; // view叠加垂直偏移量的步长
    private float scaleOffsetStep = 0f; // view叠加缩放的步长
    private int alphaOffsetStep = 0; //view叠加透明度的步长

    private static final int X_VEL_THRESHOLD = 900;
    private static final int X_DISTANCE_THRESHOLD = 300;

    public static final int VANISH_TYPE_LEFT = 0;
    public static final int VANISH_TYPE_RIGHT = 1;

    private Object lock = new Object();

    private CardSwitchListener cardSwitchListener; // 回调接口
    private List<?> dataList; // 存储的数据链表
    private int showingIndex = 0; // 当前正在显示的小项
    private boolean btnLock = false;
    private OnClickListener btnListener;

    private BaseCardAdapter mAdapter;
    private GestureDetectorCompat moveDetector;

    public SwipeCardsView(Context context) {
        this(context, null);
    }

    public SwipeCardsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeCardsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SwipCardsView);
        yOffsetStep = (int) a.getDimension(R.styleable.SwipCardsView_yOffsetStep, yOffsetStep);
        alphaOffsetStep = a.getInt(R.styleable.SwipCardsView_alphaOffsetStep, alphaOffsetStep);
        scaleOffsetStep = a.getFloat(R.styleable.SwipCardsView_scaleOffsetStep, scaleOffsetStep);

        mDragHelper = ViewDragHelper.create(this, 2f, new DragHelperCallback());
        mDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
        a.recycle();

        btnListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                // 点击的是卡片
                if (null != cardSwitchListener && view.getScaleX() > 1 - scaleOffsetStep) {
                    cardSwitchListener.onItemClick(view, showingIndex);
                }
            }
        };
        moveDetector = new GestureDetectorCompat(context, new MoveDetector());
    }

    private int getCardLayoutId(int layoutid) {
        String resourceTypeName = getContext().getResources().getResourceTypeName(layoutid);
        if (!resourceTypeName.contains("layout")) {
            String errorMsg = getContext().getResources().getResourceName(layoutid) + " is a illegal layoutid , please check your layout id first !";
            throw new RuntimeException(errorMsg);
        }
        return layoutid;
    }

    private void bindCardData(int position, View cardview) {
        if (mAdapter != null) {
            mAdapter.onBindData(position, cardview, dataList.get(position));
        }
        cardview.setVisibility(View.VISIBLE);
    }

    public void notifyDatasetChanged(List<?> list) {
        if (list != null) {
            dataList = list;
            if (mDragHelper.getViewDragState() == mDragHelper.STATE_IDLE) {
                orderViewStack();
            }
        }
    }

    public void setAdapter(BaseCardAdapter adapter) {
        if (adapter == null) {
            throw new RuntimeException("adapter==null");
        }
        mAdapter = adapter;
        dataList = mAdapter.getData();
        if (dataList == null) {
            throw new RuntimeException("mAdapter.getData() return null");
        }

        viewList.clear();
        int cardVisibleCount = mAdapter.getVisibleCardCount();
        cardVisibleCount = Math.min(cardVisibleCount, dataList.size());
        for (int i = 0; i < cardVisibleCount; i++) {
            View childView = LayoutInflater.from(getContext()).inflate(getCardLayoutId(mAdapter.getCardLayoutId()), this, false);
            if (childView == null) {
                return;
            }
            bindCardData(i, childView);
            viewList.add(childView);
            childView.setOnClickListener(btnListener);
            addView(childView, 0);
        }
        if (null != cardSwitchListener) {
            cardSwitchListener.onShow(0);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean shouldIntercept = mDragHelper.shouldInterceptTouchEvent(ev);
        boolean moveFlag = moveDetector.onTouchEvent(ev);
        int action = ev.getActionMasked();
        if (action == MotionEvent.ACTION_DOWN) {
            // ACTION_DOWN的时候就对view重新排序
            orderViewStack();
            // 保存初次按下时arrowFlagView的Y坐标
            // action_down时就让mDragHelper开始工作
            processTouchEvent(ev);
        }
        return shouldIntercept && moveFlag;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                LogUtil.i("test onTouchEvent ACTION_DOWN action=" + action);
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                LogUtil.i("test onTouchEvent ACTION_UP action=" + action);
                break;
            case MotionEvent.ACTION_CANCEL:
                LogUtil.i("test onTouchEvent ACTION_CANCEL action=" + action);
                break;
        }
        processTouchEvent(ev);
        return true;
    }

    private void processTouchEvent(MotionEvent ev) {
        mDragHelper.processTouchEvent(ev);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        int maxWidth = MeasureSpec.getSize(widthMeasureSpec);
        int maxHeight = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, 0), resolveSizeAndState(maxHeight, heightMeasureSpec, 0));
        allWidth = getMeasuredWidth();
        allHeight = getMeasuredHeight();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int size = viewList.size();
        if (size == 0) {
            return;
        }
        for (int i = 0; i < size; i++) {
            View child = viewList.get(i);
            layoutChild(child, i);
        }
        // 初始化一些中间参数
        initCenterViewX = viewList.get(0).getLeft();
        initCenterViewY = viewList.get(0).getTop();
        childWith = viewList.get(0).getMeasuredWidth();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void layoutChild(View child, int index) {
        LayoutParams lp = (LayoutParams) child.getLayoutParams();
        int width = child.getMeasuredWidth();
        int height = child.getMeasuredHeight();

        int gravity = lp.gravity;
        if (gravity == -1) {
            gravity = Gravity.TOP | Gravity.START;
        }

        int layoutDirection = getLayoutDirection();
        final int absoluteGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection);
        final int verticalGravity = gravity & Gravity.VERTICAL_GRAVITY_MASK;

        int childLeft;
        int childTop;
        switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
            case Gravity.CENTER_HORIZONTAL:
                childLeft = (getWidth() + getPaddingLeft() - getPaddingRight() - width) / 2 +
                        lp.leftMargin - lp.rightMargin;
                break;
            case Gravity.END:
                childLeft = getWidth() + getPaddingRight() - width - lp.rightMargin;
                break;
            case Gravity.START:
            default:
                childLeft = getPaddingLeft() + lp.leftMargin;
                break;
        }
        switch (verticalGravity) {
            case Gravity.CENTER_VERTICAL:
                childTop = (getHeight() + getPaddingTop() - getPaddingBottom() - height) / 2 +
                        lp.topMargin - lp.bottomMargin;
                break;
            case Gravity.BOTTOM:
                childTop = getHeight() - getPaddingBottom() - height - lp.bottomMargin;
                break;
            case Gravity.TOP:
            default:
                childTop = getPaddingTop() + lp.topMargin;
                break;
        }
        child.layout(childLeft, childTop, childLeft + width, childTop + height);
        int offset = yOffsetStep * index;
        float scale = 1 - scaleOffsetStep * index;
        float alpha = 1.0f * (100 - alphaOffsetStep * index) / 100;
        child.offsetTopAndBottom(offset);
        child.setScaleX(scale);
        child.setScaleY(scale);
        child.setAlpha(alpha);
    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        } else {
            // 动画结束
            synchronized (this) {
                if (mDragHelper.getViewDragState() == ViewDragHelper.STATE_IDLE) {
                    mDragHelper.cancel();
                    orderViewStack();
                    btnLock = false;
                }
            }
        }
    }

    /**
     * 这是viewdraghelper拖拽效果的主要逻辑
     */
    private class DragHelperCallback extends ViewDragHelper.Callback {

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            // 调用offsetLeftAndRight导致viewPosition改变，会调到此处，所以此处对index做保护处理
            int index = viewList.indexOf(changedView);

            if (index + 2 > viewList.size()) {
                return;
            }
            processLinkageView(changedView);
        }

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            // 如果数据List为空，或者子View不可见，则不予处理
            if (dataList == null || dataList.size() == 0 || child.getVisibility() != View.VISIBLE || child.getScaleX() < 1.0f - scaleOffsetStep) {
                // 一般来讲，如果拖动的是第三层、或者第四层的View，则直接禁止
                // 此处用getScale的用法来巧妙回避
                return false;
            }

            if (btnLock) {
                return false;
            }

            // 只捕获顶部view(rotation=0)
            if (viewList.indexOf(child) > 0) {
                return false;
            }
            return true;
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            // 这个用来控制拖拽过程中松手后，自动滑行的速度
            return 256;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            animToSide(releasedChild, xvel, yvel);
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return left;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            return top;
        }
    }

    /**
     * 对View重新排序
     */
    private void orderViewStack() {
        synchronized (lock) {
            if (releasedViewList.size() == 0) {
                if (viewList.size() != 0) {
                    View topView = viewList.get(0);
                    if (topView.getLeft() != initCenterViewX || topView.getTop() != initCenterViewY) {
                        topView.offsetLeftAndRight(initCenterViewX - topView.getLeft());
                        topView.offsetTopAndBottom(initCenterViewY - topView.getTop());
                    }
                }
                return;
            }
            View changedView = releasedViewList.get(0);
            if (changedView.getLeft() == initCenterViewX) {
                releasedViewList.remove(0);
                return;
            }
            int viewSize = viewList.size();
            removeViewInLayout(changedView);
            addViewInLayout(changedView, 0, changedView.getLayoutParams(), true);
            requestLayout();
//            removeView(changedView);
//            addView(changedView,0);

            // 3. changedView填充新数据
            int newIndex = showingIndex + viewSize + 1;
            if (newIndex < dataList.size()) {
                bindCardData(newIndex, changedView);
            } else {
                changedView.setVisibility(View.GONE);
            }

            // 4. viewList中的卡片view的位次调整
            viewList.remove(changedView);
            viewList.add(changedView);
            releasedViewList.remove(0);


            // 5. 更新showIndex、接口回调
            if (showingIndex + 1 < dataList.size()) {
                showingIndex++;
            }
            if (null != cardSwitchListener) {
                cardSwitchListener.onShow(showingIndex);
            }
        }
    }

    /**
     * 顶层卡片View位置改变，底层的位置需要调整
     *
     * @param changedView 顶层的卡片view
     */
    private void processLinkageView(View changedView) {
        int changeViewLeft = changedView.getLeft();
        int changeViewTop = changedView.getTop();
        int distance = Math.abs(changeViewTop - initCenterViewY)
                + Math.abs(changeViewLeft - initCenterViewX);
        float rate = distance / (float) MAX_SLIDE_DISTANCE_LINKAGE;

        for (int i = 1; i < viewList.size(); i++) {
            float rate3 = rate - 0.2f * i;
            if (rate3 > 1) {
                rate3 = 1;
            } else if (rate3 < 0) {
                rate3 = 0;
            }
            ajustLinkageViewItem(changedView, rate3, i);
        }
    }

    // 由index对应view变成index-1对应的view
    private void ajustLinkageViewItem(View changedView, float rate, int index) {
        int changeIndex = viewList.indexOf(changedView);

        int initPosY = yOffsetStep * index;
        float initScale = 1 - scaleOffsetStep * index;
        float initAlpha = 1.0f * (100 - alphaOffsetStep * index) / 100;

        int nextPosY = yOffsetStep * (index - 1);
        float nextScale = 1 - scaleOffsetStep * (index - 1);
        float nextAlpha = 1.0f * (100 - alphaOffsetStep * (index - 1)) / 100;

        int offset = (int) (initPosY + (nextPosY - initPosY) * rate);
        float scale = initScale + (nextScale - initScale) * rate;
        float alpha = initAlpha + (nextAlpha - initAlpha) * rate;

        View ajustView = viewList.get(changeIndex + index);
        ajustView.offsetTopAndBottom(offset - ajustView.getTop() + initCenterViewY);
        ajustView.setScaleX(scale);
        ajustView.setScaleY(scale);
        ajustView.setAlpha(alpha);
    }

    /**
     * 松手时处理滑动到边缘的动画
     *
     * @param xvel X方向上的滑动速度
     */
    private void animToSide(View changedView, float xvel, float yvel) {
        int finalX = initCenterViewX;
        int finalY = initCenterViewY;
        int flyType = -1;

        int dx = changedView.getLeft() - initCenterViewX;
        int dy = changedView.getTop() - initCenterViewY;
        if (dx == 0) {
            // 由于dx作为分母，此处保护处理
            dx = 1;
        }
        if (dx > X_DISTANCE_THRESHOLD || (xvel > X_VEL_THRESHOLD && dx > 0)) {//向右边滑出
            finalX = allWidth;
            finalY = dy * (childWith + initCenterViewX) / dx + initCenterViewY;
            flyType = VANISH_TYPE_RIGHT;
        } else if (dx < -X_DISTANCE_THRESHOLD || (xvel < -X_VEL_THRESHOLD && dx < 0)) {//向左边滑出
            finalX = -childWith;
            finalY = dy * (childWith + initCenterViewX) / (-dx) + dy
                    + initCenterViewY;
            flyType = VANISH_TYPE_LEFT;
        }

        if (finalY > allHeight) {
            finalY = allHeight;
        } else if (finalY < -allHeight / 2) {
            finalY = -allHeight / 2;
        }

        if (finalX != initCenterViewX) {
            releasedViewList.add(changedView);
        }

        if (mDragHelper.smoothSlideViewTo(changedView, finalX, finalY)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }

        if (flyType >= 0 && cardSwitchListener != null) {
            cardSwitchListener.onCardVanish(showingIndex, flyType);
        }
    }

    /**
     * 点击按钮消失动画
     */
    private void vanishOnBtnClick(int type) {
        synchronized (lock) {
            View animateView = viewList.get(0);
            if (animateView.getVisibility() != View.VISIBLE || releasedViewList.contains(animateView)) {
                return;
            }

            int finalX = 0;
            if (type == VANISH_TYPE_LEFT) {
                finalX = -childWith;
            } else if (type == VANISH_TYPE_RIGHT) {
                finalX = allWidth;
            }

            if (finalX != 0) {
                releasedViewList.add(animateView);
                if (mDragHelper.smoothSlideViewTo(animateView, finalX, initCenterViewY + allHeight)) {
                    ViewCompat.postInvalidateOnAnimation(this);
                }
            }

            if (type >= 0 && cardSwitchListener != null) {
                cardSwitchListener.onCardVanish(showingIndex, type);
            }
        }
    }

    /**
     * 这是View的方法，该方法不支持android低版本（2.2、2.3）的操作系统，所以手动复制过来以免强制退出
     */
    public static int resolveSizeAndState(int size, int measureSpec,
                                          int childMeasuredState) {
        int result = size;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                result = size;
                break;
            case MeasureSpec.AT_MOST:
                if (specSize < size) {
                    result = specSize | MEASURED_STATE_TOO_SMALL;
                } else {
                    result = size;
                }
                break;
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
        }
        return result | (childMeasuredState & MEASURED_STATE_MASK);
    }

    /**
     * 设置卡片操作回调
     *
     * @param cardSwitchListener 回调接口
     */
    public void setCardSwitchListener(CardSwitchListener cardSwitchListener) {
        this.cardSwitchListener = cardSwitchListener;
    }

    /**
     * 卡片回调接口
     */
    public interface CardSwitchListener {
        /**
         * 新卡片显示回调
         *
         * @param index 最顶层显示的卡片的index
         */
        void onShow(int index);

        /**
         * 卡片飞向两侧回调
         *
         * @param index 飞向两侧的卡片数据index
         * @param type  飞向哪一侧{@link #VANISH_TYPE_LEFT}或{@link #VANISH_TYPE_RIGHT}
         */
        void onCardVanish(int index, int type);

        /**
         * 卡片点击事件
         *
         * @param cardImageView 卡片上的图片view
         * @param index         点击到的index
         */
        void onItemClick(View cardImageView, int index);
    }

    class MoveDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float dx,
                                float dy) {
            // 拖动了，touch不往下传递
            return Math.abs(dy) + Math.abs(dx) > 5;
        }
    }
}
