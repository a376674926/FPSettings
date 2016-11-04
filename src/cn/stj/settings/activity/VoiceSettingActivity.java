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
 * Voice setting activity
 * @author jackey
 *
 */
public class VoiceSettingActivity extends BaseActivity implements BaseActivity.BottomKeyClickListener{
	
	private static final String TAG = VoiceSettingActivity.class.getSimpleName() ;
	private SettingListAdapter mSettingListAdapter ;
	private int mSelectedPosition ;
	private SettingPresenter mSettingPresenter ;
	private static enum SETTING_VOICE{
		VOICE_CALL, VOICE_TIME, VOICE_KEYS
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mAboveViewStub.setLayoutResource(R.layout.activity_settings_listview);
        setBottomKeyClickListener(this);
        
        initView() ;
        
        mSettingPresenter = new SettingPresenterImpl(this) ;
	}

	/**
	 * 初始化系统设置界面
	 */
	private void initView() {
		View listView = mAboveViewStub.inflate();
		String[] settingItems = getResources().getStringArray(R.array.setting_voice);
		mSettingListAdapter = new SettingListAdapter(this, Arrays.asList(settingItems)) ;
        final ListView lv = (ListView) listView.findViewById(R.id.main_list_view);
        lv.setAdapter(mSettingListAdapter);
        lv.setOnItemClickListener(new ListItemClickListener());
        lv.setOnItemSelectedListener(new ListItemSelectedListener()) ;
	}

	private class ListItemClickListener implements OnItemClickListener {

		@Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            onSettingItemClick(position);
        }
    }
	
	private class ListItemSelectedListener implements OnItemSelectedListener{

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position,
				long id) {
			mSelectedPosition = position ;
			mSettingListAdapter.setSelectedItemPosition(position) ;
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
		
	}
	
	private void onSettingItemClick(int position) {
		setVoiceOption(position) ;
	}
	
	public void setVoiceOption(int position){
		if (position == SETTING_VOICE.VOICE_CALL.ordinal()) {
			setVoiceCall() ;
		} else if (position == SETTING_VOICE.VOICE_TIME.ordinal()) {
			setVoiceTime() ;
		} else if (position == SETTING_VOICE.VOICE_KEYS.ordinal()) {
			setVoiceKeys() ;
		} 
	}

	private void setVoiceCall() {
		Bundle bundle = new Bundle() ;
		bundle.putString(OpenOffSettingActivity.SETTING_OPTIONS,OpenOffSettingActivity.VOICE_CALL) ;
		launchActivity(OpenOffSettingActivity.class, bundle) ;
	}
	
	private void setVoiceTime() {
		Bundle bundle = new Bundle() ;
		bundle.putString(OpenOffSettingActivity.SETTING_OPTIONS,OpenOffSettingActivity.VOICE_TIME) ;
		launchActivity(OpenOffSettingActivity.class, bundle) ;
	}
	
	private void setVoiceKeys() {
		Bundle bundle = new Bundle() ;
		bundle.putString(OpenOffSettingActivity.SETTING_OPTIONS,OpenOffSettingActivity.VOICE_KEYS) ;
		launchActivity(OpenOffSettingActivity.class, bundle) ;
	}

	@Override
	public void onLeftKeyPress() {
		setVoiceOption(mSelectedPosition) ;
	}

	@Override
	public void onMiddleKeyPress() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRightKeyPress() {
		launchActivity(SettingsMainActivity.class) ;
	}

}
