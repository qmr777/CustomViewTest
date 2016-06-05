package com.qmr777.customview.myView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by qmr777 on 16-5-10.
 *  双向选择器/progressBar
 */
public class CustomView1 extends View{
    private Paint mPaint;

    public CustomView1(Context context) {
        super(context);
    }

    public CustomView1(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomView1(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CustomView1(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    void initPaint(){
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(5);
        mPaint.setColor(getResources().getColor(android.R.color.black));
    }

    float x = 50;
    float y = 250;

    int w_result,h_result;



    void setCircleX(float x){
        this.x = x;
        this.invalidate();
    }

    void setCircleY(float y){
        this.y = y;
        this.invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int w_mode = MeasureSpec.getMode(widthMeasureSpec);
        int h_mode = MeasureSpec.getMode(heightMeasureSpec);



        if(w_mode == MeasureSpec.EXACTLY){
            w_result = width;
        } else if(w_mode == MeasureSpec.AT_MOST) {
            w_result = getPaddingLeft() + width + getPaddingRight();
        }


        if(h_mode == MeasureSpec.EXACTLY) {
            h_result = height < 100 ? 100 : height;
        } else {
            h_result = Math.min(height,100);
            h_result = h_result+getPaddingTop()+getPaddingBottom();
        }

        Log.d("CustomView1",w_result+"  "+h_result);
        setMeasuredDimension(w_result,h_result);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initPaint();
        canvas.drawLine(0,50,w_result,50,mPaint);
        mPaint.setColor(getResources().getColor(android.R.color.holo_red_light));
        canvas.drawCircle(x,50,25,mPaint);
        mPaint.setColor(getResources().getColor(android.R.color.holo_blue_light));
        canvas.drawCircle(y,50,25,mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float p = event.getX();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(p<0)
                    p = 0;
                if(p>w_result)
                    p = w_result;

                if(Math.abs(p-x)>Math.abs(p-y))
                    setCircleY(p);
                else
                    setCircleX(p);
                break;
            case MotionEvent.ACTION_MOVE:
                if(p<0)
                    p = 0;
                if(p>w_result)
                    p = w_result;

                if(Math.abs(p-x)>Math.abs(p-y))
                    setCircleY(p);
                else
                    setCircleX(p);
                break;
            case MotionEvent.ACTION_UP:
                if(onProgressChanged!=null)
                    onProgressChanged.setOnProgressChanged(x/w_result,y/w_result);
                break;
        }

        return true;

    }

    public void setOnProgressChanged(setOnProgressChanged setOnProgressChanged){
        this.onProgressChanged = setOnProgressChanged;
    }

    private setOnProgressChanged onProgressChanged;

    public interface setOnProgressChanged{
        void setOnProgressChanged(float left,float right);//返回左右点所在百分比位置
    }
}
