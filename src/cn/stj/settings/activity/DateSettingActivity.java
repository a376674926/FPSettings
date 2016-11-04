
package cn.stj.settings.activity;

import java.util.Calendar;
import java.util.HashMap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.Toast;

import cn.stj.settings.R;
import cn.stj.settings.presenter.SettingPresenter;
import cn.stj.settings.presenter.SettingPresenterImpl;
import cn.stj.settings.utils.DateStyle;
import cn.stj.settings.utils.DateTimeUtil;

/**
 * Date Setting Activity
 * 
 * @author jackey
 */
public class DateSettingActivity extends BaseActivity implements
        BaseActivity.BottomKeyClickListener {

    private SettingPresenter mSettingPresenter;
    private EditText mYearEditText;
    private EditText mMonthEditText;
    private EditText mDayEditText;
    private Calendar mDummyDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAboveViewStub.setLayoutResource(R.layout.activity_settings_date);
        setBottomKeyClickListener(this);

        initView();

        mSettingPresenter = new SettingPresenterImpl(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        registerReceiver(mDateReceiver, filter, null, null);
    }

    private void initView() {
        View dateView = mAboveViewStub.inflate();
        mYearEditText = (EditText) dateView.findViewById(R.id.setting_date_edit_year);
        mMonthEditText = (EditText) dateView.findViewById(R.id.setting_date_edit_month);
        mDayEditText = (EditText) dateView.findViewById(R.id.setting_date_edit_day);

        mYearEditText.addTextChangedListener(new DateTextWatcher());
        mMonthEditText.addTextChangedListener(new DateTextWatcher());
        mDayEditText.addTextChangedListener(new DateTextWatcher());
        mYearEditText.setOnFocusChangeListener(new DateFocusChangeListener());
        mMonthEditText.setOnFocusChangeListener(new DateFocusChangeListener());
        mDayEditText.setOnFocusChangeListener(new DateFocusChangeListener());
        mYearEditText.setSelection(mYearEditText.getText().toString().length());

        mDummyDate = Calendar.getInstance();

        updateTimeAndDateDisplay(this);
        setRightBtnText(getResources().getString(R.string.delete));
    }

    private class DateTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                int count) {
            String timeInfo = s.toString().replaceAll("\\s*", "");
            if (!TextUtils.isEmpty(timeInfo)) {
                setRightBtnText(getResources().getString(R.string.delete));
            } else {
                setRightBtnText(getResources().getString(R.string.back));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }

    }

    private class DateFocusChangeListener implements OnFocusChangeListener {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            String editInfo = ((EditText) v).getText().toString();
            if (hasFocus) {
                if (!TextUtils.isEmpty(editInfo)) {
                    setRightBtnText(getResources().getString(R.string.delete));
                } else {
                    setRightBtnText(getResources().getString(R.string.back));
                }
            }
        }
    }

    @Override
    public void onLeftKeyPress() {
        confirm();
    }

    /**
     * confirm
     */
    private void confirm() {
        String year = mYearEditText.getText().toString();
        String month = mMonthEditText.getText().toString();
        String day = mDayEditText.getText().toString();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        if (!TextUtils.isEmpty(year)) {
            calendar.set(Calendar.YEAR,
                    Integer.valueOf(year));
        }
        if (!TextUtils.isEmpty(month)) {
            calendar.set(Calendar.MONTH,
                    Integer.valueOf(month) - 1);
        }
        if (!TextUtils.isEmpty(day)) {
            calendar.set(Calendar.DAY_OF_MONTH,
                    Integer.valueOf(day));
        }
        setDate(calendar.getTimeInMillis());

    }

    private void setDate(long dateTime) {
        mSettingPresenter.setDateTime(this, dateTime);
        updateTimeAndDateDisplay(this);
    }

    @Override
    public void onMiddleKeyPress() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onRightKeyPress() {
        if (mYearEditText.isFocused()) {
            deleteEditTextInfo(mYearEditText);
        } else if (mMonthEditText.isFocused()) {
            deleteEditTextInfo(mMonthEditText);
        } else {
            deleteEditTextInfo(mDayEditText);
        }
    }

    public void deleteEditTextInfo(EditText editText) {
        String timeInfo = editText.getText().toString();
        if (TextUtils.isEmpty(timeInfo)) {
            launchActivity(DateTimeSettingActivity.class);
        } else {
            int mmsInfoLen = timeInfo.length();
            mmsInfoLen--;
            timeInfo = timeInfo.substring(0, mmsInfoLen);
            editText.setText(timeInfo);
            editText.setSelection(timeInfo.length());
        }
    }

    public void updateTimeAndDateDisplay(Context context) {
        final Calendar now = Calendar.getInstance();
        mDummyDate.setTimeZone(now.getTimeZone());
        mDummyDate.set(now.get(Calendar.YEAR), now.get(Calendar.MONDAY),
                now.get(Calendar.DAY_OF_MONTH), 13, 0, 0);
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH);
        int day = now.get(Calendar.DAY_OF_MONTH);
        mYearEditText.setText(year + "");
        mMonthEditText.setText(month + 1 + "");
        mDayEditText.setText(day + "");
        mYearEditText.setSelection(mYearEditText.getText().toString().length());
    }

    private BroadcastReceiver mDateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateTimeAndDateDisplay(DateSettingActivity.this);
        }
    };

    @Override
    public void onSuccess(HashMap<String, Object> respDatas) {
        super.onSuccess(respDatas);
        showToast(getResources().getString(R.string.toast_date_success));
        finish();
    }

    @Override
    public void onFail() {
        super.onFail();
        showToast(getResources().getString(R.string.toast_date_error));
    }
}
