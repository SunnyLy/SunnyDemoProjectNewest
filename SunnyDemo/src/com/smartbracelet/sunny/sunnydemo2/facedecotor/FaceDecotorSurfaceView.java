package com.smartbracelet.sunny.sunnydemo2.facedecotor;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;

/**
 * @Author sunny
 * @Date 2016/7/19  13:31
 * @Annotation
 */
public class FaceDecotorSurfaceView extends SurfaceView {

    int cameraPreviewWidth;
    int cameraPreviewHeight;

    public void setCameraPreviewWidth(int cameraPreviewWidth) {
        this.cameraPreviewWidth = cameraPreviewWidth;
    }

    public void setCameraPreviewHeight(int cameraPreviewHeight) {
        this.cameraPreviewHeight = cameraPreviewHeight;
    }

    public FaceDecotorSurfaceView(Context context) {
        super(context);
    }

    public FaceDecotorSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FaceDecotorSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        int measureWidth = MeasureSpec.getSize(widthSpec);
        int measureHeight = MeasureSpec.getSize(heightSpec);
        int width;
        int height;
        if (cameraPreviewHeight == 0 || cameraPreviewWidth == 0) {
            width = measureWidth;
            height = measureHeight;
        } else {
            float viewAspectRatio = (float)measureWidth/measureHeight;
            float cameraPreviewAspectRatio = (float) cameraPreviewWidth/cameraPreviewHeight;

            if (cameraPreviewAspectRatio > viewAspectRatio) {
                width = measureWidth;
                height =(int) (measureWidth / cameraPreviewAspectRatio);
            } else {
                width = (int) (measureHeight * cameraPreviewAspectRatio);
                height = measureHeight;
            }
        }
        setMeasuredDimension(width,height);
    }
}
