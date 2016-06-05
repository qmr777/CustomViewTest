package com.qmr777.customview.myView;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.qmr777.customview.R;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by qmr777 on 16-5-9.
 *
 */
public class MyImgView extends ImageView{

    public MyImgView(Context context) {
        super(context);
    }

    public MyImgView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyImgView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //float his = event.getX();
        switch (event.getAction()){

            case MotionEvent.ACTION_DOWN:
                Log.v("sss","down");
                break;

            case MotionEvent.ACTION_MOVE:
                this.setBackground(null);
                this.setImageBitmap(
                        BitmapFactory.decodeResource(getResources(), R.drawable.ic_error_black_48dp));
                break;
            case MotionEvent.ACTION_UP:
                this.setBackground(null);
                this.setImageBitmap(
                        BitmapFactory.decodeResource(getResources(), R.drawable.ic_warning_black_48dp));
                break;
        }

        return true;
    }

}
