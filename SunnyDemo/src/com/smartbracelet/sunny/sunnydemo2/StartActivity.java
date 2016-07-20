package com.smartbracelet.sunny.sunnydemo2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.smartbracelet.sunny.sunnydemo2.ad.MainActivity;
import com.smartbracelet.sunny.sunnydemo2.ad.ui.activitystartmode.ActivityStartMode;
import com.smartbracelet.sunny.sunnydemo2.ad.ui.ble.SunnyBLEActivity;
import com.smartbracelet.sunny.sunnydemo2.ad.ui.newproperties.NewPropertiesActivity;
import com.smartbracelet.sunny.sunnydemo2.androidl.AndroidLActivity;
import com.smartbracelet.sunny.sunnydemo2.facedecotor.AffdexFaceDector;
import com.smartbracelet.sunny.sunnydemo2.imageloader.PictureLoaderFrameActivity;
import com.smartbracelet.sunny.sunnydemo2.imagetext.ImageTextActivity;
import com.smartbracelet.sunny.sunnydemo2.mvp.presenter.LoginAtyPresenter;
import com.smartbracelet.sunny.sunnydemo2.network.OkHttpNetWorkActivity;
import com.smartbracelet.sunny.sunnydemo2.network.XutilsNetWorkActivity;
import com.smartbracelet.sunny.sunnydemo2.opengl.SunnyOpenGLActivity;
import com.smartbracelet.sunny.sunnydemo2.plugin.GrabRedpacketActivity;
import com.smartbracelet.sunny.sunnydemo2.rxjava.RxJavaActivity;
import com.smartbracelet.sunny.sunnydemo2.sharesdk.SunnyShareActivity;
import com.smartbracelet.sunny.sunnydemo2.tts.TTSActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sunny on 2015/11/18.
 * Annotion:
 */
public class StartActivity extends Activity implements AdapterView.OnItemClickListener {

    private ListView mListView;
    private SimpleAdapter mSimpleAdapter;
    private List<Map<String, String>> mDatas = new ArrayList<>();
    private String[] mFrom;
    private int[] mTo;

    private View mPopupView;
    private PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Fabric.with(this, new Crashlytics());
        //Crashlytics.start(this);
        setContentView(R.layout.activity_start);
        TextView start_task = (TextView) findViewById(R.id.start_task);
        start_task.setText("StartActivity所在TaskId:" + getTaskId());
    }

    @Override
    public void onContentChanged() {
        mListView = (ListView) findViewById(R.id.listview);
        mListView.setOnItemClickListener(this);
        initDatas();


        mSimpleAdapter = new SimpleAdapter(this, mDatas, R.layout.listview_item, mFrom, mTo);
        mListView.setAdapter(mSimpleAdapter);

    }

    private void initDatas() {
        mDatas.clear();
        String[] names = getResources().getStringArray(R.array.item_names);
        mFrom = new String[]{"text"};
        for (int i = 0; i < names.length; i++) {
            Map<String, String> data = new HashMap<>();
            data.put("text", names[i]);
            mDatas.add(data);
        }

        mTo = new int[]{R.id.item_btn};
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        switch (position) {
            case 0:
                MainActivity.startMainActivity(StartActivity.this);
                break;
            case 1:
                XutilsNetWorkActivity.startNetWorkActivity(StartActivity.this);
                break;
            case 2:
                OkHttpNetWorkActivity.startNetWorkActivity(StartActivity.this);
                break;
            case 3:
                ActivityStartMode.startActivityStartMode(this);
                break;
            case 4:
                SunnyBLEActivity.startSunnyBLEActivity(this);
                break;
            case 5:
                NewPropertiesActivity.startNewPropertiesActivity(this);
                break;
            case 6:
                PictureLoaderFrameActivity.startPictureLoaderFrameActivity(this);
                break;
            case 7:
                ImageTextActivity.startImageTextActivity(this);
                break;
            case 8:
                AndroidLActivity.startAndroidLActivity(this);
                break;
            case 9:
                RxJavaActivity.startRxJavaActivity(this);
                break;
            case 10:
                SunnyShareActivity.startSunnyShareActivity(this);
                break;
            case 11:
                TTSActivity.startTTSActivity(this);
                break;
            case 12:
                LoginAtyPresenter.startLoginAtyPresenter(this);
                break;

            case 13:
                // GrabRedpacketActivity.startGrabRedpacketActivity(this);
                Intent targeIntent = new Intent(StartActivity.this, GrabRedpacketActivity.class);
                startActivity(targeIntent);
                break;

            case 14:
                //OpenGL
                SunnyOpenGLActivity.startSunnyOpenGLActivity(this);
                break;
            case 15:
                //测试PopupWindow
                mPopupView = mListView.getAdapter().getView(position, LayoutInflater.from(this).inflate(R.layout.listview_item, null), null);
                View contentView = LayoutInflater.from(this).inflate(R.layout.acitivity_main, null);
                if(popupWindow == null){
                    popupWindow = new PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT, 1000);
                    popupWindow.setOutsideTouchable(true);
                }

                if(!popupWindow.isShowing())
                popupWindow.showAsDropDown(mPopupView == null ? mListView : mPopupView);
                else
                popupWindow.dismiss();
                break;

            case 16:
                //面部识别
                AffdexFaceDector.startAffdexFaceDector(this);
                break;
        }

    }


}
