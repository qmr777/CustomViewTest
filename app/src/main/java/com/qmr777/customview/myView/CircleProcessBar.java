package com.qmr777.customview.myView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.qmr777.customview.R;

/**
 * Created by qmr777 on 16-5-13.
 * 圆形进度条，可以设定大小
 */
public class CircleProcessBar extends View {

    float edge_length;//指定园直径
    int color;//外围圆圈颜色
    int text_size;//字体
    Paint mPaint;
    float psx,psy;//画笔落笔x,y
    int w,h;//控件宽高
    Rect mBounds = new Rect();
    int process;

    public void setProcess(int process){
        if(process>100)
            process = 100;
        if(process<0)
            process = 0;
        this.process = process;
        invalidate();
    }

    void initPaint(){
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //mPaint.setStrokeWidth(5);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    public CircleProcessBar(Context context) {
        super(context);
    }

    public CircleProcessBar(Context context, AttributeSet attrs) {
        //super(context, attrs);
        this(context,attrs,0);
    }

    public CircleProcessBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
        TypedArray a = context.getTheme().obtainStyledAttributes
                (attrs, R.styleable.CircleProcessBar,defStyleAttr,0);
        int c = a.getIndexCount();
        for(int i = 0;i<c;i++){
            int attr = a.getIndex(i);
            switch (attr){
                case R.styleable.CircleProcessBar_circle_color:
                    color = a.getColor(attr,getResources().getColor(android.R.color.holo_green_dark));
                    break;
            }
        }
        mPaint.setColor(color);
        a.recycle();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        w = getMeasureSpec(widthMeasureSpec);
        h = getMeasureSpec(heightMeasureSpec);
        edge_length = w<h?w:h;
        Log.d("CircleProcessBar","w "+w+" h "+h);
        mPaint.setTextSize(edge_length/6);
        psx = (w-edge_length)/2;
        psy = (h-edge_length)/2;
        Log.d("CircleProcessBar","psx "+psx+" psy "+psy + " edge_length "+edge_length);
        setMeasuredDimension(w+getPaddingRight()+getPaddingLeft(),h+getPaddingTop()+getPaddingEnd());
    }

    int getMeasureSpec(int measureSpec){
        if(MeasureSpec.getMode(measureSpec) == MeasureSpec.EXACTLY)
            return MeasureSpec.getSize(measureSpec);
        else {
            return 300;
        }
    }

    //boolean isRotate = false;
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        String text = process+"%";
        mPaint.getTextBounds(text,0,text.length(),mBounds);
        int ww = mBounds.width();
        int hh = mBounds.height();
        //Log.d("CircleProcessBar","开始绘制"); 外围
        mPaint.setStrokeWidth(1);
        canvas.drawArc(psx,psy,psx+edge_length,psy+edge_length,0,360,false,mPaint);
        mPaint.setStrokeWidth(5);
        canvas.drawArc(psx,psy,psx+edge_length,psy+edge_length,0,3.6f*process,false,mPaint);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawText(text,0,text.length(),(w-ww)*0.5f,(h+hh)*0.5f,mPaint);
        //canvas.drawText(text,0,text.length(),(w)*0.5f,(h)*0.5f,mPaint);
        mPaint.setStyle(Paint.Style.STROKE);
    }

}
