package com.smartbracelet.sunny.sunnydemo2.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;


import com.smartbracelet.sunny.sunnydemo2.widget.CommonLoadingDialog;

/**
 * Created by sunny on 2015/12/29.
 * Annotion:
 */
public class BaseActivity extends FragmentActivity {

    public Context mContext;
    public CommonLoadingDialog.LoadingDialog mCommonLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

        mContext = this;
        //EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       // EventBus.getDefault().unregister(this);
    }


    public void showDialog() {
        if (mCommonLoadingDialog == null) {
            mCommonLoadingDialog = CommonLoadingDialog.newInstance(mContext);
        }
        mCommonLoadingDialog.show(null);
    }

    public void hideDialog() {
        if (mCommonLoadingDialog != null && mCommonLoadingDialog.isShowing()) {
            mCommonLoadingDialog.hide();
            mCommonLoadingDialog = null;
        }
    }

}
