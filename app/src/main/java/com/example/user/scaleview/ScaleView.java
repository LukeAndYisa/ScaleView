package com.example.user.scaleview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.TextureView;

/**
 * Created by user on 2016/6/1.
 */
public class ScaleView extends TextureView implements TextureView.SurfaceTextureListener, ScaleScroller.ScrollingListener{

    ScaleScroller mScroller;

    private Rect mTextRect = new Rect();
    private RectF mBorderRectF = new RectF();

    private Paint mBorderFillPaint = new Paint();
    private Paint mBorderPaint = new Paint();
    private Paint mCurrentMarkPaint = new Paint();
    private Paint mScaleMarkPaint = new Paint();

    private int mTextHeight = 20; //数字字体大小

    private int mCenterNum; //中心点数字
    private int offset = 0;
    private int dis;  //刻度间距   由mWidth 和 allBlockNum计算得到
    private int allBlockNum = 30;  //刻度分割块的数量 默认30
    private float mWidth;

    private int maxNum = 100; //最大数字
    private int minNum = -100; //最小数字
    private int scaleNum = 1; //每一个刻度间相差数

    private NumberListener numberListener;

    public ScaleView(Context context) {
        super(context);
        init();
    }

    public ScaleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScaleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mScroller = new ScaleScroller(getContext(), this);
        setSurfaceTextureListener(this);
        initPaints();
    }

    private void initPaints() {
        mBorderPaint.setColor(0xffffdfbe);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(2);

        mBorderFillPaint.setColor(0xfffffced);
        mBorderFillPaint.setStyle(Paint.Style.FILL);
        mBorderFillPaint.setStrokeWidth(2);

        mScaleMarkPaint.setColor(0xff979797);
        mScaleMarkPaint.setStyle(Paint.Style.FILL);
        mScaleMarkPaint.setStrokeWidth(3);
        mScaleMarkPaint.setTextSize(mTextHeight);

        mCurrentMarkPaint.setColor(Color.RED);
        mCurrentMarkPaint.setStyle(Paint.Style.FILL);
        mCurrentMarkPaint.setStrokeWidth(3);

    }

    //刷新视图
    private void refreshCanvas() {
        if (mBorderRectF.isEmpty()) {
            return;
        }
        Canvas canvas = lockCanvas();
        if(canvas != null) {
            canvas.drawColor(Color.WHITE);
            drawBorder(canvas);
            drawScaleMark(canvas);
            drawMarkPoint(canvas);
        }
        unlockCanvasAndPost(canvas);
    }

    //画出所有刻度:从中间向两边画
    private void drawScaleMark(Canvas canvas) {
        int count = 0;
        final int centerX = (int)mBorderRectF.centerX();
        if(mCenterNum > maxNum)
            mCenterNum = maxNum;
        if (mCenterNum < minNum)
            mCenterNum = minNum;
        if(numberListener != null)
            numberListener.onChanged(mCenterNum);

        while(true){
            int left = centerX - dis * count;
            int leftNum = mCenterNum - count * scaleNum;
            int right = centerX + dis * count;
            int rightNum = mCenterNum + count * scaleNum;

            String leftText = String.valueOf(leftNum);
            String rightText = String.valueOf(rightNum);
            //间隔5刻度画文字信息
            if(leftNum % (5*scaleNum) == 0) {
                canvas.drawLine(left, canvas.getHeight() / 2, left, canvas.getHeight() - 1, mScaleMarkPaint);
                mScaleMarkPaint.getTextBounds(leftText, 0, leftText.length(), mTextRect);
                canvas.drawText(leftText, left - mTextRect.centerX(), canvas.getHeight() / 2, mScaleMarkPaint);
            }
            else
                canvas.drawLine(left, canvas.getHeight() * 2 / 3, left, canvas.getHeight() - 1, mScaleMarkPaint);

            if(rightNum % (5*scaleNum) == 0) {
                canvas.drawLine(right, canvas.getHeight() / 2, right, canvas.getHeight() - 1, mScaleMarkPaint);
                mScaleMarkPaint.getTextBounds(rightText, 0, rightText.length(), mTextRect);
                canvas.drawText(rightText, right - mTextRect.centerX(), canvas.getHeight() / 2, mScaleMarkPaint);
            }
            else
                canvas.drawLine(right, canvas.getHeight() * 2 / 3, right, canvas.getHeight() - 1, mScaleMarkPaint);

            count++;
            if(left < 0)
                break;
        }
    }

    private void drawBorder(Canvas canvas) {
        canvas.drawLine(mBorderRectF.left, mBorderRectF.bottom - 1, mBorderRectF.right, mBorderRectF.bottom - 1, mScaleMarkPaint);
    }


    private void drawMarkPoint(Canvas canvas) {
        int centerX = (int)mBorderRectF.centerX();
        canvas.drawLine(centerX, canvas.getHeight() / 4, centerX, canvas.getHeight() - 1, mCurrentMarkPaint);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mBorderRectF.set(mBorderPaint.getStrokeWidth(), mBorderPaint.getStrokeWidth(),
                width - mBorderPaint.getStrokeWidth(), height - mBorderPaint.getStrokeWidth());
        mWidth = mBorderRectF.width();
        dis = (int)(mWidth / allBlockNum);
        refreshCanvas();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }


    @Override
    public void onScroll(int distance) {
        offset += distance;
        if (offset > dis) {
            offset = 0;
            mCenterNum -= scaleNum;
        }
        if (offset < -dis) {
            offset = 0;
            mCenterNum += scaleNum;
        }
        refreshCanvas();
    }

    @Override
    public void onStarted() {

    }

    @Override
    public void onFinished() {
        if(offset != 0) {
            //还原中心点在刻度位置上
            offset = 0;
            refreshCanvas();
        }
    }

    @Override
    public void onJustify() {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mScroller.onTouchEvent(event);
    }

    interface NumberListener{
        public void onChanged(int mCurrentNum);
    }


    public void setNumberListener(NumberListener listener){
        this.numberListener = listener;
    }

    public void setTextSize(int textSize){
        this.mTextHeight = textSize;
    }

    public void setMaxNumber(int maxNum){
        this.maxNum = maxNum;
    }

    public void setMinNumber(int minNum){
        this.minNum = minNum;
    }

    public void setScaleNumber(int scaleNum){
        this.scaleNum = scaleNum;
    }

    public void setAllBlockNum(int allBlockNum){
        this.allBlockNum = allBlockNum;
    }

    public void setCenterNum(int centerNum){
        this.mCenterNum = centerNum;
    }
}
