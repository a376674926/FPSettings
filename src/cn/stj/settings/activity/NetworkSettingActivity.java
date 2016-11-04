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
 * network setting activity
 * @author jackey
 *
 */
public class NetworkSettingActivity extends BaseActivity implements BaseActivity.BottomKeyClickListener{
	
	private static final String TAG = NetworkSettingActivity.class.getSimpleName() ;
	private SettingListAdapter mSettingListAdapter ;
	private int mSelectedPosition ;
	private static enum SETTING_NETWORK{
		AIRPLANE_MODES, DATA_CONNECTION
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mAboveViewStub.setLayoutResource(R.layout.activity_settings_listview);
        setBottomKeyClickListener(this);
        
        initView() ;
	}

	private void initView() {
		View listView = mAboveViewStub.inflate();
		String[] settingItems = getResources().getStringArray(R.array.setting_network);
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
		setNetworkOption(position) ;
	}
	
	public void setNetworkOption(int position){
		if (position == SETTING_NETWORK.AIRPLANE_MODES.ordinal()) {
			setAirphoneMode() ;
		} else if (position == SETTING_NETWORK.DATA_CONNECTION.ordinal()) {
			setDataConnection();
		}
	}
	
	private void setAirphoneMode() {
		Bundle bundle = new Bundle() ;
		bundle.putString(OpenOffSettingActivity.SETTING_OPTIONS,OpenOffSettingActivity.AIRPHONE_MODE) ;
		launchActivity(OpenOffSettingActivity.class, bundle) ;
	}

	private void setDataConnection() {
		Bundle bundle = new Bundle() ;
		bundle.putString(OpenOffSettingActivity.SETTING_OPTIONS,OpenOffSettingActivity.DATA_CONNECTION) ;
		launchActivity(OpenOffSettingActivity.class, bundle) ;
	}

	@Override
	public void onLeftKeyPress() {
		setNetworkOption(mSelectedPosition) ;
	}

	@Override
	public void onMiddleKeyPress() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRightKeyPress() {
		launchActivity(SettingsMainActivity.class) ;
		finish() ;
	}

}
