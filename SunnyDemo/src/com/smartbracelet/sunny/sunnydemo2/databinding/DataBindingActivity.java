package com.smartbracelet.sunny.sunnydemo2.databinding;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by sunny on 2016/3/14.
 * Annotion:Android中使用DataBinding案例
 */
public class DataBindingActivity extends AppCompatActivity {

    public static final String TAG = "DataBindingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /*com.smartbracelet.sunny.sunnydemo2.databinding.ActivityDataBindingBinding dataBindingBinding = DataBindingUtil.setContentView(this,R.layout.activity_data_binding);
        TestUser testUser = new TestUser();
        testUser.setUserName("Sunny");
        testUser.setAge("20");
        testUser.setAddress("Shenzhen");
        dataBindingBinding.setUser(testUser);*/
    }
}
