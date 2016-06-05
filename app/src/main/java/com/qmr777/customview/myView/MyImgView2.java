package com.qmr777.customview.myView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.qmr777.customview.R;

/**
 * Created by qmr777 on 16-5-11.
 * 双击/双指放大，单指控制平移
 */
public class MyImgView2 extends ImageView {
    public long clickTime;
    //Drawable drawable;
    Matrix matrix = new Matrix();
    Paint mPaint;
    Bitmap bitmap;
    float minRate;//允许缩放的最小比例
    float rate;//放大倍数
    double dist = 0;//手指间距离1
    PointF mid;//手指中点
    private int MODE = 0;
    private boolean allowScale = false;//允许双击缩放

    private static final int NONE = 0;//无
    private static final int DRAG = 1;
    private static final int MULTI_SCALE = 2;//多点触控 缩放


    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        this.bitmap = bm;
    }

    public MyImgView2(Context context) {
        super(context);

    }

    public MyImgView2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyImgView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyImgView2(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(mPaint == null) {
            //super.onDraw(canvas);
            initPaint();
            float r = (float) getWidth()/(float)bitmap.getWidth();//view宽度/图片宽度
            float r2 = (float) getHeight()/(float)bitmap.getHeight();
            r = r<r2?r:r2;
            Log.d("MyImgView2",getWidth()+"  "+getHeight()+"   "+bitmap.getWidth()+"   "+bitmap.getHeight());
            minRate = r>1.0001f?1.0001f:r;
            Log.d("MyImgView2",r+"  "+r2+"初始化图片，宽高压缩率 "+minRate);
            matrix.setScale(minRate,minRate);
            if(r<r2)
                matrix.postTranslate(0,(getHeight()-bitmap.getHeight()*minRate)*0.5f);
            else
                matrix.postTranslate((getWidth()-bitmap.getWidth()*minRate)*0.5f,0);

            canvas.drawBitmap(bitmap,matrix,mPaint);//!

            rate = 1;
        }
        else
            canvas.drawBitmap(bitmap,matrix,mPaint);
        //canvas.drawPicture;
    }

    void initPaint() {
        Log.d("MyImgView2", "initPaint");
        mPaint = new Paint();
        matrix = new Matrix();
        if(bitmap == null){
            bitmap = ((BitmapDrawable)getDrawable()).getBitmap();
        }
    }

    private float calcRate() {
        rate = (rate) % 4;
        return ++rate;
    }
    void scaleImg(MotionEvent event) {
        matrix.set(getImageMatrix());
        matrix.postScale(calcRate(), rate , event.getX(), event.getY());
        Log.d("MyImgView2", "图片缩放，双击 rate "+rate);
        setImageMatrix(matrix);
    }
    void scaleImg(float rate){
        matrix.set(getImageMatrix());
        matrix.postScale(rate,rate,mid.x,mid.y);
        Log.d("MyImgView2", "图片缩放，手指捏合 rate "+rate);
        setImageMatrix(matrix);
    }


    //float tx,ty;
    void transImg(float x,float y){
        float tx=0,ty=0;
        tx+=x;
        ty+=y;
        if(Math.abs(x)>0.5||Math.abs(y)>0.5)
            allowScale = false;
        Log.d("MyImgView2","图片平移trasnImg "+tx+"  "+ty);
        matrix.postTranslate(tx,ty);
        setImageMatrix(matrix);
    }

    double getDist(MotionEvent event){
        double f = dist*dist;
        try {
            float x1 = event.getX(0);
            float x2 = event.getX(1);
            float y1 = event.getY(0);
            float y2 = event.getY(1);
            f = (x1-x2)*(x1-x2)+(y1-y2)*(y1-y2);
        } catch (Exception e){
            Log.d("MyImgView2","java.lang.IllegalArgumentException: pointerIndex out of range , return 1");
        }
        return Math.sqrt(f);

    }

    public PointF getMid(MotionEvent event) {
        float x = Math.abs(event.getX(0) + event.getX(1));
        float y = Math.abs(event.getY(0) + event.getY(1));
        return new PointF(x/2,y/2);
    }


    float x1 = 0,x2,y1 = 0,y2;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        long t = event.getDownTime();

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                MODE = DRAG;
                x1 = event.getX();
                y1 = event.getY();
                //双击放大
                if (event.getPointerCount() == 1) {
                    if (t-clickTime<750 && t-clickTime>150 && allowScale ) {
                        allowScale = false;
                        scaleImg(event);
                        //Log.d("MyImgView2","Scale");
                    }
                    else
                        allowScale = true;
                }
                Log.d("MyImgView2","双击间隔时间（毫秒） "+(t-clickTime));
                clickTime = t;
                break;

            //多点
            case MotionEvent.ACTION_POINTER_DOWN:
                mid = getMid(event);
                dist = getDist(event);
                MODE = MULTI_SCALE;
                Log.d("MyImgView2","Multi");
                break;

            case MotionEvent.ACTION_MOVE:
                Log.d("MyImgView2","ACTION_MOVE");

                if(MODE == MULTI_SCALE){
                    rate = rate*(float)(getDist(event)/dist);
                    if(rate>5)
                        rate = 5;
                    rate = rate<0.5f?0.5f:rate;
                    scaleImg(rate);
                    dist = getDist(event);
                    allowScale = false;
                }
                else if(MODE == DRAG){
                    x2 = event.getX();
                    y2 = event.getY();
                    transImg(x2-x1,y2-y1);
                    x1 = x2;
                    y1 = y2;
                    //allowScale = false;
                }
                break;

            case MotionEvent.ACTION_POINTER_UP:
                MODE = NONE;
                Log.d("MyImgView2","ACTION_POINTER_UP");
                break;
            case MotionEvent.ACTION_UP:
                MODE = NONE;
                Log.d("MyImgView2","ACTION_UP");
                break;
        }
        return true;
    }

}
