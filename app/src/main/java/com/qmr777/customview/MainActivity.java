package com.qmr777.customview;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.qmr777.customview.myView.CircleProcessBar;
import com.qmr777.customview.myView.CustomView1;
import com.qmr777.customview.myView.CustomView2;
import com.qmr777.customview.myView.MyButton;
import com.qmr777.customview.myView.MyImgView;
import com.qmr777.customview.myView.MyImgView2;
import com.qmr777.customview.myView.MyProcessBar;
import com.qmr777.customview.util.BitmapUtil;

public class MainActivity extends AppCompatActivity {
    Button button;
    MyImgView myImgView;
    CustomView1 customView1;
    CustomView2 customView2;
    MyProcessBar myProcessBar;
    CircleProcessBar circleProcessBar;
//    MyImgView2 myImgView2;
    int p = 0;

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            myProcessBar.setProcess(p);
            circleProcessBar.setProcess(p);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (MyButton) findViewById(R.id.btn);
        customView1 = (CustomView1) findViewById(R.id.customView_1);
        customView2 = (CustomView2) findViewById(R.id.customView_2);
        myProcessBar = (MyProcessBar) findViewById(R.id.myProcessBar);
        circleProcessBar = (CircleProcessBar) findViewById(R.id.circleProcessBar);

        myProcessBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        //myProcessBar.setProcess(50);

        customView1.setOnProgressChanged(new CustomView1.setOnProgressChanged() {
            @Override
            public void setOnProgressChanged(float left, float right) {
                Toast.makeText(MainActivity.this,left+"  "+right,Toast.LENGTH_SHORT).show();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"121212",Toast.LENGTH_SHORT).show();
            }
        });

        new Thread(runnable).start();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            while (p++<=100){
                mHandler.sendEmptyMessage(0);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_aty,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_setting:
                Toast.makeText(MainActivity.this,"setting",Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
