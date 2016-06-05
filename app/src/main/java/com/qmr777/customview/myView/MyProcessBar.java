package com.qmr777.customview.myView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by qmr777 on 16-5-13.
 * 自定义的进度条。。。
 */
public class MyProcessBar extends View {

    Paint mPaint;
    int rate = 0;
    int w;
    //float px;//之前画到的x坐标
    int process;//进度
    Rect rect = new Rect();

    void initPaint(){
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(5);
        mPaint.setTextSize(30);
        mPaint.setColor(getResources().getColor(android.R.color.holo_blue_dark));

    }


    public MyProcessBar(Context context) {
        super(context);
    }

    public MyProcessBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        //this(context, attrs,0);
    }

    public MyProcessBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint.setColor(getResources().getColor(android.R.color.holo_blue_dark));

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        initPaint();
        //setMeasuredDimension();
        w = getMeasureSpec(widthMeasureSpec);
        int h = getMeasureSpec(heightMeasureSpec);
        Log.d("MyProcessBar","宽度 "+w+" 高度 "+h);
        setMeasuredDimension(w,getMeasureSpec(heightMeasureSpec));
    }

    int getMeasureSpec(int measureSpec){
        if(MeasureSpec.getMode(measureSpec) == MeasureSpec.EXACTLY)
            return MeasureSpec.getSize(measureSpec);
        else {
            return 100;
        }

    }

    //先画左边的，再画数字，最后画右边的
    @Override
    protected void onDraw(Canvas canvas) {
        //Log.d("MyProcessBar","设置进度 "+process + " 开始重绘");
        drawLeft(canvas);
        drawRight(canvas);

    }

    void drawLeft(Canvas canvas){
        float p = process*w/100;
        if(p > 20){
            mPaint.setAlpha(100);
            mPaint.setColor(getResources().getColor(android.R.color.holo_blue_dark));
            canvas.drawLine(0,50,p-25,50,mPaint);
            //mPaint.setAlpha(100);
            canvas.drawText(process+"%",p-20f,60,mPaint);
        }
    }

    void drawRight(Canvas canvas){
        mPaint.setColor(getResources().getColor(android.R.color.holo_blue_bright));
        mPaint.setAlpha(50);
        float p = process*w/100;
        if(p <= 20){
            canvas.drawLine(0,50,w,50,mPaint);
        }
        else {
            canvas.drawLine(p+40f,50,w,50,mPaint);
        }
    }

    public void setProcess(int process){
        this.process = process;
        if(this.process>100)
            this.process = 100;
        else if(this.process<0)
            this.process = 0;
        //process = process*0.01f;
        //Log.d("MyProcessBar","设置进度 "+process);
        invalidate();
    }
}
