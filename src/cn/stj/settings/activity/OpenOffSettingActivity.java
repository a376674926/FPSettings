package cn.stj.settings.activity;

import java.util.Arrays;
import java.util.HashMap;

import cn.stj.settings.Constant;
import cn.stj.settings.R;
import cn.stj.settings.adapter.OpenOffListAdapter;
import cn.stj.settings.adapter.SettingListAdapter;
import cn.stj.settings.presenter.SettingPresenter;
import cn.stj.settings.presenter.SettingPresenterImpl;
import cn.stj.settings.utils.DateTimeUtil;
import cn.stj.settings.utils.NetworkUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.renderscript.Sampler.Value;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * open and off setting activity
 * @author jackey
 */
public class OpenOffSettingActivity extends BaseActivity implements BaseActivity.BottomKeyClickListener{
	
	private static final String TAG = OpenOffSettingActivity.class.getSimpleName() ;
	public static final String SETTING_OPTIONS = "setting_options" ;
	public static final String AIRPHONE_MODE = "airphone_mode" ;
	public static final String DATA_CONNECTION = "data_connection" ;
	public static final String SOS_SETTING = "sos_setting" ;
	public static final String POWER_SAVE = "power_save" ;
	public static final String BATTERY_NUMBER = "battery_number" ;
	public static final String AUTO_TIME = "auto_time" ;
	public static final String HOURS_FORMAT = "hours_format" ;
	public static final String VOICE_CALL = "voice_call" ;
	public static final String VOICE_TIME = "voice_time" ;
	public static final String VOICE_KEYS = "voice_keys" ;
	public static final int OPEN_POSITION = 0 ;
	public static final int CLOSE_POSITION = 1 ;
    private static final boolean DEBUG = true;
	private OpenOffListAdapter mOpenOffListAdapter ;
	private SettingOptions settingOption ;
	private int mSelectedPosition ;
	private SettingPresenter mSettingPresenter ;
	private boolean isOpen ;
    private ListView mListView ;
    private AirplaneModeReceiver mAirplaneModeReceiver ;
    private DataConnectionReceiver mDataConnectionReceiver ;
    private BatteryPercentageReceiver mBatteryPercentageReceiver ;
	private enum SettingOptions{
		AIRPHONE_MODE, DATA_CONNECTION, SOS_SETTING, POWER_SAVE, BATTERY_NUMBER,
		AUTO_TIME,HOURS_FORMAT,VOICE_CALL,VOICE_TIME,VOICE_KEYS
    } 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mAboveViewStub.setLayoutResource(R.layout.activity_settings_listview);
        setBottomKeyClickListener(this);
        
        initView() ;
        
        handleIntent(getIntent()) ;
        
        mSettingPresenter = new SettingPresenterImpl(this) ;
	}

	/**
	 * handle intent
	 */
	private void handleIntent(Intent intent) {
		if(intent != null && intent.hasExtra(SETTING_OPTIONS)){
			String setting_options = intent.getStringExtra(SETTING_OPTIONS) ;
			String topTitleText = null ;
			if(AIRPHONE_MODE.equals(setting_options)){
				settingOption = SettingOptions.AIRPHONE_MODE ;
				topTitleText = getResources().getString(R.string.airplane_mode) ;
				onAirplaneModeChanged() ;
				registerAirplaneModeDone() ;
			}else if(DATA_CONNECTION.equals(setting_options)){
				settingOption = SettingOptions.DATA_CONNECTION ;
				topTitleText = getResources().getString(R.string.data_connection) ;
				onDataConnectionChanged() ;
                registerDataConnection() ;
			}else if(SOS_SETTING.equals(setting_options)){
				settingOption = SettingOptions.SOS_SETTING ;
				topTitleText = getResources().getString(R.string.sos_setting) ;
			}else if(POWER_SAVE.equals(setting_options)){
				settingOption = SettingOptions.POWER_SAVE ;
				topTitleText = getResources().getString(R.string.battery_save) ;
			}else if(BATTERY_NUMBER.equals(setting_options)){
				settingOption = SettingOptions.BATTERY_NUMBER ;
				topTitleText = getResources().getString(R.string.battery_number) ;
				onBatteryPercentageChanged() ;
				registerBatteryPercentage() ;
			}else if(AUTO_TIME.equals(setting_options)){
				settingOption = SettingOptions.AUTO_TIME ;
				topTitleText = getResources().getString(R.string.settings_auto_time) ;
				onAutotimeStateChanged() ;
			}else if(HOURS_FORMAT.equals(setting_options)){
				settingOption = SettingOptions.HOURS_FORMAT ;
				topTitleText = getResources().getString(R.string.settings_hours_format) ;
				onIs24HourChanged() ;
			}else if(VOICE_CALL.equals(setting_options)){
				settingOption = SettingOptions.VOICE_CALL ;
				topTitleText = getResources().getString(R.string.voice_call) ;
				onVoiceCallChanged() ;
			}else if(VOICE_TIME.equals(setting_options)){
				settingOption = SettingOptions.VOICE_TIME ;
				topTitleText = getResources().getString(R.string.voice_time) ;
				onVoiceTimeChanged() ;
			}else if(VOICE_KEYS.equals(setting_options)){
				settingOption = SettingOptions.VOICE_KEYS ;
				topTitleText = getResources().getString(R.string.voice_keys) ;
				onVoiceKeyChanged() ;
			}
			setTopTitleText(topTitleText) ;
		}
	}

	private void initView() {
		View listView = mAboveViewStub.inflate();
		String[] settingItems = getResources().getStringArray(R.array.setting_openoff);
		mOpenOffListAdapter = new OpenOffListAdapter(this, Arrays.asList(settingItems)) ;
        if(isOpen){
        	mOpenOffListAdapter.updateOpenOffStatusMap(OPEN_POSITION, true) ;
        }else{
        	mOpenOffListAdapter.updateOpenOffStatusMap(CLOSE_POSITION, true) ;
        }
        mListView = (ListView) listView.findViewById(R.id.main_list_view);
        mListView.setAdapter(mOpenOffListAdapter);
        mListView.setOnItemClickListener(new ListItemClickListener());
        mListView.setOnItemSelectedListener(new ListItemSelectedListener()) ;
	}

	private class ListItemClickListener implements OnItemClickListener {

		@Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        	mSelectedPosition = position ;
            onSettingItemClick(position);
        }
    }
	
	private class ListItemSelectedListener implements OnItemSelectedListener{

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position,
				long id) {
			mSelectedPosition = position ;
			mOpenOffListAdapter.setSelectedItemPosition(position) ;
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
		
	}
	
	private void onSettingItemClick(int position) {
		mOpenOffListAdapter.updateOpenOffStatusMap(position, true) ;
		isOpen = position == OPEN_POSITION ? true:false ;
		mOpenOffListAdapter.notifyDataSetChanged() ;
	}
	
	@Override
	public void onLeftKeyPress() {
	    onSettingItemClick(mSelectedPosition) ;
		confirm() ;
	}

	/**
	 * confirm 
	 */
	private void confirm() {
		switch (settingOption) {
		case AIRPHONE_MODE:
			setAirplaneMode(isOpen) ;
			break ;
        case DATA_CONNECTION:
        	setDataConnection(isOpen) ;
			break;
        case SOS_SETTING:
        	setSosSetting() ;
        	break ;
        case POWER_SAVE:
        	setPowerSave() ;
        	break ;
        case BATTERY_NUMBER:
			setBatteryNumber() ;
			break ;
        case AUTO_TIME:
			setAutoTime(isOpen) ;
			break ;
        case HOURS_FORMAT:
			setHourFormat() ;
			break ;
        case VOICE_CALL:
			setVoiceCall() ;
			break ;
        case VOICE_TIME:
			setVoiceTime() ;
			break ;
        case VOICE_KEYS:
			setVoiceKeys() ;
			break ;
		default:
			break;
		}
		finish();
	}

	private void setDataConnection(boolean isOpen) {
		mSettingPresenter.setDataConnection(this,isOpen) ;
	}

	private void setAirplaneMode(boolean isOpen) {
		mSettingPresenter.setAirplaneMode(this,isOpen) ;
	}

	private void setSosSetting() {
		showToast("设置SOS开关") ;
	}
	
	private void setPowerSave() {
		showToast("设置一键省电开关") ;
	}
	
	private void setBatteryNumber() {
		showToast("设置电池百分比开关:isOpen" + isOpen) ;
		mSettingPresenter.setBatteryPercentage(this,isOpen) ;
	}

	private void setAutoTime(boolean isOpen) {
	   mSettingPresenter.setAutotimeState(this, isOpen) ;
	}
	
	private void setHourFormat() {
	    mSettingPresenter.setHourFormat(this, isOpen) ;
	    onIs24HourChanged() ;
	    timeUpdated();
	}
	
	private void setVoiceCall() {
		mSettingPresenter.setCallVoiceBroadcast(this, isOpen) ;
	}
	
	private void setVoiceTime() {
	    mSettingPresenter.setTimeVoiceBroadcast(this, isOpen) ;
	}
	
	private void setVoiceKeys() {
	    mSettingPresenter.setKeyVoiceBroadcast(this, isOpen) ;
	}
	
	@Override
	public void onMiddleKeyPress() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRightKeyPress() {
		switch (settingOption) {
		case AIRPHONE_MODE:
        case DATA_CONNECTION:
			launchActivity(NetworkSettingActivity.class) ;
			break;
        case SOS_SETTING:
        	launchActivity(SOSSettingActivity.class) ;
        	break ;
        case POWER_SAVE:
        case BATTERY_NUMBER:
			launchActivity(BatterySettingActivity.class) ;
			break ;
        case AUTO_TIME:
        case HOURS_FORMAT:
			launchActivity(DateTimeSettingActivity.class) ;
			break ;
        case VOICE_CALL:
        case VOICE_TIME:
        case VOICE_KEYS:
			launchActivity(VoiceSettingActivity.class) ;
			break ;
		default:
			break;
		}
	}
	
	/**
	 * 设置air成功
	 */
    public void onSuccess(HashMap<String, Object> respDatas){
    	switch (settingOption) {
		case AIRPHONE_MODE:
			break ;
        case DATA_CONNECTION:
			break;
        case SOS_SETTING:
        	break ;
        case POWER_SAVE:
        	break ;
        case BATTERY_NUMBER:
			break ;
		default:
			break;
		}
    };
    
    /**
     * 是否开启飞行模式
     * @param context
     * @return
     */
    public static boolean isAirplaneModeOn(Context context) {
        return Settings.Global.getInt(context.getContentResolver(),
                Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
    }
    
    /**
     * 注册设置飞行模式改变的广播接收者
     */
    private void registerAirplaneModeDone() {
        mAirplaneModeReceiver = new AirplaneModeReceiver() ;
		IntentFilter filter = new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED);
//        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED_DONE);
        this.registerReceiver(mAirplaneModeReceiver, filter);
	}
    
    /**
     * Called when we've received confirmation that the airplane mode was set.
     */
    private void onAirplaneModeChanged() {
        isOpen = isAirplaneModeOn(this);
        updateOpenOffList(isOpen) ;
    }
    
    private class AirplaneModeReceiver extends BroadcastReceiver{
        public void onReceive(Context context, Intent intent) {
        	onAirplaneModeChanged() ;
        }
    };
    
    /**
     * Called when we've received confirmation that the data connection was set
     */
    private void onDataConnectionChanged() {
        isOpen = NetworkUtil.checkMobileNetwork(this);
        updateOpenOffList(isOpen) ;
    }
    
    /**
     * 注册设置数据连接改变的广播接收者
     */
    private void registerDataConnection() {
        mDataConnectionReceiver = new DataConnectionReceiver() ;
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mDataConnectionReceiver, filter);
    }
    
    private class DataConnectionReceiver extends BroadcastReceiver{
        public void onReceive(Context context, Intent intent) {
            onDataConnectionChanged() ;
        }
    };
    
    /**
     * 注册设置电池状态改变的广播接收者
     */
    private void registerBatteryPercentage() {
        mBatteryPercentageReceiver = new BatteryPercentageReceiver() ;
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        this.registerReceiver(mBatteryPercentageReceiver, filter);
    }
    
    /**
     * 是否显示电池百分比
     * @param context
     * @return
     */
    public static boolean isShowBatteryPercentage(Context context) {
        boolean isShowPrecentage = Settings.Global.getInt(context.getContentResolver(),
                "battery_percentage_enabled", 0) == 1;
        Log.d(TAG, "isShowBatteryPercentage isShowPrecentage:" + isShowPrecentage) ;
        return isShowPrecentage ;

    }
    
    /**
     *电池百分比改变 
     */
    private void onBatteryPercentageChanged() {
        isOpen = isShowBatteryPercentage(this) ;
        updateOpenOffList(isOpen) ;
    }
    
    private class BatteryPercentageReceiver extends BroadcastReceiver{
        public void onReceive(Context context, Intent intent) {
            onBatteryPercentageChanged() ;
        }
    };
    
    private void onAutotimeStateChanged() {
        boolean autoTimeEnabled = getAutoState(Settings.Global.AUTO_TIME);
        isOpen = autoTimeEnabled ;
        updateOpenOffList(isOpen) ;
    }
    
    private boolean getAutoState(String name) {
        try {
            return Settings.Global.getInt(getContentResolver(), name) > 0;
        } catch (SettingNotFoundException snfe) {
            return false;
        }
    }
    
    private void onIs24HourChanged() {
        boolean is24HourEnabled = is24Hour() ;
        isOpen = is24HourEnabled ;
        updateOpenOffList(isOpen) ;
    }
    
    private boolean is24Hour() {
        return DateFormat.is24HourFormat(this);
    }
    
    private void timeUpdated() {
        Intent timeChanged = new Intent(Intent.ACTION_TIME_CHANGED);
        this.sendBroadcast(timeChanged);
    }
    
    private void onVoiceCallChanged() {
        isOpen = getVoiceBroadcastState(Constant.Settings.CALL_VOICE_BROADCAST) ;
        updateOpenOffList(isOpen) ;
    }
    
    private void onVoiceTimeChanged() {
        isOpen = getVoiceBroadcastState(Constant.Settings.TIME_VOICE_BROADCAST) ;
        updateOpenOffList(isOpen) ;
    }
    
    private void onVoiceKeyChanged() {
        isOpen = getVoiceBroadcastState(Constant.Settings.KEY_VOICE_BROADCAST) ;
        updateOpenOffList(isOpen) ;
    }
    
    private boolean getVoiceBroadcastState(String name) {
        try {
            return Settings.System.getInt(getContentResolver(), name) > 0;
        } catch (SettingNotFoundException snfe) {
            return false;
        }
    }

    public void updateOpenOffList(boolean isOpen){
        if(isOpen){
            mOpenOffListAdapter.updateOpenOffStatusMap(OPEN_POSITION, true) ;
        }else{
            mOpenOffListAdapter.updateOpenOffStatusMap(CLOSE_POSITION, true) ;
        }
        mOpenOffListAdapter.notifyDataSetChanged() ;
    }
    
    protected void onPause() {
    	super.onPause() ;
    };
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mAirplaneModeReceiver != null){
            unregisterReceiver(mAirplaneModeReceiver);
        }
        if(mDataConnectionReceiver != null){
            unregisterReceiver(mDataConnectionReceiver);
        }
        if(mBatteryPercentageReceiver != null){
            unregisterReceiver(mBatteryPercentageReceiver) ;
        }
    }

}
