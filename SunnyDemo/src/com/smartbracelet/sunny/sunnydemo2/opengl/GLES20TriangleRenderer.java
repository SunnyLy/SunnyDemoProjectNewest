/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.smartbracelet.sunny.sunnydemo2.opengl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;

import com.smartbracelet.sunny.sunnydemo2.R;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

class GLES20TriangleRenderer implements GLSurfaceView.Renderer {

    public GLES20TriangleRenderer(Context context) {
        mContext = context;
        mTriangleVertices = ByteBuffer.allocateDirect(mTriangleVerticesData.length
                * FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTriangleVertices.put(mTriangleVerticesData).position(0);
    }

    public void onDrawFrame(GL10 glUnused) {
        // Ignore the passed-in GL10 interface, and use the GLES20
        // class's static methods instead.
        //glClear:清除缓冲区标志，这里为：清除颜色缓冲及深度缓冲，把整个窗口清除为蓝色背景
        GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        //渲染蓝色背景，单独使用glClearColor并不会使窗口变色，要配合使用glClear清除缓冲标志
        GLES20.glClearColor(0.0f, 0.0f, 1.0f, 1.0f);
        //使用着色器脚本程序
        GLES20.glUseProgram(mProgram);
        checkGlError("glUseProgram");

        //OpenGL是用来对图片(Bitmap)进行滤镜渲染的，
        //当然不会直接操作Bitmap,而是间接通过GLSurfaceView来进行渲染的
        //而Bitmap不能直接加载到GLSurfaceView中，只能通过纹理(Texture)的形式加载进来
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureID);

        mTriangleVertices.position(TRIANGLE_VERTICES_DATA_POS_OFFSET);
        //给着色器的aPosition参数赋值，这里是三角形，所以3个物体顶点
        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false,
                TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mTriangleVertices);
        checkGlError("glVertexAttribPointer maPosition");
        mTriangleVertices.position(TRIANGLE_VERTICES_DATA_UV_OFFSET);
        //使用着色器aPosition参数的值
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        checkGlError("glEnableVertexAttribArray maPositionHandle");


        //给着色器的aTextureCoorde参数赋值
        GLES20.glVertexAttribPointer(maTextureHandle, 2, GLES20.GL_FLOAT, false,
                TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mTriangleVertices);
        checkGlError("glVertexAttribPointer maTextureHandle");
        //使用着色器的aTextureCoorde的值
        GLES20.glEnableVertexAttribArray(maTextureHandle);
        checkGlError("glEnableVertexAttribArray maTextureHandle");

        long time = SystemClock.uptimeMillis() % 4000L;
        float angle = 0.090f * ((int) time);
        //创建一个绕x,y,z轴旋转一定角度的矩阵
        Matrix.setRotateM(mMMatrix, 0, angle, 0, 0, 1.0f);

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
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        checkGlError("glDrawArrays");
    }

    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        // Ignore the passed-in GL10 interface, and use the GLES20
        // class's static methods instead.
        GLES20.glViewport(0, 0, width+200, height+200);
        float ratio = (float) width / height;
        //调用此方法计算产生透视投影矩阵
        Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        // Ignore the passed-in GL10 interface, and use the GLES20
        // class's static methods instead.
        mProgram = createProgram(mVertexShader, mFragmentShader);
        if (mProgram == 0) {
            return;
        }
        //获取着色器参数 aPosition
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        checkGlError("glGetAttribLocation aPosition");
        if (maPositionHandle == -1) {
            throw new RuntimeException("Could not get attrib location for aPosition");
        }
        //获取着色器参数 aTextureCoord
        maTextureHandle = GLES20.glGetAttribLocation(mProgram, "aTextureCoord");
        checkGlError("glGetAttribLocation aTextureCoord");
        if (maTextureHandle == -1) {
            throw new RuntimeException("Could not get attrib location for aTextureCoord");
        }

        //获取着色器参数 uMVPMatrix
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        checkGlError("glGetUniformLocation uMVPMatrix");
        if (muMVPMatrixHandle == -1) {
            throw new RuntimeException("Could not get attrib location for uMVPMatrix");
        }

        /*
         * Create our texture. This has to be done each time the
         * surface is created.
         */

        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);

        mTextureID = textures[0];
        /*
        **glBindTexture:绑定纹理。
        * 绑定成功后，其就会包含一定数目的可用纹素
         */
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureID);

        /*
        *
        * 纹理的取样方式：取邻近值，取线性值，循环模式
        * 当然还有一些别的取样方式，如：拉伸，压缩，旋转
         */
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_REPEAT);

        InputStream is = mContext.getResources()
            .openRawResource(R.raw.robot);
        Bitmap bitmap;
        try {
           // bitmap = BitmapFactory.decodeStream(is);
            bitmap = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.p1);
        } finally {
            try {
                is.close();
            } catch(IOException e) {
                // Ignore.
            }
        }

        /*
        *让图片与纹理关联起来，加载到OpenGL中
         */
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

        /*
        *回收图片资源
         */
        bitmap.recycle();

        /*
        *设定相机的视角
        * //调用此方法产生摄像机9参数位置矩阵
         */
        Matrix.setLookAtM(mVMatrix, 0,
                0, 0, -5, //相机的x,y,z坐标
                0f, 0f, 0f, //目标对应的x,y,z坐标
                0f, 1.0f, 0.0f//相机的视觉向量(upx,upy,upz,三个向量最终的合成向量的方向为相机的方向)
        );
    }

    private int loadShader(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        if (shader != 0) {
            //加载着色器脚本程序
            GLES20.glShaderSource(shader, source);
            //编译着色器脚本程序
            GLES20.glCompileShader(shader);
            int[] compiled = new int[1];
            //查看编译结果
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
                //编译失败，释放申请的着色器
                Log.e(TAG, "Could not compile shader " + shaderType + ":");
                Log.e(TAG, GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }

    /**
     * 创建着色器脚本程序
     * 可以创建以.sh结尾的文件，放在assets目录下，语法跟C很相似，本demo是直接写在字符串里面了
     * @param vertexSource
     * @param fragmentSource
     * @return
     */
    private int createProgram(String vertexSource, String fragmentSource) {
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == 0) {
            return 0;
        }

        int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (pixelShader == 0) {
            return 0;
        }

        //申请特定的着色器，program != 0 申请成功
        int program = GLES20.glCreateProgram();
        if (program != 0) {
            GLES20.glAttachShader(program, vertexShader);
            checkGlError("glAttachShader");
            GLES20.glAttachShader(program, pixelShader);
            checkGlError("glAttachShader");
            //连接着色器程序
            GLES20.glLinkProgram(program);
            int[] linkStatus = new int[1];
            //查看着色器连接情况
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES20.GL_TRUE) {
                //连接失败
                Log.e(TAG, "Could not link program: ");
                Log.e(TAG, GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }
        return program;
    }

    private void checkGlError(String op) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, op + ": glError " + error);
            throw new RuntimeException(op + ": glError " + error);
        }
    }

    private static final int FLOAT_SIZE_BYTES = 4;
    private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 5 * FLOAT_SIZE_BYTES;
    private static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
    private static final int TRIANGLE_VERTICES_DATA_UV_OFFSET = 3;
    /**
     * X,Y,Z三轴坐标
     * U,V也即S,T，是图片，视频等以纹理的形式加载到GLSurfaceView中时的坐标
     * U,V是无方向的
     */
    private final float[] mTriangleVerticesData = {
            // X, Y, Z, U, V
            -1.0f, -0.5f, 0, -0.5f, 0.0f,
            1.0f, -0.5f, 0, 1.5f, -0.0f,
            0.0f,  1.11803399f, 0, 0.5f,  1.61803399f,
            2.0f, 1.11803399f,0,1.0f,1.61803399f,

    };

    private FloatBuffer mTriangleVertices;

    /**
     * 顶点着色器的语句
     */
    private final String mVertexShader =
        "uniform mat4 uMVPMatrix;\n" +
        "attribute vec4 aPosition;\n" +
        "attribute vec2 aTextureCoord;\n" +
        "varying vec2 vTextureCoord;\n" +
        "void main() {\n" +
        "  gl_Position = uMVPMatrix * aPosition;\n" +
        "  vTextureCoord = aTextureCoord;\n" +
        "}\n";

    /**
     * 碎片着色器的语句
     */
    private final String mFragmentShader =
        "precision mediump float;\n" +
        "varying vec2 vTextureCoord;\n" +
        "uniform sampler2D sTexture;\n" +
        "void main() {\n" +
        "  gl_FragColor = texture2D(sTexture, vTextureCoord);\n" +
        "}\n";

    //记录模型、视图、投影变换的[4*4]变换总矩阵
    private float[] mMVPMatrix = new float[16];
    //记录投影变换的[4*4]矩阵
    private float[] mProjMatrix = new float[16];
    //记录模型变换的[4*4]矩阵
    private float[] mMMatrix = new float[16];
    //记录视图变换的[4*4]矩阵
    private float[] mVMatrix = new float[16];

    private int mProgram;
    private int mTextureID;
    private int muMVPMatrixHandle;
    private int maPositionHandle;
    private int maTextureHandle;

    private Context mContext;
    private static String TAG = "GLES20TriangleRenderer";
}
