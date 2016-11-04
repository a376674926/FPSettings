
package cn.stj.settings.activity;

import java.util.Arrays;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import cn.stj.settings.R;
import cn.stj.settings.adapter.SettingListAdapter;

/**
 * Display setting activity
 * 
 * @author jackey
 */
public class DisplaySettingActivity extends BaseActivity implements
        BaseActivity.BottomKeyClickListener {

    private static final String TAG = DisplaySettingActivity.class.getSimpleName();
    private SettingListAdapter mSettingListAdapter;
    private int mSelectedPosition;

    private static enum SETTING_DISPLAY {
        DISPLAY_BRIGHTNESS, SLEEP_TIME
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAboveViewStub.setLayoutResource(R.layout.activity_settings_listview);
        setBottomKeyClickListener(this);

        initView();
    }

    /**
     * 初始化系统设置界面
     */
    private void initView() {
        View listView = mAboveViewStub.inflate();
        String[] settingItems = getResources().getStringArray(R.array.setting_display);
        mSettingListAdapter = new SettingListAdapter(this, Arrays.asList(settingItems));
        final ListView lv = (ListView) listView.findViewById(R.id.main_list_view);
        lv.setAdapter(mSettingListAdapter);
        lv.setOnItemClickListener(new OptionMmsItemClickListener());
        lv.setOnItemSelectedListener(new ListItemSelectedListener());
    }

    private class OptionMmsItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            onMmsItemClick(position);
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

    private void onMmsItemClick(int position) {
        setDisplayOption(position);
    }

    public void setDisplayOption(int position) {
        if (position == SETTING_DISPLAY.DISPLAY_BRIGHTNESS.ordinal()) {
            setDisplayBrightness();
        } else if (position == SETTING_DISPLAY.SLEEP_TIME.ordinal()) {
            setSleepTime();
        }
    }

    private void setDisplayBrightness() {
        Bundle bundle = new Bundle();
        bundle.putString(BrightnessVolumeSettingActivity.SETTING_OPTIONS,
                BrightnessVolumeSettingActivity.BRIGHTNESS);
        launchActivity(BrightnessVolumeSettingActivity.class, bundle);
    }

    private void setSleepTime() {
        launchActivity(SleepTimeSettingActivity.class);
    }

    @Override
    public void onLeftKeyPress() {
        setDisplayOption(mSelectedPosition);
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
