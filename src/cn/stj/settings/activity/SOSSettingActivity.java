
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
 * SOS setting activity
 * 
 * @author jackey
 */
public class SOSSettingActivity extends BaseActivity implements BaseActivity.BottomKeyClickListener {

    private static final String TAG = SOSSettingActivity.class.getSimpleName();
    private SettingListAdapter mSettingListAdapter;
    private int mSelectedPosition;
    private SettingPresenter mSettingPresenter;

    private static enum SETTING_SOS {
        SOS_HELP, SOS_SETTING
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
        String[] settingItems = getResources().getStringArray(R.array.setting_sos);
        mSettingListAdapter = new SettingListAdapter(this, Arrays.asList(settingItems));
        final ListView lv = (ListView) listView.findViewById(R.id.main_list_view);
        lv.setAdapter(mSettingListAdapter);
        lv.setOnItemClickListener(new ListItemClickListener());
        lv.setOnItemSelectedListener(new ListItemSelectedListener());
    }

    private class ListItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
        setSOSOption(position);
    }

    public void setSOSOption(int position) {
        if (position == SETTING_SOS.SOS_HELP.ordinal()) {
            setSOSHelp();
        } else if (position == SETTING_SOS.SOS_SETTING.ordinal()) {
            setSOSSetting();
        }
    }

    private void setSOSHelp() {
        if (NetworkUtil.checkNetwork(this)) {
            mSettingPresenter.setSOSHelp();
        } else {
            showToast(getResources().getString(R.string.toast_sos_nonet), Gravity.CENTER);
        }
    }

    private void setSOSSetting() {
        Bundle bundle = new Bundle();
        bundle.putString(OpenOffSettingActivity.SETTING_OPTIONS, OpenOffSettingActivity.SOS_SETTING);
        launchActivity(OpenOffSettingActivity.class, bundle);
    }

    @Override
    public void onLeftKeyPress() {
        setSOSOption(mSelectedPosition);
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
