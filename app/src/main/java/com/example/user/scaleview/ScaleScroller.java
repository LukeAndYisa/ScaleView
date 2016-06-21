package com.example.user.scaleview;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Scroller;

/**
 * 监听滑动
 * Created by user on 2016/6/1.
 */
public class ScaleScroller {

    private Context context;
    private GestureDetector gestureDetector; //滑动手势
    private Scroller scroller; //滑动辅助类
    private ScrollingListener listener;
    private int lastX;
    private final int ON_FLING = 1;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            boolean isFinished = scroller.computeScrollOffset();
            int curX = scroller.getCurrX();
            System.out.println("velocityX = " + curX);
            int delta = lastX - curX;
            if(listener != null){
                listener.onScroll(delta);
            }
            lastX = curX;
            if(isFinished)
                handler.sendEmptyMessage(ON_FLING);
            else
                listener.onFinished();
        }
    };

    private GestureDetector.SimpleOnGestureListener simpleOnGestureListener = new GestureDetector.SimpleOnGestureListener(){
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            final int minX = -0x7fffffff;
            final int maxX = 0x7fffffff;
            lastX = 0;
            scroller.fling(0, 0, (int)-velocityX, 0, minX, maxX, 0, 0);
            handler.sendEmptyMessage(ON_FLING);
            return true;
        }
    };

    public ScaleScroller(Context context, ScrollingListener listener){
        this.context = context;
        this.listener = listener;
        init();
    }

    private void init() {
        gestureDetector = new GestureDetector(context, simpleOnGestureListener);
        gestureDetector.setIsLongpressEnabled(false);
        scroller = new Scroller(context);
    }

    //由外部传入event事件
    public boolean onTouchEvent(MotionEvent event){
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            scroller.forceFinished(true);
            lastX = (int)event.getX();
        } else if (event.getAction() == MotionEvent.ACTION_MOVE){
            int distanceX = (int) (event.getX() - lastX);
            if (distanceX != 0) {
                listener.onScroll(distanceX);
                lastX = (int)event.getX();
            }
        } else if(event.getAction() == MotionEvent.ACTION_UP){
            listener.onFinished();
        }
        gestureDetector.onTouchEvent(event);
        return true;
    }

    //滑动回调
    public interface ScrollingListener {

        void onScroll(int distance);

        void onStarted();

        void onFinished();

        void onJustify();
    }

}
