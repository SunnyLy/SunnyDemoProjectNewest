package com.smartbracelet.sunny.sunnydemo2.facedecotor;

import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.affectiva.android.affdex.sdk.Frame;
import com.affectiva.android.affdex.sdk.detector.CameraDetector;
import com.affectiva.android.affdex.sdk.detector.Detector;
import com.affectiva.android.affdex.sdk.detector.Face;
import com.smartbracelet.sunny.sunnydemo2.R;
import com.smartbracelet.sunny.sunnydemo2.utils.LogUtils;

import java.util.List;

/**
 * @Author sunny
 * @Date 2016/7/19  10:11
 * @Annotation Affdex 第三方面部识别SDK Demo
 */
public class AffdexFaceDector extends FragmentActivity implements CameraDetector.CameraEventListener, Detector.ImageListener,
        Detector.FaceListener {

    private FaceDecotorSurfaceView mSurfaceView;
    private Button mBtnStart_StopCamera;
    private Button mBtnSwitchCamera;
    private TextView mResult;

    private CameraDetector mCameraDetector;

    private int cameraPreviewWidth;
    private int cameraPreviewHeight;

    private boolean isAffdexSDKStarted = false;
    private boolean isPreviewCamera = true;//默认为前置摄像头

    public static void startAffdexFaceDector(Context context) {
        Intent targetIntent = new Intent(context, AffdexFaceDector.class);
        context.startActivity(targetIntent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_affdex_facedector);
        mBtnStart_StopCamera = (Button) findViewById(R.id.affdex_start_camera);
        mBtnSwitchCamera = (Button) findViewById(R.id.affdex_camera_type);
        mResult = (TextView) findViewById(R.id.affdex_result);
        mSurfaceView = (FaceDecotorSurfaceView) findViewById(R.id.affdex_surface_view);
        initCameraDetector();
    }

    private void initCameraDetector() {
        mCameraDetector = new CameraDetector(this, CameraDetector.CameraType.CAMERA_FRONT, mSurfaceView);
        mCameraDetector.setLicensePath("affdex.license");
        mCameraDetector.setImageListener(this);
        //mCameraDetector.setFaceListener(this);
        mCameraDetector.setOnCameraEventListener(this);
        mCameraDetector.setSendUnprocessedFrames(true);//不跳过未处理的帧

        mCameraDetector.setMaxProcessRate(5);

        mCameraDetector.setDetectSmile(true);
        mCameraDetector.setDetectAllEmotions(true);
        mCameraDetector.setDetectAllEmojis(true);
        mCameraDetector.setDetectAllExpressions(true);
        mCameraDetector.setDetectAttention(true);
       /* mCameraDetector.setDetectJoy(true);
        mCameraDetector.setDetectSadness(true);
        mCameraDetector.setDetectSmirk(true);
        mCameraDetector.setDetectSurprise(true);
        mCameraDetector.setDetectAllEmotions(true);*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isAffdexSDKStarted)
        startCamera();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
        stopCamera();
    }

    private void startCamera(){
        if (mCameraDetector != null && !mCameraDetector.isRunning()) {
            new Thread(){
                @Override
                public void run() {
                    mCameraDetector.start();
                    super.run();
                }
            }.start();

        }
    }

    private void stopCamera(){
        if (mCameraDetector != null && mCameraDetector.isRunning()) {
            new Thread(){
                @Override
                public void run() {
                    mCameraDetector.stop();
                    super.run();
                }
            }.start();

        }
    }


    /**
     * 启动或结束 Camera
     * @param view
     */
    public void onStart_StopCamera(View view){
        if(isAffdexSDKStarted){
            isAffdexSDKStarted = false;
            stopCamera();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mResult.setText("Stop Camera……");
                    mBtnStart_StopCamera.setText("Start Camera");
                }
            });


        }else {
            isAffdexSDKStarted = true;
            startCamera();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mResult.setText("Start Camera……");
                    mBtnStart_StopCamera.setText("Stop Camera");
                }
            });

        }
    }

    /**
     * 前后摄像头切换
     * @param view
     */
    public void switchCamera(View view){

        if(isPreviewCamera){
            isPreviewCamera = false;
            mCameraDetector.setCameraType(CameraDetector.CameraType.CAMERA_BACK);
            mBtnSwitchCamera.setText("前置摄像头");
        }else{
            isPreviewCamera = true;
            mCameraDetector.setCameraType(CameraDetector.CameraType.CAMERA_FRONT);
            mBtnSwitchCamera.setText("后置摄像头");
        }
    }

    /**
     * CameraDetector.CameraEventListener
     */
    @Override
    public void onCameraSizeSelected(int cameraWidth, int cameraHeight, Frame.ROTATE rotation) {

        //cameraWidth and cameraHeight report the unrotated dimensions of the camera frames,
        // so switch the width and height if necessary
        if (rotation == Frame.ROTATE.BY_90_CCW || rotation == Frame.ROTATE.BY_90_CW) {
            cameraPreviewWidth = cameraHeight;
            cameraPreviewHeight = cameraWidth;
        } else {
            cameraPreviewWidth = cameraWidth;
            cameraPreviewHeight = cameraHeight;
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSurfaceView.setCameraPreviewHeight(cameraPreviewHeight);
                mSurfaceView.setCameraPreviewWidth(cameraPreviewWidth);
                // mSurfaceView.setLayoutParams(params);
                mSurfaceView.requestLayout();
            }
        });

    }

    /**
     * Detector.ImageListener
     *
     * @param frame
     * @param v
     */
    @Override
    public void onImageResults(List<Face> faces, Frame frame, float v) {

        if (mCameraDetector != null) {
            // mCameraDetector.setSendUnprocessedFrames(true);
            if (faces == null)
                return; //frame was not processed

            if (faces.size() == 0){

                mResult.setText("No face found");
                return; //no face found
            }

            //For each face found
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("结果:\n");
            for (int i = 0; i < faces.size(); i++) {
                Face face = faces.get(i);
                LogUtils.e("expressions:"+face.expressions.getMouthOpen());
                int faceId = face.getId();

                /*
                =====================
                   Appearance
                =====================
                 */
                Face.GENDER genderValue = face.appearance.getGender();
                Face.GLASSES glassesValue = face.appearance.getGlasses();

                /*
                ======================
                    Some Emoji
                ======================
                 */
                float smiley = face.emojis.getSmiley();
                float laughing = face.emojis.getLaughing();
                float wink = face.emojis.getWink();
                //失望
                float disappointed = face.emojis.getDisappointed();
                //吻
                float kiss = face.emojis.getKissing();
                float relaxed = face.emojis.getRelaxed();
                float smirk_emoj = face.emojis.getSmirk();
                float flushed = face.emojis.getFlushed();
                float rage = face.emojis.getRage();
                float scream = face.emojis.getScream();
                //出舌头
                float tongue = face.emojis.getStuckOutTongue();
                //伸舌头，眨眼睛
                float tongue_wingEye = face.emojis.getStuckOutTongueWinkingEye();

                stringBuffer.append("表情:===============\n");
                stringBuffer.append(String.format("微笑的:%.2f",smiley)+"       ");
                stringBuffer.append(String.format("大笑的:%.2f",laughing)+"       ");
                stringBuffer.append(String.format("眨眼:%.2f",wink)+"\n");
                stringBuffer.append(String.format("失望的:%.2f",disappointed)+"       ");
                stringBuffer.append(String.format("吻:%.2f",kiss)+"       ");
                stringBuffer.append(String.format("放松的:%.2f",relaxed)+"\n");
                stringBuffer.append(String.format("傻笑的:%.2f",smirk_emoj)+"       ");
                stringBuffer.append(String.format("激动的:%.2f",flushed)+"       ");
                stringBuffer.append(String.format("愤怒的:%.2f",rage)+"\n");
                stringBuffer.append(String.format("尖叫:%.2f",scream)+"       ");
                stringBuffer.append(String.format("伸舌头:%.2f",tongue)+"       ");
                stringBuffer.append(String.format("伸舌头,眨眼睛:%.2f",tongue_wingEye)+"\n");


                /*
                ======================
                     Some Emotions
                ======================
                 */
                float joy = face.emotions.getJoy();
                float anger = face.emotions.getAnger();
                float disgust = face.emotions.getDisgust();
                stringBuffer.append("情绪:===============\n");
                stringBuffer.append(String.format("喜悦:%.2f",joy)+"\n");
                stringBuffer.append(String.format("生气:%.2f",anger)+"\n");
                stringBuffer.append(String.format("厌恶:%.2f",disgust)+"\n");

                /*
                ======================
                     Some Expressions
                ======================
                 */
                float smile = face.expressions.getSmile();
                //傻笑
                float smirk = face.expressions.getSmirk();
                //闭目
                float eyeClosure = face.expressions.getEyeClosure();
                //嘴巴
                float lipCornerDepressor = face.expressions.getLipCornerDepressor();
                float lipPress = face.expressions.getLipPress();
                float lipPucker = face.expressions.getLipPucker();
                float mouthOpen = face.expressions.getMouthOpen();
                //上嘴唇
                float upperLip = face.expressions.getUpperLipRaise();
                //眉毛
                float brow_furrow = face.expressions.getBrowFurrow();
                float brow_raise = face.expressions.getBrowRaise();
                float inner_brow_raise = face.expressions.getInnerBrowRaise();
                //chin 下巴
                float chinRaise = face.expressions.getChinRaise();
                //nose 鼻子
                float nose = face.expressions.getNoseWrinkle();
                float attention = face.expressions.getAttention();
                stringBuffer.append("面部表现:===============\n");
                stringBuffer.append(String.format("嘴角向下:%.2f",lipCornerDepressor));
                stringBuffer.append(String.format(",,闭目:%.2f",eyeClosure));
                stringBuffer.append(String.format(",,傻笑:%.2f",smirk));
                stringBuffer.append(String.format(",,微笑:%.2f",smile));
                stringBuffer.append(String.format(",,眉毛向上:%.2f",brow_raise));
                stringBuffer.append(String.format(",,张嘴:%.2f",mouthOpen));
                stringBuffer.append(String.format(",,鼻子:%.2f",nose));


                /*
                =======================
                     Measurements
                =======================
                 */
                float interocular_distance = face.measurements.getInterocularDistance();
                float yaw = face.measurements.orientation.getYaw();
                float roll = face.measurements.orientation.getRoll();
                float pitch = face.measurements.orientation.getPitch();

                //Face feature points coordinates
                PointF[] points = face.getFacePoints();

            }

            mResult.setText(stringBuffer.toString());
        }
    }

    /**
     * Detector.FaceListener
     */
    @Override
    public void onFaceDetectionStarted() {
        LogUtils.e("======onFaceDetectionStarted=========");
    }

    @Override
    public void onFaceDetectionStopped() {
        LogUtils.e("======onFaceDetectionStopped=========");
    }
}
