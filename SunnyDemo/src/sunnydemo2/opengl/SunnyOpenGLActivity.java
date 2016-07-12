package sunnydemo2.opengl;

import android.content.Context;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;

import com.smartbracelet.sunny.sunnydemo2.R;

/**
 * @Author sunny
 * @Date 2016/7/11  13:25
 * @Annotation
 */
public class SunnyOpenGLActivity extends FragmentActivity {

    private GLSurfaceView mGLSurfaceView;
    private SunnyGLRender mGLRender;

    private float mPreviousX;
    private float mPreviousY;
    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;

    public static void startSunnyOpenGLActivity(Context context) {
        Intent targetIntent = new Intent(context, SunnyOpenGLActivity.class);
        context.startActivity(targetIntent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_gl);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();
    }

    private void initView() {
        mGLSurfaceView = (GLSurfaceView) findViewById(R.id.demo_gl_surfaceview);
        mGLRender = new SunnyGLRender();
        mGLSurfaceView.setEGLContextClientVersion(2);
        mGLSurfaceView.setRenderer(mGLRender);
        //设置渲染的模式
        //当且仅当有变化时才渲染View
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        mGLSurfaceView.setOnTouchListener(mSurfaceViewTouchListener);
    }

    /**
     * GLSurfaceView的触摸处理
     */
    private View.OnTouchListener mSurfaceViewTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    float dx = x - mPreviousX;
                    float dy = y - mPreviousY;

                    //旋转中线上方方向
                    if(y > mGLSurfaceView.getHeight()/2){
                        dx = dx * -1;
                    }

                    //旋转中线左方方向
                    if(x < mGLSurfaceView.getWidth() / 2){
                        dy = dy * -1;
                    }

                    mGLRender.mAngle += (dx+dy) * TOUCH_SCALE_FACTOR;
                    mGLSurfaceView.requestRender();
                    break;
            }

            mPreviousY = y;
            mPreviousX = x;
            return true;
        }
    };
}
