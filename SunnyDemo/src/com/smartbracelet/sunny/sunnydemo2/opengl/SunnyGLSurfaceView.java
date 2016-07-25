package com.smartbracelet.sunny.sunnydemo2.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * @Author sunny
 * @Date 2016/7/12  09:13
 * @Annotation
 */
public class SunnyGLSurfaceView extends GLSurfaceView {

    private float mPreviousX;
    private float mPreviousY;
    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;

    private SunnyGLRender mGLRender;


    public SunnyGLSurfaceView(Context context) {
        super(context);
    }

    public SunnyGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setGLRender(SunnyGLRender GLRender) {
        mGLRender = GLRender;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.e("glSurfaceView","down=========");
                break;
            case MotionEvent.ACTION_UP:
                Log.e("glSurfaceView","up=========");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.e("glSurfaceView","move=========");
                float dx = x - mPreviousX;
                float dy = y - mPreviousY;

                //旋转中线上方方向
                if(y > getHeight()/(float)2){
                    dx = dx * -1;
                }

                //旋转中线左方方向
                if(x < getWidth() / 2){
                    dy = dy * -1;
                }


                mGLRender.mAngle += (dx+dy) * TOUCH_SCALE_FACTOR;
                requestRender();
                break;
        }

        mPreviousY = y;
        mPreviousX = x;
        return true;
    }
}
