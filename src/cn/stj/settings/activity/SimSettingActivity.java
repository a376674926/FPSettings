
package cn.stj.settings.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import cn.stj.settings.R;
import cn.stj.settings.adapter.SettingListAdapter;

import android.R.integer;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.preference.Preference;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.android.internal.telephony.TelephonyProperties;

/**
 * Sim setting activity
 * 
 * @author jackey add by hhj@20160712 for sim setting activity
 */
public class SimSettingActivity extends BaseActivity implements BaseActivity.BottomKeyClickListener {

    private static final String TAG = SimSettingActivity.class.getSimpleName();
    private static final boolean DEBUG = true;
    private static final String PROGRESS_DIALOG_TAG = "progress_dialog";
    private SettingListAdapter mSettingListAdapter;
    private int mSelectedPosition;
    private TextView mSimCarrierOneTextView;
    private CheckBox mCheckBoxOne;
    private TextView mSimCarrierSecondTextView;
    private CheckBox mCheckBoxSecond;
    private View mSimOneLayout;
    private View mSimSecondLayout;
    private ViewGroup mParentSimView;

    private TextView mSimCarrierTextView;
    private ImageView mSimIconImageView;
    private CheckBox mSwitchCheckBox;
    private View mSimLayout;

    private SubscriptionManager mSubscriptionManager;
    private TelephonyManager mTelephonyManager = null;
    private int mNumSlots;
    private List<SubscriptionInfo> mSelectableSubInfos = null;
    private List<SubscriptionInfo> mAvailableSubInfoList = null;
    private int[] mSlotIcons = {
            R.drawable.ic_solt_one, R.drawable.ic_solt_second
    };
    private FragmentManager mFragmentManager;
    private static PorgressDialogFragment mProgressDialogFragment = null;
    private int mProgressShow = 1;
    private boolean mhasProgressShow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAboveViewStub.setLayoutResource(R.layout.activity_sim_setting);
        mParentSimView = (ViewGroup) mAboveViewStub.inflate();
        setBottomKeyClickListener(this);

        mSubscriptionManager = SubscriptionManager.from(this);
        mFragmentManager = getFragmentManager();
        mTelephonyManager = TelephonyManager.from(this);
        final TelephonyManager tm =
                (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

        mNumSlots = tm.getSimCount();

        mSelectableSubInfos = new ArrayList<SubscriptionInfo>();
        mAvailableSubInfoList = getActiveSubInfoList();

        if (mAvailableSubInfoList.size() > 1) {
            initSimManagerSharedPreferences();
        }
        // initView();
        // add begin. by hhj@20160720
        registerRadioBusyObserver();
        addOnSubscriptionsChangedListener();
        // add end.
    }

    // add begin. by hhj@20160720
    private void addOnSubscriptionsChangedListener() {
        mSubscriptionManager.addOnSubscriptionsChangedListener(mOnSubscriptionsChangeListener);
    }

    private void registerRadioBusyObserver() {
        getContentResolver().registerContentObserver(
                Settings.Global.getUriFor(Settings.Global.RADIO_OPERATION), true,
                mRadioBusyObserver, UserHandle.USER_OWNER);
    }

    // add end.

    @Override
    protected void onResume() {
        super.onResume();
        /* SPRD: modify for bug 493220 @{ */
        // delete begin. by hhj@20160720 for
        /*
         * getContentResolver().registerContentObserver(
         * Settings.Global.getUriFor(Settings.Global.RADIO_OPERATION), true,
         * mRadioBusyObserver, UserHandle.USER_OWNER); // SPRD: modify for bug
         * 508104 mSubscriptionManager.addOnSubscriptionsChangedListener(
         * mOnSubscriptionsChangeListener); //add begin. by hhj@20160719 for
         * dimiss progress dialog when onresume if(mProgressDialogFragment !=
         * null){ HasShowProgress(); } //add end.
         */// delete end.
        updateSimState();
    }

    private void updateSubscriptions() {
        mAvailableSubInfoList = mSubscriptionManager.getActiveSubscriptionInfoList();
        mParentSimView.removeAllViews();

        for (int i = 0; i < mNumSlots; ++i) {
            View simView = getLayoutInflater().inflate(R.layout.sim_setting_layout, null);
            simView.setOnClickListener(new SimViewOnClickListener());
            mParentSimView.addView(simView);
        }

        updateSimSlotValues();
    }

    private void updateSimSlotValues() {
        int childCount = mParentSimView.getChildCount();
        for (int i = 0; i < childCount; ++i) {
            View simView = mParentSimView.getChildAt(i);
            final SubscriptionInfo sir = mSubscriptionManager
                    .getActiveSubscriptionInfoForSimSlotIndex(i);
            updateSimView(simView, sir, i);
        }
    }

    private void updateSimView(View simView, SubscriptionInfo subInfoRecord, final int slotId) {
        mSimCarrierTextView = (TextView) simView.findViewById(R.id.sim_carrier);
        mSwitchCheckBox = (CheckBox) simView.findViewById(R.id.sim_checkbox);
        mSimLayout = simView.findViewById(R.id.sim_layout);
        mSimIconImageView = (ImageView) simView.findViewById(R.id.sim_icon);
        if (subInfoRecord != null) {// SPRD: add option to enable/disable sim
                                    // card
            if (!mTelephonyManager.isSimStandby(slotId)) {
                mSimCarrierTextView.setText(R.string.not_stand_by);
            } else {
                mSimCarrierTextView.setText(subInfoRecord.getDisplayName());
                mSimLayout.setEnabled(!mTelephonyManager.isRadioBusy()
                        && !mTelephonyManager.isAirplaneModeOn()
                        // SPRD: modify the bug494142
                        && mTelephonyManager.getSimState(subInfoRecord
                                .getSimSlotIndex()) == TelephonyManager.SIM_STATE_READY);
            }
            mSimIconImageView.setImageDrawable(new BitmapDrawable(getResources(), (subInfoRecord
                    .createIconBitmap(this))));
        } else {
            mSimCarrierTextView.setText(R.string.sim_slot_empty);
            mSimLayout.setEnabled(false);
        }

        mSwitchCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boolean standby = mTelephonyManager.isSimStandby(slotId);
                if (standby != isChecked) {
                    if (mTelephonyManager != null) {
                        mTelephonyManager.setSimStandby(slotId, isChecked);
                        showProgressDialog();
                        updateSimSlotValues();
                        // delete begin. by hhj@20160720
                        // HasShowProgress();
                    }
                }
            }
        });

        if (subInfoRecord != null) {
            boolean standby = mTelephonyManager.isSimStandby(slotId);
            mSwitchCheckBox.setChecked(standby);
            boolean canSetSimStandby = (mTelephonyManager.getSimState(
                    slotId) == TelephonyManager.SIM_STATE_READY || !standby)
                    && !mTelephonyManager.isRadioBusy()
                    && !mTelephonyManager.isAirplaneModeOn();
            mSwitchCheckBox.setEnabled(canSetSimStandby);
            mSwitchCheckBox.setVisibility(View.VISIBLE);
        } else {
            // mSwitchCheckBox.setVisibility(View.GONE);
            mSwitchCheckBox.setEnabled(false);
        }
        // ensure request focus after update sim view
        mParentSimView.requestFocus();
    }

    private void updateSimState() {
        if (!mTelephonyManager.isRadioBusy() && mhasProgressShow) { // SPRD:
                                                                    // modify
                                                                    // for bug
                                                                    // 514144
            if (mProgressDialogFragment != null) {
                mProgressDialogFragment.dismissAllowingStateLoss(); // SPRD:
                                                                    // modify
                                                                    // for
                                                                    // bug500791
                mProgressDialogFragment = null;
                mhasProgressShow = false;
            }
        }

        if (mTelephonyManager.isAirplaneModeOn()) {
            if (mProgressDialogFragment != null) {
                mProgressDialogFragment.dismissAllowingStateLoss(); // SPRD:
                                                                    // modify
                                                                    // for
                                                                    // bug500791
                mProgressDialogFragment = null;
            }
        }
        boolean isSimEnabled = !mTelephonyManager.isRadioBusy()
                && !mTelephonyManager.isAirplaneModeOn();
        mParentSimView.setEnabled(isSimEnabled);
    }

    private void initSimManagerSharedPreferences() {
        SubscriptionManager subscriptionManager = SubscriptionManager.from(this);
        long smsSubId = mTelephonyManager.getMultiSimActiveDefaultSmsSubId();
        long voiceSubId = mTelephonyManager.getMultiSimActiveDefaultVoiceSubId();
        Log.d(TAG, "initSimManagerSharedPreferences, smsSubId: " + smsSubId + ",  voiceSubId: "
                + voiceSubId);
        if (smsSubId == SubscriptionManager.INVALID_SUBSCRIPTION_ID) {
            mTelephonyManager.setMultiSimActiveDefaultSmsSubId(subscriptionManager
                    .getDefaultSmsSubId());
        }
        if (voiceSubId == SubscriptionManager.INVALID_SUBSCRIPTION_ID) {
            mTelephonyManager.setMultiSimActiveDefaultVoiceSubId(subscriptionManager
                    .getDefaultVoiceSubId());
        }
    }

    /* SPRD: add option to enable/disable sim card @{ */
    private List<SubscriptionInfo> getActiveSubInfoList() {
        /* SPRD: modify for avoid null point exception @{ */
        if (mSubscriptionManager == null) {
            return new ArrayList<SubscriptionInfo>();
        }
        /* @} */
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

    private final SubscriptionManager.OnSubscriptionsChangedListener mOnSubscriptionsChangeListener = new SubscriptionManager.OnSubscriptionsChangedListener() {
        @Override
        public void onSubscriptionsChanged() {
            if (DEBUG)
                Log.d(TAG, "==============onSubscriptionsChanged===========");
            // SPRD: modify for bug497338
            mAvailableSubInfoList = getActiveSubInfoList();
            updateSubscriptions();
        }
    };

    private String getPhoneNumber(SubscriptionInfo info) {
        final TelephonyManager tm =
                (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getLine1NumberForSubscriber(info.getSubscriptionId());
    }

    /* SPRD: add option to enable/disable sim card @{ */
    private ContentObserver mRadioBusyObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            if (DEBUG)
                Log.d(TAG, "==============RadioBusyObserver onChange =========== isReaidoBusy:"
                        + mTelephonyManager.isRadioBusy());
            updateSubscriptions();
            updateSimState();
            // add begin. by hhj@20160720
            if (!mTelephonyManager.isRadioBusy()) {
                HasShowProgress();
            }
            // add end.
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        // delete begin. by hhj@20160720
        /*
         * getContentResolver().unregisterContentObserver(mRadioBusyObserver);
         * mSubscriptionManager
         * .removeOnSubscriptionsChangedListener(mOnSubscriptionsChangeListener
         * ); mHandler.removeMessages(mProgressShow);
         */
        // delete end.
    }

    // add begin. by hhj@20160720
    @Override
    protected void onDestroy() {
        super.onDestroy();
        getContentResolver().unregisterContentObserver(mRadioBusyObserver);
        mSubscriptionManager.removeOnSubscriptionsChangedListener(mOnSubscriptionsChangeListener);
    }

    // add end.

    private class SimViewOnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            CheckBox mSwitCheckBox = (CheckBox) v.findViewById(R.id.sim_checkbox);
            if (mSwitCheckBox.isEnabled()) {
                mSwitCheckBox.setChecked(!mSwitCheckBox.isChecked());
            }
        }
    }

    @Override
    public void onLeftKeyPress() {
        setSimStandby();
    }

    @Override
    public void onMiddleKeyPress() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onRightKeyPress() {
        launchActivity(DualCardManageActivity.class);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
                setSimStandby();
                break;
            case KeyEvent.KEYCODE_MENU:
                onLeftKeyPress();
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setSimStandby() {
        for (int i = 0; i < mParentSimView.getChildCount(); i++) {
            View childView = mParentSimView.getChildAt(i);
            if (childView.hasFocus()) {
                childView.performClick();
                break;
            }
        }
    }

    private void showProgressDialog() {
        Log.d(TAG, "show progressing dialog...");
        // FragmentManager fm = getFragmentManager();
        if (this.isResumed()) {
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            mProgressDialogFragment = new PorgressDialogFragment();
            mProgressDialogFragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
            mProgressDialogFragment.setCancelable(false);
            mProgressDialogFragment.show(
                    transaction, PROGRESS_DIALOG_TAG);
        }
    }

    public static class PorgressDialogFragment extends DialogFragment {
        View v;
        TextView mMessageView;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            // TODO Auto-generated method stub
            // return super.onCreateView(inflater, container,
            // savedInstanceState);
            v = inflater.inflate(R.layout.progress_dialog_fragment_ex, container, false);
            // ProgressBar mProgress = (ProgressBar)
            // v.findViewById(R.id.progress);
            mMessageView = (TextView) v.findViewById(R.id.message);
            mMessageView.setText(getResources().getString(R.string.primary_card_switching));
            // setView(view);
            return v;
        }

        /* SPRD: modify for bug493042 @{ */
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            resetProgressDialogFragment(this);
            return super.onCreateDialog(savedInstanceState);
        }
        /* @} */

    }

    private static void resetProgressDialogFragment(PorgressDialogFragment dialogFragment) {
        mProgressDialogFragment = dialogFragment;
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            mhasProgressShow = true;
            updateSimState();
        };
    };
    private long showTime;

    private void HasShowProgress() {
        // modify begin. by hhj@20160720
        // mHandler.sendEmptyMessageDelayed(mProgressShow, 5000);
        mHandler.sendEmptyMessage(mProgressShow);
        // modify end.
    }

}
