package com.qmr777.customview.myView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.qmr777.customview.R;

/**
 * Created by qmr777 on 16-5-10.
 * 验证码生成
 */
public class CustomView2 extends View {

    String text;
    int x, y;
    private Paint mPaint = new Paint();
    private int w_result, h_result;
    private Rect mBound = new Rect();

    public CustomView2(Context context) {
        super(context);
        initPaint();
    }

    //默认调用的构造方法
    public CustomView2(Context context, AttributeSet attrs) {
        //super(context, attrs);
        this(context,attrs,0);
        //initPaint();
    }

    public CustomView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.d("CustomView2","构造3");

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,R.styleable.CustomView2,defStyleAttr,0);
        for(int i = 0;i<a.getIndexCount();i++){
            int attr = a.getIndex(i);
            switch (attr){
                case R.styleable.CustomView2_string:
                    text = a.getString(attr);
                    Log.d("CustomView2","text的内容"+text);
                    break;
            }
        }
        a.recycle();
        initPaint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY)
            w_result = MeasureSpec.getSize(widthMeasureSpec);
        else {
            mPaint.getTextBounds(text, 0, text.length(), mBound);
            w_result = mBound.width() + getPaddingLeft() + getPaddingRight() + 100;
        }

        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY)
            h_result = MeasureSpec.getSize(heightMeasureSpec);
        else {
            mPaint.getTextBounds(text, 0, text.length(), mBound);
            h_result = mBound.height() + getPaddingTop() + getPaddingBottom() + 100;
        }
        Log.d("customView2", "width=" + w_result + " height=" + h_result);
        setMeasuredDimension(w_result, h_result);

    }

    void initPaint() {
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(5);
        mPaint.setColor(getResources().getColor(android.R.color.black));
        mPaint.setTextSize(120);
    }

    //重设验证码数字
    public void setText(int num) {
        this.text = num % 8999 + "";
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mPaint == null) {
            initPaint();
        }
        canvas.drawColor(getResources().getColor(R.color.colorSmoke));
        //写字
        for (int i = 0; i < 4; i++) {
            mPaint.setAlpha((int) getRandom());
            mPaint.setStrokeWidth((int) getPaint());
            canvas.drawText(text.substring(i, i + 1), (i) * w_result / 4, (h_result + mBound.height()) / 2, mPaint);
        }

        //干扰线条
        mPaint.setAlpha((int) getRandom());
        for (int i = 0; i < 3; i++) {
            canvas.drawLine((int) (Math.random() * w_result), (int) (Math.random() * h_result),
                    (int) (Math.random() * w_result), (int) (Math.random() * h_result), mPaint);
            canvas.drawArc((float) (Math.random() * w_result), (float) (Math.random() * h_result),
                    (float) (Math.random() * w_result), (float) (Math.random() * h_result),
                    (float) (Math.random() * 360), (float) (Math.random() * 360), false, mPaint);
        }

    }

    //获取alpha值[50,100]
    double getRandom() {
        return Math.random() * 50 + 50;
    }

    //mPaint笔触宽度[3,9]
    double getPaint() {
        return Math.random() * 6 + 3;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                this.invalidate();
                break;
        }
        return true;
    }
}
