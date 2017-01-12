package com.wcl.guide.demo;

import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.wcl.guide.Guide;

public class MainActivity extends AppCompatActivity
{

    private com.wcl.guide.Guide mHightLight;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        findViewById(R.id.id_btn_amazing).post(
                new Runnable()
                {
                    @Override
                    public void run()
                    {
                        showTipMask();
                    }
                }

        );
    }

    private void showTipMask()
    {
        mHightLight = new Guide(MainActivity.this)
                .anchor(findViewById(R.id.id_container))
                .animIn(R.anim.anim_fade_in)
                .animOut(R.anim.anim_fade_out)
                .next()//创建一页引导
                .shadow(true)//是否显示描边阴影
                .addHighLight(R.id.id_btn_important_right, R.layout.info_gravity_right_up, new Guide.OnPosCallback(){
                    @Override
                    public void getPos(float rightMargin, float bottomMargin, RectF rectF, Guide.LayoutParams layoutParams) {
                        layoutParams.rightMargin = rightMargin;
                        layoutParams.topMargin = rectF.top + rectF.height();
                    }
                })
                .addHighLight(R.id.id_btn_whoami, R.layout.info_gravity_left_down, new Guide.OnPosCallback() {
                    @Override
                    public void getPos(float rightMargin, float bottomMargin, RectF rectF, Guide.LayoutParams layoutParams) {
                        layoutParams.leftMargin = rectF.right - rectF.width()/2;
                        layoutParams.bottomMargin = bottomMargin + rectF.height();
                    }
                })
                .addControlView(R.layout.control_1, new Guide.OnPosCallback() {
                    @Override
                    public void getPos(float rightMargin, float bottomMargin, RectF rectF, Guide.LayoutParams layoutParams) {
                        layoutParams.rightMargin = 10;
                        layoutParams.bottomMargin = 10;
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(v.getId() == R.id.btn_next) {
                            mHightLight.showNext();
                        }
                    }
                })
                .addMarkView(R.id.id_btn_whoami_center, R.layout.mark_2, new Guide.OnPosCallback() {
                    @Override
                    public void getPos(float rightMargin, float bottomMargin, RectF rectF, Guide.LayoutParams layoutParams) {
                        int addSize = 20;
                        layoutParams.leftMargin = rectF.left - addSize / 2;
                        layoutParams.topMargin = rectF.top - addSize / 2;
                        layoutParams.width = layoutParams.width + addSize;
                        layoutParams.height = layoutParams.height + addSize;
                    }
                })

                .next()
                .shadow(false)
                .addHighLight(R.id.id_btn_important, R.layout.info_gravity_right_up, new Guide.OnPosCallback(){
                    @Override
                    public void getPos(float rightMargin, float bottomMargin, RectF rectF, Guide.LayoutParams layoutParams) {
                        layoutParams.rightMargin = rightMargin;
                        layoutParams.topMargin = rectF.top + rectF.height();
                    }
                })
                .addHighLight(R.id.id_btn_whoami, R.layout.info_gravity_left_down, new Guide.OnPosCallback() {
                    @Override
                    public void getPos(float rightMargin, float bottomMargin, RectF rectF, Guide.LayoutParams layoutParams) {
                        layoutParams.leftMargin = rectF.right - rectF.width()/2;
                        layoutParams.bottomMargin = bottomMargin + rectF.height();
                    }
                })
                .addControlView(R.layout.control_2, new Guide.OnPosCallback() {
                    @Override
                    public void getPos(float rightMargin, float bottomMargin, RectF rectF, Guide.LayoutParams layoutParams) {
                        layoutParams.rightMargin = 10;
                        layoutParams.bottomMargin = 10;
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(v.getId() == R.id.btn_next) {
                            mHightLight.remove();
                        }else {
                            mHightLight.showPre();
                        }
                    }
                })
                .addMarkView(R.id.id_btn_whoami_center, BitmapFactory.decodeResource(getResources(), R.drawable.mark_2), new Guide.OnPosCallback() {
                    @Override
                    public void getPos(float rightMargin, float bottomMargin, RectF rectF, Guide.LayoutParams layoutParams) {

                    }
                })
                .end()
        .intercept(false);
        mHightLight.show();
    }

    public void remove(View view)
    {
        mHightLight.remove();
    }

    public void add(View view)
    {
        showTipMask();
    }
}
