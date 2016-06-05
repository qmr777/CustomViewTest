package com.qmr777.customview.myView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

/**
 * Created by qmr777 on 16-5-10.
 */
public class MyButton extends Button {
    public MyButton(Context context) {
        super(context);
    }

    public MyButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                this.setBackgroundColor(getResources().getColor(android.R.color.black));
                this.setTextColor(getResources().getColor(android.R.color.holo_blue_bright));
                break;
            case MotionEvent.ACTION_UP:
                this.setBackground(getResources().getDrawable(android.R.drawable.btn_default, null));
                this.setTextColor(getResources().getColor(android.R.color.black));

        }
        return super.onTouchEvent(event);
    }
}
