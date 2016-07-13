package sunnydemo2.opengl;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @Author sunny
 * @Date 2016/7/11  13:31
 * @Annotation 创建自己的渲染类，实现GLSurfaceView.Renderer的三个方法：
 * onSurfaceCreated:在初始化GLSurfaceView环境时调用，我们可以在这里进行一些相应的配置
 * onSurfaceChanged:在比如横竖屏切换时，调用。
 * onDrawFrame:此方法在重绘时被调用
 */
public class SunnyGLRender implements GLSurfaceView.Renderer {

    private float[] mRotationMatrix = new float[16];

    private int mProgram;
    private int maPostionHandle;

    private FloatBuffer mTriangleVB;
    //定义顶点(vertex)阴影命令语句
    private final String vertexShaderCode =
            "uniform mat4 uMVPMatrix; \n" +
            "attribute vec4 vPosition; \n" +
                    "void main(){              \n" +
                    " gl_Position = vPosition; \n" +
                    "}                         \n";


    private final String fragmentShaderCode =
            "precision mediump float;\n" +
                    "void main(){ \n" +
                    " gl_FragColor = vec4 (0.63671875,0.76953125,0.22265625,1.0);\n" +
                    "}\n";

    //应用投影与相机视图
    private int muMVPMatrixHandle;
    private float[] mMVPMatrix = new float[16];
    private float[] mVMatrix = new float[16];
    private float[] mProjMatrix = new float[16];

    //添加动作
    private float[] mMMatrix = new float[16];

    public float mAngle;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        //GLES20:为OpenGL ES2.0版本,相应的
        //GLES30:OpenGL ES3.0
        //黑色背景
        GLES20.glClearColor(0.5f,0.5f,0.5f,1.0f);
        initShapes();
        int vertextShader = loadShader(GLES20.GL_VERTEX_SHADER,vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER,fragmentShaderCode);
        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram,vertextShader);
        GLES20.glAttachShader(mProgram,fragmentShader);
        GLES20.glLinkProgram(mProgram);

        maPostionHandle = GLES20.glGetAttribLocation(mProgram,"vPosition");
    }

    /**
     * 初始化三角形的一些参数
     */
    private void initShapes() {
        float trianlgeCoords[] = {
                //X,Y,Z
                -0.5f,-0.25f,0,
                0.5f,-0.25f,0,
                0,0.559016994f,0
        };

        ByteBuffer vbb = ByteBuffer.allocateDirect(trianlgeCoords.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        mTriangleVB = vbb.asFloatBuffer();
        mTriangleVB.put(trianlgeCoords);
        mTriangleVB.position(0);
    }

    /**
     * 加载Shader
     * @param type
     * @param shaderCode
     * @return
     */
    private int loadShader(int type,String shaderCode){

        int shader = GLES20.glCreateShader(type);

        GLES20.glShaderSource(shader,shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width/height;
        //把投影矩阵应用到坐标系中
        Matrix.frustumM(mProjMatrix,0,-ratio,ratio,-1,1,3,7);
        //当Sucrface改变时，利用投影获取MatrixHandle对象
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram,"uMVPMatrix");
        //接下来定义一个相机矩阵
        Matrix.setLookAtM(mVMatrix,0,0,0,-3,0,0,0,0,1.0f,1.0f);

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        //应用ModelView投影变换
        Matrix.multiplyMM(mMVPMatrix,0,mProjMatrix,0,mVMatrix,0);
      //  GLES20.glUniformMatrix4fv(muMVPMatrixHandle,1,false,mMVPMatrix,0);

        //为三角形创建一个旋转动作
        /*long time = SystemClock.uptimeMillis() % 4000L;
        mAngle = 0.090f * ((int)time);*/
        Matrix.setRotateM(mMMatrix,0,mAngle,0,0,1.0f);
        Matrix.multiplyMM(mMVPMatrix,0,mVMatrix,0,mMMatrix,0);
        Matrix.multiplyMM(mMVPMatrix,0,mProjMatrix,0,mMVPMatrix,0);
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle,1,false,mMVPMatrix,0);


        //把Program用到OpenGL环境中
        GLES20.glUseProgram(mProgram);
        //准备画三角形的数据
        GLES20.glVertexAttribPointer(maPostionHandle,3,GLES20.GL_FLOAT,false,12,mTriangleVB);
        GLES20.glEnableVertexAttribArray(maPostionHandle);

        //开始画
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,3);


    }
}
