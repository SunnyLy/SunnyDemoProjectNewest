package sunnydemo2.opengl.cubtriangle;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

/**
 * @Author sunny
 * @Date 2016/7/14  09:13
 * @Annotation
 */
public class SunnyCubTriangleGLSurfaceView extends GLSurfaceView {
    private SunnyCubTriangleRender mCubTriangleRender;

    private float mPreviousX;
    private float mPreviousY;

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;

    public SunnyCubTriangleGLSurfaceView(Context context) {
        super(context);
        mCubTriangleRender = new SunnyCubTriangleRender(context);
        setEGLContextClientVersion(2);
        setRenderer(mCubTriangleRender);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    @Override
    public boolean onTrackballEvent(MotionEvent event) {
        mCubTriangleRender.mAngleX += event.getX() * 36.0f;
        mCubTriangleRender.mAngleY += event.getY() * 36.0f;
        requestRender();
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_MOVE:
                float dx = x - mPreviousX;
                float dy = y - mPreviousY;

                mCubTriangleRender.mAngleX += dx * TOUCH_SCALE_FACTOR;
                mCubTriangleRender.mAngleY += dy * TOUCH_SCALE_FACTOR;
                requestRender();
        }
        mPreviousX = x;
        mPreviousY = y;
        return true;
    }
}
