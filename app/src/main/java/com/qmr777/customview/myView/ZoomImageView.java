package com.qmr777.customview.myView;


import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

public class ZoomImageView extends ImageView implements OnScaleGestureListener,
        OnTouchListener, ViewTreeObserver.OnGlobalLayoutListener

{
    //�?大缩放比�?
    public static final float SCALE_MAX = 4.0f;
    public static final float SCALE_MID = 2.0f;
    private static final String TAG = ZoomImageView.class.getSimpleName();
    /**
     * 用于存放矩阵�?9个�??
     */
    private final float[] matrixValues = new float[9];
    private final Matrix mScaleMatrix = new Matrix();
    int mTouchSlop = 1;
    int lastPointerCount;
    boolean isCanDrag;
    float mLastY;
    float mLastX;
    boolean isCheckLeftAndRight, isCheckTopAndBottom, isAutoScale;
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {

            setImageMatrix(mScaleMatrix);

        }

        ;
    };

    ImageDoListenner ido;
    /**
     * 0000000000000000000000000000000000000000000
     * 初始化时的缩放比例，如果图片宽或高大于屏幕，此�?�将小于0
     */
    private float initScale = 1.0f;
    private boolean once = true;
    /**
     * 缩放的手势检�?
     */
    private ScaleGestureDetector mScaleGestureDetector = null;
    private int caozuonum = 1;

    public ZoomImageView(Context context) {
        this(context, null);
    }

    public ZoomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.setScaleType(ScaleType.MATRIX);
        mScaleGestureDetector = new ScaleGestureDetector(context, this);
        this.setOnTouchListener(this);

    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float scale = getScale();
        float scaleFactor = detector.getScaleFactor();

        if (getDrawable() == null)
            return true;

        /**
         * 缩放的范围控�?
         */
        if ((scale < SCALE_MAX && scaleFactor > 1.0f)
                || (scale > initScale && scaleFactor < 1.0f)) {
            /**
             * �?大�?�最小�?�判�?
             */
            if (scaleFactor * scale < initScale) {
                scaleFactor = initScale / scale;
            }
            if (scaleFactor * scale > SCALE_MAX) {
                scaleFactor = SCALE_MAX / scale;
            }
//                        /**
//                         * 设置缩放比例
//                         */
//                        mScaleMatrix.postScale(scaleFactor, scaleFactor, getWidth() / 2,
//                                        getHeight() / 2);
//                        setImageMatrix(mScaleMatrix);


            /**
             * 设置缩放比例
             */
            mScaleMatrix.postScale(scaleFactor, scaleFactor,
                    detector.getFocusX(), detector.getFocusY());
            checkBorderAndCenterWhenScale();
            setImageMatrix(mScaleMatrix);


        }
        return true;

    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {


        mScaleGestureDetector.onTouchEvent(event);

        float x = 0, y = 0;
        // 拿到触摸点的个数
        final int pointerCount = event.getPointerCount();
        // 得到多个触摸点的x与y均�??
        for (int i = 0; i < pointerCount; i++) {
            x += event.getX(i);
            y += event.getY(i);
        }
        x = x / pointerCount;
        y = y / pointerCount;

        /**
         * 每当触摸点发生变化时，重置mLasX , mLastY
         */
        if (pointerCount != lastPointerCount) {
            isCanDrag = false;
            mLastX = x;
            mLastY = y;
        }


        lastPointerCount = pointerCount;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                caozuonum = 1;

                break;

            case MotionEvent.ACTION_MOVE:


                Log.e(TAG, "ACTION_MOVE");
                float dx = x - mLastX;
                float dy = y - mLastY;


                if (dx == 0 || dy == 0) {

                    break;
                }
                caozuonum = 2;
                if (!isCanDrag) {
                    isCanDrag = isCanDrag(dx, dy);
                }
                if (isCanDrag) {

                    RectF rectF = getMatrixRectF();
                    if (getDrawable() != null) {
                        isCheckLeftAndRight = isCheckTopAndBottom = true;
                        // 如果宽度小于屏幕宽度，则禁止左右移动
                        if (rectF.width() < getWidth()) {
                            dx = 0;
                            isCheckLeftAndRight = false;
                        }
                        // 如果高度小雨屏幕高度，则禁止上下移动
                        if (rectF.height() < getHeight()) {
                            dy = 0;
                            isCheckTopAndBottom = false;
                        }
                        mScaleMatrix.postTranslate(dx, dy);

                        setImageMatrix(mScaleMatrix);
                        checkMatrixBoundsOne();

                    }
                }
                mLastX = x;
                mLastY = y;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                caozuonum = 2;
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:

                lastPointerCount = 0;
                checkMatrixBounds();


                if (caozuonum == 1 && ido != null) {
                    ido.onClick_NoMove(this);
                }


        }

        return true;
    }

    private void checkMatrixBoundsOne() {


        RectF rect = getMatrixRectF();


        final float viewWidth = getWidth();


        if (rect.left > 50 && isCheckLeftAndRight) {
            if (ido != null) ido.onDrag_Collision(this, ImageDoListenner.DIR_LEFT);


        }
        if (rect.right < viewWidth - 50 && isCheckLeftAndRight) {
            if (ido != null) ido.onDrag_Collision(this, ImageDoListenner.DIR_RIGHT);


        }


    }

    /**
     * 移动时，进行边界判断，主要判断宽或高大于屏幕�?
     */
    private void checkMatrixBounds() {


        RectF rect = getMatrixRectF();

        float deltaX = 0, deltaY = 0;
        final float viewWidth = getWidth();
        final float viewHeight = getHeight();


        // 判断移动或缩放后，图片显示是否超出屏幕边�?
        if (rect.top > 0 && isCheckTopAndBottom) {

            deltaY = -rect.top;
        }
        if (rect.bottom < viewHeight && isCheckTopAndBottom) {

            deltaY = viewHeight - rect.bottom;
        }
        if (rect.left > 0 && isCheckLeftAndRight) {

            deltaX = -rect.left;

        }
        if (rect.right < viewWidth && isCheckLeftAndRight) {
            deltaX = viewWidth - rect.right;

        }
        Log.v("admin", deltaX + "-------" + deltaY);


        final Float xx = deltaX;
        final Float yy = deltaY;


        new Thread() {
            public void run() {
                int i = 0;
                while (i < 100) {
                    try {
                        sleep(5);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    mScaleMatrix.postTranslate(xx / 100, yy / 100);
                    handler.sendMessage(new Message());
                    i++;
                }

            }
        }.start();

    }

    /**
     * 是否是推动行�?
     *
     * @param dx
     * @param dy
     * @return
     */
    private boolean isCanDrag(float dx, float dy) {
        return Math.sqrt((dx * dx) + (dy * dy)) >= mTouchSlop;
    }

    /**
     * 获得当前的缩放比�?
     *
     * @return
     */
    public final float getScale() {
        mScaleMatrix.getValues(matrixValues);
        return matrixValues[Matrix.MSCALE_X];
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeGlobalOnLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        if (once) {
            Drawable d = getDrawable();
            if (d == null)
                return;
            Log.e(TAG, d.getIntrinsicWidth() + " , " + d.getIntrinsicHeight());
            int width = getWidth();
            int height = getHeight();
            // 拿到图片的宽和高
            int dw = d.getIntrinsicWidth();
            int dh = d.getIntrinsicHeight();
            float scale = 1.0f;
            // 如果图片的宽或�?�高大于屏幕，则缩放至屏幕的宽或者高
            if (dw > width && dh <= height) {
                scale = width * 1.0f / dw;
            }
            if (dh > height && dw <= width) {
                scale = height * 1.0f / dh;
            }
            // 如果宽和高都大于屏幕，则让其按按比例适应屏幕大小
            if (dw > width && dh > height) {
                scale = Math.min(dw * 1.0f / width, dh * 1.0f / height);
            }
            initScale = scale;
            // 图片移动至屏幕中�?
            mScaleMatrix.postTranslate((width - dw) / 2, (height - dh) / 2);
            mScaleMatrix
                    .postScale(scale, scale, getWidth() / 2, getHeight() / 2);
            setImageMatrix(mScaleMatrix);
            once = false;
        }

    }

    /**
     * 在缩放时，进行图片显示范围的控制
     */
    private void checkBorderAndCenterWhenScale() {

        RectF rect = getMatrixRectF();
        float deltaX = 0;
        float deltaY = 0;

        int width = getWidth();
        int height = getHeight();

        // 如果宽或高大于屏幕，则控制范�?
        if (rect.width() >= width) {
            if (rect.left > 0) {
                deltaX = -rect.left;
            }
            if (rect.right < width) {
                deltaX = width - rect.right;
            }
        }
        if (rect.height() >= height) {
            if (rect.top > 0) {
                deltaY = -rect.top;
            }
            if (rect.bottom < height) {
                deltaY = height - rect.bottom;
            }
        }
        // 如果宽或高小于屏幕，则让其居�?
        if (rect.width() < width) {
            deltaX = width * 0.5f - rect.right + 0.5f * rect.width();
        }
        if (rect.height() < height) {
            deltaY = height * 0.5f - rect.bottom + 0.5f * rect.height();
        }
        Log.e(TAG, "deltaX = " + deltaX + " , deltaY = " + deltaY);

        mScaleMatrix.postTranslate(deltaX, deltaY);


    }


//        Handler   handler;
//        /**
//         * 点击
//         * @param han
//         */
//        public void setBackClickhandler(Handler  han){
//        	this.handler=han;
//        }

    /**
     * 根据当前图片的Matrix获得图片的范�?
     *
     * @return
     */
    private RectF getMatrixRectF() {
        Matrix matrix = mScaleMatrix;
        RectF rect = new RectF();
        Drawable d = getDrawable();
        if (null != d) {
            rect.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            matrix.mapRect(rect);
        }
        return rect;
    }

    public void setImageDoListenner(ImageDoListenner ido) {
        this.ido = ido;
    }


    public interface ImageDoListenner {
        int DIR_TOP = 1;
        int DIR_BUTTON = 2;
        int DIR_LEFT = 3;
        int DIR_RIGHT = 4;

        void onClick_NoMove(View v);

        void onDrag_Collision(View v, int Collision_Dir);

    }

    ;


}