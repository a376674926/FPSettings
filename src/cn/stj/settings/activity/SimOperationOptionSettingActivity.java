package cn.stj.settings.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import cn.stj.settings.R;
import cn.stj.settings.adapter.SettingListAdapter;
import cn.stj.settings.utils.PreferencesUtils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.preference.Preference;
import android.provider.Settings;
import android.telecom.PhoneAccount;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;

import com.android.internal.telephony.TelephonyProperties;

/**
 * Sim operation options setting activity
 * @author jackey
 * add by hhj@20160712 for sim operation options
 */
public class SimOperationOptionSettingActivity extends BaseActivity implements BaseActivity.BottomKeyClickListener{
	
	private static final String TAG = SimOperationOptionSettingActivity.class.getSimpleName() ;
    protected static final boolean DEBUG = false;
    private static final String SUBID = "subId";
    private static final String ISDATAENABLE = "isdataenable" ;
	private SettingListAdapter mSettingListAdapter ;
	private int mSelectedPosition ;
	private View mCellularDataLayout;
	private TextView mCellularDataCarrierTextView;
	private TextView mCellularDataTextView;
	private View mPhoneCallLayout;
    private TextView mPhoneCallTextView;
    private TextView mPhoneCallCarrierTextView;
    private View mSmsLayout;
    private TextView mSmsTextView;
    private TextView mSmsCarrierTextView;
    private View mSimOperationView;
    private SubscriptionManager mSubscriptionManager;
    private TelephonyManager mTelephonyManager = null;
    private int mNumSlots;
    private List<SubscriptionInfo> mAvailableSubInfoList = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mAboveViewStub.setLayoutResource(R.layout.activity_operation_option_setting);
        setBottomKeyClickListener(this);
        
        mSubscriptionManager = SubscriptionManager.from(this);
        mTelephonyManager = TelephonyManager.from(this);
        final TelephonyManager tm =
                (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        mNumSlots = tm.getSimCount();
        
        mAvailableSubInfoList = getActiveSubInfoList();
        
        initView() ;
	}

	@Override
	protected void onResume() {
	    super.onResume();
	    updateSimOptionsState();
	    getContentResolver().registerContentObserver(
                Settings.Global.getUriFor(Settings.Global.MOBILE_DATA), true,
                mMobileDataObserver,UserHandle.USER_OWNER);   // SPRD:  modify for bug 508104
	    getContentResolver().registerContentObserver(
                Settings.Global.getUriFor(Settings.Global.RADIO_OPERATION), true,
                mRadioBusyObserver, UserHandle.USER_OWNER); 
	    final TelephonyManager tm =
                (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        tm.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        mSubscriptionManager.addOnSubscriptionsChangedListener(mOnSubscriptionsChangeListener);
//        updateAllOptions();
        
	}
	
	private void updateAllOptions() {
	    updateCellularDataValues();
	    updateCallValues();
	    updateSmsValues();
	}

    private void updateCellularDataValues() {
        final  SubscriptionInfo sir = mSubscriptionManager.getDefaultDataSubscriptionInfo();
        if (sir != null) {
            boolean isSimStandby = Settings.Global.getInt(this.getContentResolver(),
                    Settings.Global.SIM_STANDBY + sir.getSimSlotIndex(), 1) == 1;
            mCellularDataCarrierTextView.setText(sir.getDisplayName().toString().trim().isEmpty()?
                    "SIM"+(sir.getSimSlotIndex()+1):sir.getDisplayName());
        } else if (sir == null) {
            mCellularDataCarrierTextView.setText(R.string.sim_off);
        }
        if (mSubscriptionManager.getActiveSubscriptionInfoCount() < 1) {
            mCellularDataLayout.setEnabled(false);
            mCellularDataTextView.setEnabled(false);
        } else {
            //SPRD: modify for bug497338
            mCellularDataLayout.setEnabled(mAvailableSubInfoList.size() > 0);
            mCellularDataTextView.setEnabled(mAvailableSubInfoList.size() > 0);
        }

    }
    
    private void updateCallValues() {
        final TelecomManager telecomManager = TelecomManager.from(this);
        final PhoneAccountHandle phoneAccount =
            telecomManager.getUserSelectedOutgoingPhoneAccount();
        final List<PhoneAccountHandle> allPhoneAccounts =
            telecomManager.getCallCapablePhoneAccounts();
        PhoneAccount pa = telecomManager.getPhoneAccount(phoneAccount);
        final boolean isPhoneAccountAvialable = (phoneAccount != null) && (pa != null);
        mPhoneCallCarrierTextView.setText(!isPhoneAccountAvialable
                ? this.getResources().getString(R.string.ask_first)
                : (String)pa.getLabel());

        mPhoneCallTextView.setEnabled(mAvailableSubInfoList.size() > 1);
        mPhoneCallLayout.setEnabled(mAvailableSubInfoList.size() > 1);
    }
    
    private void updateSmsValues() {
        final SubscriptionInfo sir = mSubscriptionManager.getDefaultSmsSubscriptionInfo();

        if (sir != null) {
            mSmsCarrierTextView.setText(sir.getDisplayName());
        } else if (sir == null) {
            mSmsCarrierTextView.setText(R.string.ask_first);
        }

        mSmsTextView.setEnabled(mAvailableSubInfoList.size() > 1);
        mSmsLayout.setEnabled(mAvailableSubInfoList.size() > 1);
    }

	
	private void updateSimOptionsState() {
        boolean isSimEnabled = !mTelephonyManager.isRadioBusy()
                && !mTelephonyManager.isAirplaneModeOn() && mAvailableSubInfoList.size() > 0;
        mCellularDataLayout.setEnabled(isSimEnabled);
        mPhoneCallLayout.setEnabled(isSimEnabled);
        mSmsLayout.setEnabled(isSimEnabled);
        mCellularDataTextView.setEnabled(isSimEnabled);
        mPhoneCallTextView.setEnabled(isSimEnabled);
        mSmsTextView.setEnabled(isSimEnabled);
    }
	
	private void initView() {
	    mSimOperationView = mAboveViewStub.inflate();
		mCellularDataLayout = mSimOperationView.findViewById(R.id.sim_cellular_data_layout);
		mCellularDataCarrierTextView = (TextView) mSimOperationView.findViewById(R.id.sim_cellular_data_carrier);
		mCellularDataTextView = (TextView) mSimOperationView.findViewById(R.id.sim_cellular_data);
		mPhoneCallLayout = mSimOperationView.findViewById(R.id.sim_phone_call_layout);
		mPhoneCallCarrierTextView = (TextView) mSimOperationView.findViewById(R.id.sim_phonecall_carrier);
        mPhoneCallTextView = (TextView) mSimOperationView.findViewById(R.id.sim_phonecall);
		mSmsLayout = mSimOperationView.findViewById(R.id.sim_sms_layout);
        mSmsCarrierTextView = (TextView) mSimOperationView.findViewById(R.id.sim_sms_carrier);
        mSmsTextView = (TextView) mSimOperationView.findViewById(R.id.sim_sms);
        
        mCellularDataCarrierTextView.setText(R.string.sim_off);
        mPhoneCallCarrierTextView.setText(R.string.ask_first);
        mSmsCarrierTextView.setText(R.string.ask_first);
        
        mCellularDataLayout.setOnClickListener(new OptionOnClickListener());
        mPhoneCallLayout.setOnClickListener(new OptionOnClickListener());
        mSmsLayout.setOnClickListener(new OptionOnClickListener());
        
	}

	private class OptionOnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            final Context context = SimOperationOptionSettingActivity.this;
            Intent intent = new Intent(context, SimDialogActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            switch (v.getId()) {
                case R.id.sim_cellular_data_layout:
                    intent.putExtra(SimDialogActivity.DIALOG_TYPE_KEY, SimDialogActivity.DATA_PICK);
                    context.startActivity(intent);
                    break;
                case R.id.sim_phone_call_layout:
                    intent.putExtra(SimDialogActivity.DIALOG_TYPE_KEY, SimDialogActivity.CALLS_PICK);
                    context.startActivity(intent);
                    break;
                case R.id.sim_sms_layout:
                    intent.putExtra(SimDialogActivity.DIALOG_TYPE_KEY, SimDialogActivity.SMS_PICK);
                    context.startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    }

	
	@Override
	public void onLeftKeyPress() {
	    if(mCellularDataLayout.isEnabled() && mCellularDataLayout.hasFocus()){
	        mCellularDataLayout.performClick();
	    }else if(mPhoneCallLayout.isEnabled() && mPhoneCallLayout.hasFocus()){
	        mPhoneCallLayout.performClick();
	    }else if(mSmsLayout.isEnabled() && mSmsLayout.hasFocus()){
	        mSmsLayout.performClick();
	    }
	}

	@Override
	public void onMiddleKeyPress() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRightKeyPress() {
		launchActivity(DualCardManageActivity.class) ;
		finish() ;
	}

	@Override
    protected void onPause() {
        super.onPause();
        getContentResolver().unregisterContentObserver(mMobileDataObserver);
        final TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        tm.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        getContentResolver().unregisterContentObserver(mRadioBusyObserver);
        mSubscriptionManager.removeOnSubscriptionsChangedListener(mOnSubscriptionsChangeListener);
    }
	
	private ContentObserver mMobileDataObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            updateCellularDataValues();
        }

    };
	
    private final SubscriptionManager.OnSubscriptionsChangedListener mOnSubscriptionsChangeListener = new SubscriptionManager.OnSubscriptionsChangedListener() {
        @Override
        public void onSubscriptionsChanged() {
            if (DEBUG)
                Log.d(TAG,"============== mOnSubscriptionsChangeListener onSubscriptionsChanged:==================");
            mAvailableSubInfoList = getActiveSubInfoList();
            updateAllOptions();
        }
    };
    
    /* SPRD: add option to enable/disable sim card @{ */
    private ContentObserver mRadioBusyObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            if (DEBUG)
                Log.d(TAG, "==============mRadioBusyObserver onChange ===========");
            updateAllOptions();
            updateSimOptionsState();
        }
    };
    
	private final PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        // Disable Sim selection for Data when voice call is going on as changing the default data
        // sim causes a modem reset currently and call gets disconnected
        // ToDo : Add subtext on disabled preference to let user know that default data sim cannot
        // be changed while call is going on
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            if (DEBUG) Log.d(TAG,"==================PhoneStateListener.onCallStateChanged: state=" + state);
            final boolean ecbMode = SystemProperties.getBoolean(
                    TelephonyProperties.PROPERTY_INECM_MODE, false);
            
            if (mSubscriptionManager.getActiveSubscriptionIdList().length <= 1) {
                mCellularDataLayout.setEnabled(false);
                mCellularDataTextView.setEnabled(false);
            } else {
                boolean isEnable = (state == TelephonyManager.CALL_STATE_IDLE) && !ecbMode && mAvailableSubInfoList.size() > 0;
                mCellularDataLayout.setEnabled(isEnable);
                mCellularDataTextView.setEnabled(isEnable);
            }
                 
            
        }
    };
    
    private List<SubscriptionInfo> getActiveSubInfoList() {
        if (mSubscriptionManager == null) {
            return new ArrayList<SubscriptionInfo>();
        }
         
        List<SubscriptionInfo> availableSubInfoList = mSubscriptionManager
                .getActiveSubscriptionInfoList();
        if (availableSubInfoList == null) {
            return new ArrayList<SubscriptionInfo>();
        }
        Iterator<SubscriptionInfo> iterator = availableSubInfoList.iterator();
        while (iterator.hasNext()) {
            SubscriptionInfo subInfo = iterator.next();
            int phoneId = subInfo.getSimSlotIndex();
            boolean isSimReady = mTelephonyManager.getSimState(phoneId) == TelephonyManager.SIM_STATE_READY;
            boolean isSimStandby = Settings.Global.getInt(this.getContentResolver(),
                    Settings.Global.SIM_STANDBY + phoneId, 1) == 1;
            if (!isSimStandby || !isSimReady) {
                iterator.remove();
            }
        }
        return availableSubInfoList;
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                onLeftKeyPress();
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
    
}

