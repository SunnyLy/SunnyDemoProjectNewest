package sunnydemo2.opengl;

import android.content.Context;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

/**
 * @Author sunny
 * @Date 2016/7/11  13:25
 * @Annotation
 */
public class SunnyOpenGLActivity extends FragmentActivity {

    //private SunnyGLSurfaceView mGLSurfaceView;
    //private SunnyGLRender mGLRender;
    private GLSurfaceView mGLSurfaceView;
    private GLES20TriangleRenderer mGLRender;

    public static void startSunnyOpenGLActivity(Context context) {
        Intent targetIntent = new Intent(context, SunnyOpenGLActivity.class);
        context.startActivity(targetIntent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  setContentView(R.layout.activity_open_gl);
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

      //  mGLSurfaceView = (SunnyGLSurfaceView) findViewById(R.id.demo_gl_surfaceview);
       // mGLSurfaceView = new SunnyGLSurfaceView(this);
        mGLSurfaceView = new GLSurfaceView(this);
        //mGLRender = new SunnyGLRender();
        mGLRender = new GLES20TriangleRenderer(this);
        mGLSurfaceView.setEGLContextClientVersion(2);
        mGLSurfaceView.setRenderer(mGLRender);
        // mGLSurfaceView.setGLRender(mGLRender);
        //设置渲染的模式
        //当且仅当有变化时才渲染View(即被动渲染),当把它注释掉时，三角形就会自动转起来
       // mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        //主动渲染，当不设置时，默认就是主动渲染
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        setContentView(mGLSurfaceView);
    }

}
