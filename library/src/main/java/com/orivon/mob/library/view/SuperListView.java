package com.orivon.mob.library.view;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.orivon.mob.library.R;

import java.text.SimpleDateFormat;

/**
 * Created by Eenie on 2016/1/27.
 * 下拉刷新、上拉加载更多的实现
 */
public class SuperListView extends ListView implements AbsListView.OnScrollListener {

    private LinearLayout headerView;
    private LinearLayout footerView;
    private LinearLayout emptyView;
    Context context;

    private int headerViewHeight;

    private float startY = 0;//触摸起始Y坐标
    private float offsetY = 0;//触摸移动的Y坐标

    private final int LIST_STATUE_START = 0;//开始下拉
    private final int LIST_STATUE_PUSHING = 1;//正在下拉
    private final int LIST_STATUE_BOTTOM = 2;//下拉到最大位置
    private final int LIST_STATUE_REFRESHING = 3;//正在刷新

    private int listStatue = LIST_STATUE_START;//记录listView的状态

    //原有listPadding
    private int listPaddingTop = 0;
    private int listPaddingBottom = 0;
    private int listPaddingLeft = 0;
    private int listPaddingRight = 0;

    private TextView textOnRefresh;
    private TextView textRefreshDate;
    private TextView textLoadMore;


    private boolean isTouch = false;//是否在触摸
    private int firstVisibleItem = 0;


    private float newOffsetY = 0;
    private float oldOffsetY = 0;

    private boolean canPull = true;//可以下拉
    private boolean canLoad = true;//可以加载

    private RefreshListener refreshListener;

    public SuperListView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public SuperListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public SuperListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();


    }


    public interface RefreshListener {
        void onRefresh();

        void onLoadMore();

    }

    public void setSuperListViewRefreshListener(RefreshListener listener) {
        this.refreshListener = listener;
    }


    /**
     * 控件初始化
     */
    private void init() {
        setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        setOnScrollListener(this);
        headerView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.view_list_head, null, false);
        textOnRefresh = (TextView) headerView.findViewById(R.id.textPush);
        textRefreshDate = (TextView) headerView.findViewById(R.id.textDate);


        measureView(headerView);
        addHeaderView(headerView);


        footerView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.view_list_footer, null, false);
        textLoadMore = ((TextView) footerView.findViewById(R.id.textLoadMore));
        footerView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                textLoadMore.setText("正在加载...");
                refreshListener.onLoadMore();
            }
        });

        measureView(footerView);
//        addFooterView(footerView);


//        emptyView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.view_list_empty, null, false);
//        measureView(emptyView);
//        setEmptyView(emptyView);

        headerViewHeight = headerView.getMeasuredHeight();
        listPaddingTop = headerView.getPaddingTop();
        listPaddingBottom = headerView.getPaddingBottom();
        listPaddingLeft = headerView.getPaddingLeft();
        listPaddingRight = headerView.getPaddingRight();

        //隐藏headerView
        headerView.setPadding(listPaddingLeft, -headerViewHeight, listPaddingRight, listPaddingBottom);


        Log.i("superListView", "headerViewHeight=" + (headerViewHeight + listPaddingTop));

    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (listStatue != LIST_STATUE_REFRESHING) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startY = ev.getY();
                    isTouch = true;
                    oldOffsetY = startY;
//                    System.out.println("起始的Y偏移量-->" + startY);
                    break;
                case MotionEvent.ACTION_MOVE:

                    newOffsetY = ev.getY();
                    offsetY = newOffsetY - startY;

                    if (isTouch && firstVisibleItem == 0) {

                        if (offsetY < headerViewHeight + listPaddingBottom) {

                            oldOffsetY = newOffsetY;

                            headerView.setPadding(listPaddingLeft, (int) (-headerViewHeight + offsetY), listPaddingRight, listPaddingBottom);
//                      System.out.println("滑动的Y偏移量-->" + offsetY);
                            textOnRefresh.setText("下拉刷新");
                            listStatue = LIST_STATUE_PUSHING;

                        } else {
                            textOnRefresh.setText("释放立即刷新");
                            headerView.setPadding(listPaddingLeft, listPaddingTop, listPaddingRight, listPaddingBottom);
                            listStatue = LIST_STATUE_BOTTOM;
                        }

                    }
                    break;


                case MotionEvent.ACTION_UP:
                    isTouch = false;

                    if (listStatue == LIST_STATUE_BOTTOM) {
                        textOnRefresh.setText("正在刷新");
                        listStatue = LIST_STATUE_REFRESHING;
                        refreshListener.onRefresh();
                        textRefreshDate.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(System.currentTimeMillis()));


                    } else {
                        //隐藏headerView

                        headerView.setPadding(listPaddingLeft, -headerViewHeight, listPaddingRight, listPaddingBottom);
//                        closeHeader((int) (-headerViewHeight + offsetY));


                    }
                    break;
            }
        }
        return super.onTouchEvent(ev);
    }


    public void refreshCompleted() {
        //隐藏headerView
        textOnRefresh.setText("刷新成功");
//        headerView.setPadding(listPaddingLeft, -headerViewHeight, listPaddingRight, listPaddingBottom);
        closeHeader();
        listStatue = LIST_STATUE_START;
    }


    public void loadMoreCompleted() {
        //隐藏headerView
        textLoadMore.setText("加载成功!");

        textOnRefresh.postDelayed(new Runnable() {
            @Override
            public void run() {
                textLoadMore.setText("点击加载更多");
            }
        }, 1000);

    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {


    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.firstVisibleItem = firstVisibleItem;
    }


    private void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
                    MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0,
                    MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    /**
     * 关闭列表头部布局
     */
    private void closeHeader() {
        ObjectAnimator animator = new ObjectAnimator()
                .ofFloat(headerView, "alpha", 1f, 0, 1f)
                .setDuration(800);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            float newValue;
            float oldValue = 1;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                newValue = (Float) animation.getAnimatedValue();
                if (newValue - oldValue < 0) {
                    headerView.setPadding(listPaddingLeft, (int) (listPaddingTop - (listPaddingTop + headerViewHeight) * (1 - newValue)), listPaddingRight, listPaddingBottom);
                    oldValue = newValue;
                }
            }
        });
        animator.setStartDelay(300);
        animator.start();
    }

    private void closeHeader(final int currentPaddingTop) {
        ObjectAnimator animator = new ObjectAnimator()
                .ofFloat(headerView, "alpha", 1f, 0, 1f)
                .setDuration(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            float newValue;
            float oldValue = 1;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                newValue = (Float) animation.getAnimatedValue();
                if (newValue - oldValue < 0) {
                    headerView.setPadding(listPaddingLeft, (int) (currentPaddingTop - (listPaddingTop + headerViewHeight) * (1 - newValue)), listPaddingRight, listPaddingBottom);
                    oldValue = newValue;
                }
            }
        });
        animator.start();
    }


    public void openHeader() {
        ObjectAnimator animator = new ObjectAnimator()
                .ofFloat(headerView, "alpha", 0, 1f)
                .setDuration(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            float newValue;

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                newValue = (Float) animation.getAnimatedValue();
                headerView.setPadding(listPaddingLeft, (int) (-headerViewHeight + (headerViewHeight + listPaddingTop) * newValue), listPaddingRight, listPaddingBottom);
            }
        });
        animator.start();
    }


    /**
     * 开始刷新
     */
    public void startRefresh() {

        openHeader();
        textOnRefresh.setText("正在刷新");
        listStatue = LIST_STATUE_REFRESHING;
        textOnRefresh.postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshListener.onRefresh();
            }
        }, 1000);
    }


}
