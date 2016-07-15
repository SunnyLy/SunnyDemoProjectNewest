package sunnydemo2.opengl.cubtriangle;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import sunnydemo2.utils.LogUtils;

/**
 * @Author sunny
 * @Date 2016/7/13  17:10
 * @Annotation 立体三角形
 */
public class SunnyCubTriangleRender implements GLSurfaceView.Renderer {
    public static final String TAG = SunnyCubTriangleRender.class.getSimpleName();

    private int mProgram;
    private int maPositionHandle;
    private int maColorHandle;
    private int muMVPMatrixHandle;

    private int mTextureID;

    //记录模型、视图、投影变换的[4*4]变换总矩阵
    private float[] mMVPMatrix = new float[16];
    //记录投影变换的[4*4]矩阵
    private float[] mProjMatrix = new float[16];
    //记录模型变换的[4*4]矩阵
    private float[] mMMatrix = new float[16];
    //记录视图变换的[4*4]矩阵
    private float[] mVMatrix = new float[16];

    public int mAngleX;
    public int mAngleY;
    public int angle;

    private Context mContext;

    private FloatBuffer mTriangleVerticesBuffer;
    private FloatBuffer mColorBuffer;
    private ByteBuffer  mIndexBuffer;
    /**
     * X,Y,Z三轴坐标
     * U,V也即S,T，是图片，视频等以纹理的形式加载到GLSurfaceView中时的坐标
     * U,V是无方向的
     */
   /* private final float[] mTriangleVerticesData = {
            // X, Y, Z, U, V
            -1.0f, -0.5f, 0, -0.5f, 0.0f,
            1.0f, -0.5f, 0, 1.5f, -0.0f,
            0.0f,  1.11803399f, 0, 0.5f,  1.61803399f,

    };*/
    //int one = 0x10000;
            float one = 1.0f;
    //以立方体的中心为世界坐标系的原点
    private final float[] mTriangleVerticesData = {
            //X,Y,Z
            -one, -0, one,//0
            one, 0, one,//1
            one,  one, one,//2
            -one,  one, one,//3
            -one, 0,  0,//4
            one, 0,  0,//5
            one,  one,  0,//6
            -one,  one,  0,//7

    };



    //颜色值
    private final float[] mColorBufferData = {
            0,    0,    0,  one,
            one,    0,    0,  one,
            one,  one,    0,  one,
            0,  one,    0,  one,
            0,    0,  one,  one,
            one,    0,  one,  one,
            one,  one,  one,  one,
            0,  one,  one,  one,
    };

    //VBO索引
    //OpenGL是以三角形为基本图元来进行绘制其它多边形的，
    //比如，一个立方体的一面由两个三角形组成
    //则VBO索引的意思：0，4，5，为一个面的一个三角形，0，5，1为同一面的另一个三角形
    //因此0，4，5 与 0，5，1就组成了立方体其中的一个面，
    //以此类推
    byte indices[] = {
            0, 4, 5,    0, 5, 1,//
            1, 5, 6,    1, 6, 2,
            2, 6, 7,    2, 7, 3,
            3, 7, 4,    3, 4, 0,
            4, 7, 6,    4, 6, 5,
            3, 0, 1,    3, 1, 2
    };

    private static final int FLOAT_SIZE_BYTES = 4;
    private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 5 * FLOAT_SIZE_BYTES;
    private static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
    private static final int TRIANGLE_VERTICES_DATA_UV_OFFSET = 3;

    public SunnyCubTriangleRender(Context context) {
        mContext = context;
        mTriangleVerticesBuffer = ByteBuffer.allocateDirect(mTriangleVerticesData.length
                * FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTriangleVerticesBuffer.put(mTriangleVerticesData).position(0);

        mColorBuffer = ByteBuffer.allocateDirect(mColorBufferData.length * FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mColorBuffer.put(mColorBufferData).position(0);

        mIndexBuffer = ByteBuffer.allocateDirect(indices.length);
        mIndexBuffer.put(indices);
        mIndexBuffer.position(0);
    }

    /**
     * 顶点着色器的语句
     */
    private final String mVertexShader =
            "uniform mat4 uMVPMatrix;\n" +
                    "attribute vec4 aPosition;\n" +
                    // "attribute vec2 aTextureCoord;\n" +
                    "attribute vec4 aColor;\n" +
                    // "varying vec2 vTextureCoord;\n" +
                    "varying vec4 vColor;\n" +
                    "void main() {\n" +
                    "  gl_Position = uMVPMatrix * aPosition;\n" +
                    //"  vTextureCoord = aTextureCoord;\n" +
                    " vColor = aColor;\n" +
                    "}\n";

    /**
     * 片元着色器的语句
     */
    private final String mFragmentShader =
            "precision mediump float;\n" +
                    // "varying vec2 vTextureCoord;\n" +
                    "varying vec4 vColor;\n" +
                   // "uniform sampler2D sTexture;\n" +
                    "void main() {\n" +
                    // "  gl_FragColor = texture2D(sTexture, vTextureCoord);\n" +
                    "gl_FragColor = vColor;\n" +
                    "}\n";

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        LogUtils.e("=========onSurfaceCreated==========");

        //GL10中有4个内置的矩阵：GL_MODELVIEW,GL_PROJECTION,GL_TEXTURE,GL_COLOR
        //而在OpenGL v3.0+、OpenGL ES v2.0+与WebGL v1.0+等可编程的管线中，不能使用上述内置矩阵函数，
        //此时必须拥有属于自己的矩阵实现，并且向OpenGL着色语言传递该矩阵数据
        //因为GLES20不能再像ApiDemo中那样使用gl.glMatrixMode(GL_MODELVIEW)内置矩阵函数。

        // GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        //启用剔除
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        //开启深度测试
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        mProgram = createShaderProgram(mVertexShader, mFragmentShader);
        if (mProgram == 0) {
            return;
        }
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        if (maPositionHandle == -1) {
            throw new RuntimeException("Could not get attrib location for aPosition");
        }
        maColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");
        if (maColorHandle == -1) {
            throw new RuntimeException("Could not get attrib location for aColor");
        }
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        if (muMVPMatrixHandle == -1) {
            throw new RuntimeException("Could not get attrib location for uMVPMatrix");
        }

        /*
        *设定相机的视角
        * //调用此方法产生摄像机9参数位置矩阵
         */
        Matrix.setLookAtM(mVMatrix, 0,
                0, 0, -5.0f, //相机的x,y,z坐标
                0f, 0f, 0f, //目标对应的x,y,z坐标
                0f, 1.0f, 0.0f//相机的视觉向量(upx,upy,upz,三个向量最终的合成向量的方向为相机的方向)
               /* -8f,1f,0,
                0f,0f,0f,
                0f,1.0f,1.0f*/
        );

    }

    /**
     * 创建着色器脚本
     *
     * @param vertexSource
     * @param fragmentSource
     * @return
     */
    private int createShaderProgram(String vertexSource, String fragmentSource) {
        int program = 0;
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == 0) {
            return 0;
        }

        int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (pixelShader == 0) {
            return 0;
        }

        //两种着色加载资源成功，下面运行脚本生成着色器
        program = GLES20.glCreateProgram();
        if (program != 0) {
            //关联着色器
            GLES20.glAttachShader(program, vertexShader);
            GLES20.glAttachShader(program, pixelShader);

            //连接着色器
            GLES20.glLinkProgram(program);
            //查看着色器连接情况
            int[] likProgramResult = new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, likProgramResult, 0);
            if (likProgramResult[0] != GLES20.GL_TRUE) {
                //连接失败，释放资源
                GLES20.glDeleteProgram(program);
                program = 0;
            }

        }

        return program;

    }

    /**
     * 加载着色器
     *
     * @param shaderType   着色器的类型
     * @param shaderSource 着色器的脚本语句
     * @return
     */
    private int loadShader(int shaderType, String shaderSource) {

        int shader = GLES20.glCreateShader(shaderType);
        if (shader != 0) {
            //加载脚本语句到OpenGL环境中
            GLES20.glShaderSource(shader, shaderSource);
            //编译脚本
            GLES20.glCompileShader(shader);
            //查看编译结果
            int[] compileResult = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileResult, 0);
            if (compileResult[0] == 0) {
                //编译失败
                Log.e(TAG, "Could not compile shader " + shaderType + ":");
                //释放资源
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        LogUtils.e("=========onSurfaceChanged==========");
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 1.0f;
        final float far = 10.0f;
        //调用此方法计算产生透视投影矩阵
        Matrix.frustumM(mProjMatrix, 0, left, right, bottom, top, near, far);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        LogUtils.e("=========onDrawFrame==========");
        //glClear:清除缓冲区标志，这里为：清除颜色缓冲及深度缓冲，把整个窗口清除为蓝色背景
       /* GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        //渲染蓝色背景，单独使用glClearColor并不会使窗口变色，要配合使用glClear清除缓冲标志
        GLES20.glClearColor(0.0f, 0.0f, 1.0f, 1.0f);*/
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        //使用着色器脚本程序
        GLES20.glUseProgram(mProgram);



        /*//OpenGL是用来对图片(Bitmap)进行滤镜渲染的，
        //当然不会直接操作Bitmap,而是间接通过GLSurfaceView来进行渲染的
        //而Bitmap不能直接加载到GLSurfaceView中，只能通过纹理(Texture)的形式加载进来
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureID);*/

        mTriangleVerticesBuffer.position(0);
        //给着色器的aPosition参数赋值，这里是三角形，所以3个物体顶点
        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false,
                0, mTriangleVerticesBuffer);
        //mTriangleVerticesBuffer.position(TRIANGLE_VERTICES_DATA_UV_OFFSET);
        //使用着色器aPosition参数的值
        GLES20.glEnableVertexAttribArray(maPositionHandle);


        mColorBuffer.position(0);
        //给着色器的aColor参数赋值
        GLES20.glVertexAttribPointer(maColorHandle, 4, GLES20.GL_FLOAT, false,
                0, mColorBuffer);
        //使用着色器的aTextureCoorde的值
        GLES20.glEnableVertexAttribArray(maColorHandle);

       /* long time = SystemClock.uptimeMillis() % 1000L;
        float angle = (360.0f / 10000.0f)* ((int) time);*/
        Matrix.setIdentityM(mMMatrix, 0);
        Matrix.translateM(mMMatrix, 0, 0.0f, 0.0f, -3.0f);
        Matrix.rotateM(mMMatrix, 0, angle, 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(mMMatrix, 0, angle*0.25f, 1.0f, 0.0f, 0);
        angle += 1.2f;

        /**
         * 可通过变换矩阵相乘得出想要变换矩阵
         */
        //把左边矩阵mVmatrix与右边矩阵mMMatrix相乘后的结果存储在总变换矩阵mMVPMatrix中
        Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, mMMatrix, 0);
        //把矩阵mProjMatrix与矩阵mMVPMatrix相乘后的结果存储在总变换矩阵mMVPMatrix中
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);

        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix, 0);


        //最后开始画三角形，绘制的方式有三种：点，线，面
        //这里以面的方式来画
        //GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES,36,GLES20.GL_UNSIGNED_BYTE,mIndexBuffer);
    }
}
