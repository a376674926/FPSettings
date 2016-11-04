
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
 * Time Setting Activity
 * 
 * @author jackey
 */
public class TimeSettingActivity extends BaseActivity implements
        BaseActivity.BottomKeyClickListener {

    private SettingPresenter mSettingPresenter;
    private EditText mHourEditText;
    private EditText mMinEditText;
    private Calendar mDummyDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAboveViewStub.setLayoutResource(R.layout.activity_settings_time);
        setBottomKeyClickListener(this);

        initView();

        mSettingPresenter = new SettingPresenterImpl(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        registerReceiver(mTimeReceiver, filter, null, null);
    }

    private void initView() {
        View dateView = mAboveViewStub.inflate();
        mHourEditText = (EditText) dateView.findViewById(R.id.setting_time_edit_hour);
        mMinEditText = (EditText) dateView.findViewById(R.id.setting_time_edit_min);

        mHourEditText.addTextChangedListener(new TimeTextWatcher());
        mMinEditText.addTextChangedListener(new TimeTextWatcher());
        mHourEditText.setOnFocusChangeListener(new TimeFocusChangeListener());
        mMinEditText.setOnFocusChangeListener(new TimeFocusChangeListener());
        mHourEditText.setSelection(mHourEditText.getText().toString().length());

        mDummyDate = Calendar.getInstance();

        updateTimeAndDateDisplay(this);
        setRightBtnText(getResources().getString(R.string.delete));
    }

    private void updateTimeAndDateDisplay(TimeSettingActivity timeSettingActivity) {
        final Calendar now = Calendar.getInstance();
        mDummyDate.setTimeZone(now.getTimeZone());
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int min = now.get(Calendar.MINUTE);
        mHourEditText.setText(hour + "");
        mMinEditText.setText(min + "");
        mHourEditText.setSelection(mHourEditText.getText().toString().length());
    }

    private class TimeTextWatcher implements TextWatcher {

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

    private class TimeFocusChangeListener implements OnFocusChangeListener {
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
        String hour = mHourEditText.getText().toString();
        String min = mMinEditText.getText().toString();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        if (!TextUtils.isEmpty(hour)) {
            calendar.set(Calendar.HOUR_OF_DAY,
                    Integer.valueOf(hour));
        }
        if (!TextUtils.isEmpty(min)) {
            calendar.set(Calendar.MINUTE,
                    Integer.valueOf(min));
        }
        setTime(calendar.getTimeInMillis());
    }

    private void setTime(long time) {
        mSettingPresenter.setDateTime(this, time);
        updateTimeAndDateDisplay(this);
    }

    @Override
    public void onMiddleKeyPress() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onRightKeyPress() {
        if (mHourEditText.isFocused()) {
            deleteEditTextInfo(mHourEditText);
        } else if (mMinEditText.isFocused()) {
            deleteEditTextInfo(mMinEditText);
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

    private BroadcastReceiver mTimeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateTimeAndDateDisplay(TimeSettingActivity.this);
        }
    };

    @Override
    public void onSuccess(HashMap<String, Object> respDatas) {
        super.onSuccess(respDatas);
        showToast(getResources().getString(R.string.toast_time_success));
        finish();
    }

    @Override
    public void onFail() {
        super.onFail();
        showToast(getResources().getString(R.string.toast_time_error));
    }
}
