package sunnydemo2.ad;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.UriMatcher;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.smartbracelet.sunny.sunnydemo2.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import sunnydemo2.ad.ui.CarouselADActivity;
import sunnydemo2.ad.ui.view.VerticalViewPagerActivity;
import sunnydemo2.bargraph.BarGraphActivity;
import sunnydemo2.bargraph.CurveChartActivity;
import sunnydemo2.bargraph.DivisionCircle2Activity;
import sunnydemo2.bargraph.DivisionCircleActivity;
import sunnydemo2.danmuku.DanmuKuActivity;
import sunnydemo2.pulltofresh.SunnyPullToFreshActivity;

/**
 * Created by sunny on 2015/11/12.
 * 自定义控件主Activity
 */
public class MainActivity extends Activity implements ViewSwitcher.ViewFactory {

    private TextSwitcher mTextViewSwitcher;
    private boolean isAutoing = true;


    private NumberPicker mNumberPickerYear;
    private NumberPicker mNumberMonth;
    private NumberPicker mNumberDay;


    private sunnydemo2.widget.NumberPicker mPickerYear;
    private sunnydemo2.widget.NumberPicker mPickerMonth;
    private sunnydemo2.widget.NumberPicker mPickerDay;

    private EditText mEditTextYear;
    private EditText mEidtTextMonth;
    private EditText mEditTextDay;

    private Calendar mMaxDate;
    private Calendar mMinDate;
    private Calendar mCurrentDate;

    private String[] mDisplayedValuesYear = new String[DEFAULT_END_YEAR - DEFAULT_START_YEAR + 1];
    private String[] mDisplayedValuesMonth = new String[DEFALUT_END_MONTH - DEFAULT_START_MONTH + 1];
    private String[] mDisplayedValuesDay = new String[DEFALUT_END_DAY - DEFAULT_START_DAY + 1];

    //年
    private static final int DEFAULT_START_YEAR = 1990;
    private static final int DEFAULT_END_YEAR = 2100;
    private int mCurrentYearIndex = 0;//当前年在展示列表中的索引

    private int mMaxYear = DEFAULT_END_YEAR;
    private int mMaxMonth = DEFALUT_END_MONTH;
    private int mMaxDay = DEFALUT_END_DAY;

    //月
    private static final int DEFAULT_START_MONTH = 1;
    private static final int DEFALUT_END_MONTH = 12;
    private int mCurrentMothIndex = 0;//当前月在展示列表中的索引

    //日
    private static final int DEFAULT_START_DAY = 1;
    private static final int DEFALUT_END_DAY = 30;
    private int mCurrentDayIndex = 0;//当前年在展示列表中的索引

    //弹幕
   /* private DanmakuView mDanmaku;
    private List<IDanmakuItem> mDanmakuList;*/
    private boolean isShowing = false;

    public static void startMainActivity(Context context) {
        Intent targetIntent = new Intent(context, MainActivity.class);
        context.startActivity(targetIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_main);

    }

    private void initCalender() {
        for (int i = 0; i < DEFAULT_END_YEAR - DEFAULT_START_YEAR + 1; i++) {
            mDisplayedValuesYear[i] = DEFAULT_START_YEAR + i + "年";
        }

        for (int j = 0; j < 12; j++) {
            mDisplayedValuesMonth[j] = DEFAULT_START_MONTH + j + "月";
        }
        for (int k = 0; k < 30; k++) {
            mDisplayedValuesDay[k] = DEFAULT_START_DAY + k + "日";
        }
        mMaxDate = getCalendarForLocale(mMaxDate, Locale.CHINA);
        mMinDate = getCalendarForLocale(mMinDate, Locale.CHINA);
        mCurrentDate = getCalendarForLocale(mCurrentDate, Locale.CHINA);

    }

    @Override
    public void onContentChanged() {
        mTextViewSwitcher = (TextSwitcher) findViewById(R.id.text_view_ad);
        mTextViewSwitcher.setFactory(this);

        mNumberPickerYear = (NumberPicker) findViewById(R.id.number_picker_year);
        mNumberPickerYear.setWrapSelectorWheel(false);
        mNumberMonth = (NumberPicker) findViewById(R.id.number_picker_month);
        mNumberDay = (NumberPicker) findViewById(R.id.number_picker_day);

        mPickerYear = (sunnydemo2.widget.NumberPicker) findViewById(R.id.sunny_picker_year);
        mPickerYear.setUnSelectedTextColor(Color.RED);//改变未选中字体颜色
        mPickerMonth = (sunnydemo2.widget.NumberPicker) findViewById(R.id.sunny_picker_month);
        mPickerDay = (sunnydemo2.widget.NumberPicker) findViewById(R.id.sunny_picker_day);

        mEditTextYear = (EditText) mPickerYear.findViewById(R.id.np__numberpicker_input);
        mEidtTextMonth = (EditText) mPickerMonth.findViewById(R.id.np__numberpicker_input);
        mEditTextDay = (EditText) mPickerDay.findViewById(R.id.np__numberpicker_input);

        mNumberPickerYear.clearFocus();
        initCalender();
        //年
        mNumberPickerYear.setDisplayedValues(mDisplayedValuesYear);
        mNumberPickerYear.setMaxValue(mDisplayedValuesYear.length - 1);
        mNumberPickerYear.setMinValue(0);
        mNumberPickerYear.setValue(3);

        mPickerYear.setDisplayedValues(mDisplayedValuesYear);
        mPickerYear.setMaxValue(mDisplayedValuesYear.length - 1);
        mPickerYear.setMinValue(0);
        mPickerYear.setValue(3);
        mPickerYear.setWrapSelectorWheel(false);//当值达到最大时，不再循环滚动

        //月
        mNumberMonth.setDisplayedValues(mDisplayedValuesMonth);
        mNumberMonth.setMaxValue(mDisplayedValuesMonth.length - 1);
        mNumberMonth.setMinValue(0);

        mPickerMonth.setDisplayedValues(mDisplayedValuesMonth);
        mPickerMonth.setMaxValue(mDisplayedValuesMonth.length - 1);
        mPickerMonth.setMinValue(1);
        mPickerMonth.setWrapSelectorWheel(false);//当值达到最大时，不再循环滚动

        //日
        mNumberDay.setDisplayedValues(mDisplayedValuesDay);
        mNumberDay.setMaxValue(mDisplayedValuesDay.length - 1);
        mNumberDay.setMinValue(DEFAULT_START_DAY);

        mPickerDay.setDisplayedValues(mDisplayedValuesDay);
        mPickerDay.setMaxValue(mDisplayedValuesDay.length - 1);
        mPickerDay.setMinValue(DEFAULT_START_DAY);
        mPickerDay.setWrapSelectorWheel(false);//当值达到最大时，不再循环滚动

       /* mDanmaku = (DanmakuView) findViewById(R.id.danmakuView);
        initDanmaKu();*/
    }

  /*  private void initDanmaKu() {
        mDanmakuList = initItems();
        mDanmaku.clear();
        mDanmaku.addItem(mDanmakuList,true);
        mDanmaku.addItem(new DanmakuItem(this,"呵呵，呵呵",mDanmaku.getWidth()));
    }

    private List<IDanmakuItem> initItems() {
        List<IDanmakuItem> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            IDanmakuItem item = new DanmakuItem(this, i + " : plain text danmuku", mDanmaku.getWidth());
            list.add(item);
        }

        String msg = " : text with image   ";
        for (int i = 0; i < 100; i++) {
            ImageSpan imageSpan = new ImageSpan(this, R.drawable.ic_launcher);
            SpannableString spannableString = new SpannableString(i + msg);
            spannableString.setSpan(imageSpan, spannableString.length() - 2, spannableString.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            IDanmakuItem item = new DanmakuItem(this, spannableString, mDanmaku.getWidth(), 0, 0, 0, 1.5f);
            list.add(item);
        }
        return list;
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        autoSwitcher();
        //mDanmaku.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
       /* if(mDanmaku != null){
            mDanmaku.clear();
        }*/
    }

    /**
     * 自动循环播放
     */
    private void autoSwitcher() {
        int i = -1;
                    while (i == -1) {
                        i ++;
                        final int finalI = i;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mTextViewSwitcher.setText("文字切换类似广告轮播:" + finalI);
                            }
                        });
                        if(i>=5){
                            i = -1;
                        }
                    }

    }

    private int getMaxIndex(int maxYear) {
        int index = 0;
        for (int i = 0; i < mDisplayedValuesYear.length; i++) {
            String year = mDisplayedValuesYear[i];
            if (year.equals(String.valueOf(maxYear))) {
                index = i;
            }
        }

        return index;
    }

    public void jumpTextLinkify(View view) {
        TextLinkifyActivity.startTextLinkifyActivity(this);
    }

    public void jumpAD(View view) {
        Intent targetIntent = new Intent(this, CarouselADActivity.class);
        startActivity(targetIntent);
    }

    public void jumpToBarGraph(View view) {
        Intent targetIntent = new Intent(this, BarGraphActivity.class);
        startActivity(targetIntent);
    }

    public void jumpToDivishCircle(View view) {
        Intent targetIntent = new Intent(this, DivisionCircleActivity.class);
        startActivity(targetIntent);
    }

    public void jumpToDivishCircle2(View view) {
        Intent targetIntent = new Intent(this, DivisionCircle2Activity.class);
        startActivity(targetIntent);
    }

    /**
     * 跳转至Android弹幕
     * @param view
     */
    public void jumpDanmuKu(View view){

      /*  if(isShowing){
            isShowing = false;
            mDanmaku.hide();
        }else{
            isShowing = true;
            mDanmaku.show();
        }*/

        DanmuKuActivity.startDanmuKuActivity(this);
    }

    /**
     * 跳转至罗盘
     *
     * @param view
     */
    public void jumpToComprassView(View view) {

        ComprassViewActivity.startCompassViewActivity(this);
    }

    public void jumpToCurveChart(View view) {
        Intent targetIntent = new Intent(this, CurveChartActivity.class);
        startActivity(targetIntent);
    }

    public void jumpToVerticalViewPager(View view) {
        VerticalViewPagerActivity.startVerticalViewPagerActivity(this);
    }

    /**
     * 下拉刷新控件
     */
    public void jumpToPullToFreshActivity(View view) {
        SunnyPullToFreshActivity.startSunnyPullToFreshActivity(this);
    }

    /**
     * 显示时间选择对话框
     *
     * @param view
     */
    public void showTimeDialog(View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            }
        }, 2015, 12, 3);

        datePickerDialog.show();
    }


    private Calendar getCalendarForLocale(Calendar oldCalendar, Locale locale) {
        if (oldCalendar == null) {
            return Calendar.getInstance(locale);
        } else {
            final long currentTimeMillis = oldCalendar.getTimeInMillis();
            Calendar newCalendar = Calendar.getInstance(locale);
            newCalendar.setTimeInMillis(currentTimeMillis);
            return newCalendar;
        }
    }


    public void onSure(View view) {

        String year = mEditTextYear.getText().toString();
        String month = mEidtTextMonth.getText().toString();
        String day = mEditTextDay.getText().toString();
        Toast.makeText(MainActivity.this, year + "-" + month + "-" + day, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isAutoing = false;
    }

    @Override
    public View makeView() {
        //ViewFactory创建一个视图，并返回
        TextView textView = new TextView(this);
        textView.setText("Test");
        textView.setTextSize(36);
        return textView;
    }
}
