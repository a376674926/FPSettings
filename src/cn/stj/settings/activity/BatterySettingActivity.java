
package cn.stj.settings.activity;

import java.util.Arrays;

import cn.stj.settings.R;
import cn.stj.settings.adapter.SettingListAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * battery setting activity
 * 
 * @author jackey
 */
public class BatterySettingActivity extends BaseActivity implements
        BaseActivity.BottomKeyClickListener {

    private static final String TAG = BatterySettingActivity.class.getSimpleName();
    private SettingListAdapter mSettingListAdapter;
    private int mSelectedPosition;

    private static enum SETTING_BATTERY {
        BATTERY_POWERSAVING, BATTERY_NUMBER
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAboveViewStub.setLayoutResource(R.layout.activity_settings_listview);
        setBottomKeyClickListener(this);

        initView();
    }

    private void initView() {
        View listView = mAboveViewStub.inflate();
        String[] settingItems = getResources().getStringArray(R.array.setting_battery);
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
            onMmsItemClick(position);
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

    private void onMmsItemClick(int position) {
        setBatteryOption(position);
    }

    public void setBatteryOption(int position) {
        if (position == SETTING_BATTERY.BATTERY_POWERSAVING.ordinal()) {
            setBatterySaving();
        } else if (position == SETTING_BATTERY.BATTERY_NUMBER.ordinal()) {
            setBatteryNumber();
        }
    }

    private void setBatterySaving() {
        Bundle bundle = new Bundle();
        bundle.putString(OpenOffSettingActivity.SETTING_OPTIONS, OpenOffSettingActivity.POWER_SAVE);
        launchActivity(OpenOffSettingActivity.class, bundle);
    }

    private void setBatteryNumber() {
        Bundle bundle = new Bundle();
        bundle.putString(OpenOffSettingActivity.SETTING_OPTIONS,
                OpenOffSettingActivity.BATTERY_NUMBER);
        launchActivity(OpenOffSettingActivity.class, bundle);
    }

    @Override
    public void onLeftKeyPress() {
        setBatteryOption(mSelectedPosition);
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
