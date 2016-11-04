
package cn.stj.settings.activity;

import java.util.Arrays;
import java.util.HashMap;

import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import cn.stj.settings.R;
import cn.stj.settings.adapter.OpenOffListAdapter;
import cn.stj.settings.adapter.SettingListAdapter;
import cn.stj.settings.presenter.SettingPresenter;
import cn.stj.settings.presenter.SettingPresenterImpl;
import cn.stj.settings.utils.DateTimeUtil;

/**
 * sleeptime setting activity
 * 
 * @author jackey
 */
public class SleepTimeSettingActivity extends BaseActivity implements
        BaseActivity.BottomKeyClickListener {

    private static final String TAG = SleepTimeSettingActivity.class.getSimpleName();
    private OpenOffListAdapter mSettingListAdapter;
    private int mSelectedPosition;
    // save the selected sleep time
    private static final String SELECTED_SLEEPTIME = "selected_sleeptime";
    private HashMap<String, Integer> mSleepTimeSelectedMap = new HashMap<String, Integer>();
    private SettingPresenter mSettingPresenter;
    private long[] sleepTime = new long[] {
            5 * DateTimeUtil.ONE_SECOND, 10 * DateTimeUtil.ONE_SECOND,
            15 * DateTimeUtil.ONE_SECOND,
            DateTimeUtil.ONE_MINUTE, 5 * DateTimeUtil.ONE_MINUTE, 30 * DateTimeUtil.ONE_MINUTE
    };
    private long mSelectedSleepTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAboveViewStub.setLayoutResource(R.layout.activity_settings_listview);
        setBottomKeyClickListener(this);

        initView();

        mSettingPresenter = new SettingPresenterImpl(this);

        updateSleepTimeListView();
    }

    /**
     * update sleep time list view
     */
    private void updateSleepTimeListView() {
        mSelectedSleepTime = getCurrentSleepTime();
        Log.d(TAG, "updateSleepTimeListView-->>currentSleepTime:" + mSelectedSleepTime);
        if (mSelectedSleepTime != 0) {
            for (int i = 0; i < sleepTime.length; i++) {
                if (mSelectedSleepTime == sleepTime[i]) {
                    mSettingListAdapter.updateOpenOffStatusMap(i, true);
                    mSleepTimeSelectedMap.put(SELECTED_SLEEPTIME, i);
                    mSettingListAdapter.setSelectedItemPosition(i);
                }
            }
        } else {
            mSettingPresenter.setSleepTime(this, sleepTime[0]);
        }
    }

    /**
     * 初始化系统设置界面
     */
    private void initView() {
        View listView = mAboveViewStub.inflate();
        String[] settingItems = getResources().getStringArray(R.array.setting_sleeptime);
        mSettingListAdapter = new OpenOffListAdapter(this, Arrays.asList(settingItems));
        final ListView lv = (ListView) listView.findViewById(R.id.main_list_view);
        lv.setAdapter(mSettingListAdapter);
        lv.setOnItemClickListener(new ListItemClickListener());
        lv.setOnItemSelectedListener(new ListItemSelectedListener());
    }

    private class ListItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mSelectedPosition = position;
            onSettingItemClick(position);
        }
    }

    private class ListItemSelectedListener implements OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position,
                long id) {
            mSelectedPosition = position;
            mSettingListAdapter.setSelectedItemPosition(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }

    }

    private void onSettingItemClick(int position) {
        if (mSleepTimeSelectedMap.get(SELECTED_SLEEPTIME) != null) {
            int selectedPosition = mSleepTimeSelectedMap.get(SELECTED_SLEEPTIME);
            if (selectedPosition != position) {
                mSettingListAdapter.getmOpenOffStatusMap().put(selectedPosition, false);
            }
        }
        mSettingListAdapter.getmOpenOffStatusMap().put(position, true);
        mSleepTimeSelectedMap.put(SELECTED_SLEEPTIME, position);
        mSettingListAdapter.setSelectedItemPosition(position);
    }

    @Override
    public void onLeftKeyPress() {
        onSettingItemClick(mSelectedPosition);
        mSelectedSleepTime = sleepTime[mSelectedPosition];
        confirm();
    }

    private void confirm() {
        setSleepTime(mSelectedSleepTime);
        finish();
    }

    private void setSleepTime(long sleepTime) {
        mSettingPresenter.setSleepTime(this, sleepTime);
    }

    @Override
    public void onMiddleKeyPress() {
        // TODO Auto-generated method stub

    }

    private int getCurrentSleepTime() {
        int currentSleepTime = 0;
        try {
            currentSleepTime = Settings.System.getInt(getContentResolver(),
                    Settings.System.SCREEN_OFF_TIMEOUT);
        } catch (SettingNotFoundException e) {
            e.printStackTrace();
        }
        return currentSleepTime;
    }

    @Override
    public void onRightKeyPress() {
        launchActivity(DisplaySettingActivity.class);
    }

}
