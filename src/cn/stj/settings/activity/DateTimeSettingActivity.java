
package cn.stj.settings.activity;

import java.util.Arrays;

import cn.stj.settings.R;
import cn.stj.settings.adapter.SettingListAdapter;
import cn.stj.settings.presenter.SettingPresenter;
import cn.stj.settings.presenter.SettingPresenterImpl;
import cn.stj.settings.utils.NetworkUtil;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * date time setting list activity
 * 
 * @author jackey
 */
public class DateTimeSettingActivity extends BaseActivity implements
        BaseActivity.BottomKeyClickListener {

    private static final String TAG = DateTimeSettingActivity.class.getSimpleName();
    private SettingListAdapter mSettingListAdapter;
    private int mSelectedPosition;
    private SettingPresenter mSettingPresenter;

    private static enum SETTING_DATE {
        AUTO_TIME, DATE, TIME, HOURS_FORMAT
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAboveViewStub.setLayoutResource(R.layout.activity_settings_listview);
        setBottomKeyClickListener(this);

        initView();

        mSettingPresenter = new SettingPresenterImpl(this);
    }

    /**
     * 初始化系统设置界面
     */
    private void initView() {
        View listView = mAboveViewStub.inflate();
        String[] settingItems = getResources().getStringArray(R.array.setting_date);
        mSettingListAdapter = new SettingListAdapter(this, Arrays.asList(settingItems));
        final ListView lv = (ListView) listView.findViewById(R.id.main_list_view);
        lv.setAdapter(mSettingListAdapter);
        lv.setOnItemClickListener(new OptionMmsItemClickListener());
        lv.setOnItemSelectedListener(new ListItemSelectedListener());
    }

    private class OptionMmsItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d(TAG, " onItemClick-->>position:" + position);
            onDateItemClick(position);
        }
    }

    private class ListItemSelectedListener implements OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position,
                long id) {
            Log.d(TAG, " onItemSelected-->>mSelectedPosition:" + position);
            mSelectedPosition = position;
            mSettingListAdapter.setSelectedItemPosition(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }

    }

    private void onDateItemClick(int position) {
        setDateOption(position);
    }

    public void setDateOption(int position) {
        if (position == SETTING_DATE.AUTO_TIME.ordinal()) {
            setAutoTime();
        } else if (position == SETTING_DATE.DATE.ordinal()) {
            setDate();
        } else if (position == SETTING_DATE.TIME.ordinal()) {
            setTime();
        } else if (position == SETTING_DATE.HOURS_FORMAT.ordinal()) {
            setHourFormat();
        }
    }

    private void setHourFormat() {
        Bundle bundle = new Bundle();
        bundle.putString(OpenOffSettingActivity.SETTING_OPTIONS,
                OpenOffSettingActivity.HOURS_FORMAT);
        launchActivity(OpenOffSettingActivity.class, bundle);
    }

    private void setTime() {
        launchActivity(TimeSettingActivity.class);
    }

    private void setDate() {
        launchActivity(DateSettingActivity.class);
    }

    private void setAutoTime() {
        Bundle bundle = new Bundle();
        bundle.putString(OpenOffSettingActivity.SETTING_OPTIONS, OpenOffSettingActivity.AUTO_TIME);
        launchActivity(OpenOffSettingActivity.class, bundle);
    }

    @Override
    public void onLeftKeyPress() {
        setDateOption(mSelectedPosition);
    }

    @Override
    public void onMiddleKeyPress() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onRightKeyPress() {
        launchActivity(SettingsMainActivity.class);
    }

}
