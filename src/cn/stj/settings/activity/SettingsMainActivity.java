
package cn.stj.settings.activity;

import java.util.Arrays;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
 * settings main activity
 * 
 * @author jackey
 */
public class SettingsMainActivity extends BaseActivity implements
        BaseActivity.BottomKeyClickListener {

    private static final String TAG = SettingsMainActivity.class
            .getSimpleName();
    private static final String SETTING_EXITACTION = "setting.action.exit";
    // add begin by lzy@20160716, for add wifi/bt/gps option.
    private static final String SETTING_WIFI_ACTION = "android.settings.WIFI_SETTINGS";
    private static final String SETTING_BLUETHOOTH_ACTION = "android.settings.BLUETOOTH_SETTINGS";
    private static final String SETTING_LOCATION_ACTION = "android.settings.LOCATION_SOURCE_SETTINGS";
    private static final String SETTING_PROFILE_ACTION = "com.sprd.action.AUDIO_PROFILE";
    // add end.

    /*private static enum SETTING_MAIN_FUNCTION {
        NETWORK, SOS, BATTERY, DISPLAY, SOUND, DATE, VOICE, MOBILE
    }*/

    // modify begin by lzy@20160716, for add wifi/bt/gps option.
    /*
    private static enum SETTING_MAIN_FUNCTION {
        NETWORK, SIM, DISPLAY, SOUND, DATE, VOICE, MOBILE
    }
    */
    private static enum SETTING_MAIN_FUNCTION {
        WIFI, NETWORK, SIM, BLUETOOTH, LOCATION, DISPLAY, PROFILES, DATE, /*VOICE,*/ MOBILE
    }
    // modify end.

    private SettingListAdapter mSettingListAdapter;
    private int mSelectedPosition;
    // 閫�鍑哄箍鎾帴鏀惰��
    private ExitReceiver mExitReceiver = new ExitReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAboveViewStub.setLayoutResource(R.layout.activity_settings_listview);
        setBottomKeyClickListener(this);

        initView();
        registerBroadcastReceiver();
    }

    /**
     * 娉ㄥ唽閫�鍑哄箍鎾帴鏀惰��
     */
    private void registerBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter(SETTING_EXITACTION);
        registerReceiver(mExitReceiver, intentFilter);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

    }

    /**
     * 鍒濆鍖栫郴缁熻缃晫闈�
     */
    private void initView() {
        View listView = mAboveViewStub.inflate();
        String[] settingItems = getResources().getStringArray(
                R.array.setting_templates);
        mSettingListAdapter = new SettingListAdapter(this,
                Arrays.asList(settingItems));
        final ListView lv = (ListView) listView
                .findViewById(R.id.main_list_view);
        lv.setAdapter(mSettingListAdapter);
        lv.setOnItemClickListener(new ListItemClickListener());
        lv.setOnItemSelectedListener(new ListItemSelectedListener());
    }

    private class ListItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            onSettingItemClick(position);
        }
    }

    private class ListItemSelectedListener implements OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                int position, long id) {
            mSelectedPosition = position;
            mSettingListAdapter.setSelectedItemPosition(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }

    }

    private void onSettingItemClick(int position) {
        if (position == SETTING_MAIN_FUNCTION.NETWORK.ordinal()) {
            setNetwork();
        } else if (position == SETTING_MAIN_FUNCTION.SIM.ordinal()) {
            setSIM();//add by hhj@20160712 
        } else if (position == SETTING_MAIN_FUNCTION.WIFI.ordinal()) {
            setWifi();
        } else if (position == SETTING_MAIN_FUNCTION.BLUETOOTH.ordinal()) {
            setBlueTooth();
        } else if (position == SETTING_MAIN_FUNCTION.LOCATION.ordinal()) {
            setLocation();
        }/*else if (position == SETTING_MAIN_FUNCTION.SOS.ordinal()) {
            setSOS();
        } else if (position == SETTING_MAIN_FUNCTION.BATTERY.ordinal()) {
            setBattery();
        } */else if (position == SETTING_MAIN_FUNCTION.DISPLAY.ordinal()) {
            setDisplay();
        } else if (position == SETTING_MAIN_FUNCTION.PROFILES.ordinal()) {
            setProfiles();
        } else if (position == SETTING_MAIN_FUNCTION.DATE.ordinal()) {
            setDate();
        }/* else if (position == SETTING_MAIN_FUNCTION.VOICE.ordinal()) {
            setVoice();
        } */else if (position == SETTING_MAIN_FUNCTION.MOBILE.ordinal()) {
            setMobile();
        }
    }

    /**
     * network setting
     */
    private void setNetwork() {
        launchActivity(NetworkSettingActivity.class);
    }

    //add begin by hhj@20160712 for SIM setting
    private void setSIM() {
        launchActivity(DualCardManageActivity.class);
    }
    //add end
    
    private void setSOS() {
        launchActivity(SOSSettingActivity.class);
    }

    private void setBattery() {
        launchActivity(BatterySettingActivity.class);
    }

    private void setDisplay() {
        launchActivity(DisplaySettingActivity.class);
    }

    private void setSound() {
        launchActivity(SoundSettingActivity.class);
    }

    private void setDate() {
        launchActivity(DateTimeSettingActivity.class);
    }

    private void setVoice() {
        launchActivity(VoiceSettingActivity.class);
    }

    private void setMobile() {
        launchActivity(OtherSettingActivity.class);
    }

    @Override
    public void onLeftKeyPress() {
        onSettingItemClick(mSelectedPosition);
    }

    @Override
    public void onMiddleKeyPress() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onRightKeyPress() {
        launchActivity(SettingsMainActivity.class);
        sendBroadcast(new Intent(SETTING_EXITACTION));
    }

    class ExitReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            SettingsMainActivity.this.finish();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mExitReceiver);
    }

    // add begin by lzy@20160718, for add wifi/bt/gps option.
    private void setWifi() {
        Intent intent = new Intent();
        intent.setAction(SETTING_WIFI_ACTION);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        startActivity(intent);
    }

    private void setBlueTooth() {
        Intent intent = new Intent();
        intent.setAction(SETTING_BLUETHOOTH_ACTION);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        startActivity(intent);
    }

    private void setLocation() {
        Intent intent = new Intent();
        intent.setAction(SETTING_LOCATION_ACTION);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        startActivity(intent);
    }

    private void setProfiles() {
        Intent intent = new Intent();
        intent.setAction(SETTING_PROFILE_ACTION);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        startActivity(intent);
    }
    // add end.

}
