
package cn.stj.settings.activity;

import java.util.Arrays;

import cn.stj.settings.R;
import cn.stj.settings.adapter.SettingListAdapter;
import cn.stj.settings.presenter.SettingPresenter;
import cn.stj.settings.presenter.SettingPresenterImpl;
import cn.stj.settings.utils.NetworkUtil;
import android.app.LauncherActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * other setting activity
 * 
 * @author jackey
 */
public class OtherSettingActivity extends BaseActivity implements
        BaseActivity.BottomKeyClickListener {

    private static final String TAG = OtherSettingActivity.class.getSimpleName();
    private SettingListAdapter mSettingListAdapter;
    private int mSelectedPosition;
    private SettingPresenter mSettingPresenter;

    // modify begin by hhj@20160722 , for update function key function so that
    // do not cause crash exception
    private static enum SETTING_OTHER {
        MOBILE_LANGUAGE, MOBILE_RESET , MOBILE_FUNCTION_KEY
    }

    /*
     * private static enum SETTING_OTHER { MOBILE_LANGUAGE, MOBILE_RESET }
     */
    // modify end.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAboveViewStub.setLayoutResource(R.layout.activity_settings_listview);
        setBottomKeyClickListener(this);

        initView();

        mSettingPresenter = new SettingPresenterImpl(this);
    }

    /**
     * 鍒濆鍖栫郴缁熻缃晫闈�
     */
    private void initView() {
        View listView = mAboveViewStub.inflate();
        String[] settingItems = getResources().getStringArray(R.array.setting_other);
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
        setOtherOption(position);
    }

    public void setOtherOption(int position) {
        if (position == SETTING_OTHER.MOBILE_LANGUAGE.ordinal()) {
            setMobileLanguage();
        } else if (position == SETTING_OTHER.MOBILE_FUNCTION_KEY.ordinal()) { // delete
                                                                              // by
                                                                              // lzy@20160716.
            setMobileFunctionKey();
        } else if (position == SETTING_OTHER.MOBILE_RESET.ordinal()) {
            setMobileReset();
        }
    }

    private void setMobileReset() {
        launchActivity(ResetActivity.class);
    }

    private void setMobileFunctionKey() {
        launchActivity(FunctionKeySettingActivity.class);
    }

    private void setMobileLanguage() {
        launchActivity(LanguageSettingActivity.class);
    }

    @Override
    public void onLeftKeyPress() {
        setOtherOption(mSelectedPosition);
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
