
package cn.stj.settings.model;

import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.backup.BackupManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Switch;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.nfc.Tag;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.UserHandle;

import cn.stj.settings.Constant;
import cn.stj.settings.model.SettingModel.OnGetRingtoneFinish;
import cn.stj.settings.model.SettingModel.OnSetFinish;
//deleted begin by lzy@20160630, for cause build error at android_6.0 platform.
//import com.android.internal.os.storage.ExternalStorageFormatter;

import java.lang.reflect.Method;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import android.provider.Settings.Bookmarks;

public class SettingModelImpl implements SettingModel {

    private OnGetRingtoneFinish mOnGetRingtoneFinish;
    protected static final String TAG = SettingModelImpl.class.getSimpleName();
    public static final String TITLE = "title";
    public static final String FOLDER = "folder";
    public static final String INTENT = "intent";
    public static final String SHORTCUT = "shortcut";
    public static final String ORDERING = "ordering";
    public static final String ID = "_id";
    private static final String[] sIntentProjection = {
            INTENT
    };
    private static final String[] sShortcutProjection = {
            ID, SHORTCUT
    };
    private static final String sShortcutSelection = SHORTCUT + "=?";

    @Override
    public void setAirplaneMode(Context context, boolean isOpen) {
        Settings.Global.putInt(context.getContentResolver(),
                Settings.Global.AIRPLANE_MODE_ON, isOpen ? 1 : 0);
        Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        intent.putExtra("state", isOpen);
        context.sendBroadcastAsUser(intent, UserHandle.ALL);
    }

    @Override
    public void setDataConnection(Context context, boolean isOpen) {
        ConnectivityManager connectivityManager = null;
        try {
            /*connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            Method method = connectivityManager.getClass().getMethod(
                    "setMobileDataEnabled", new Class[] {
                        boolean.class
                    });
            method.invoke(connectivityManager, isOpen);*/
        	if (TelephonyManager.getDefault().getSimCount() == 1) {
                Settings.Global.putInt(context.getContentResolver(), android.provider.Settings.Global.MOBILE_DATA,
                		isOpen ? 1 : 0);
            } else {
            	Settings.Global.putInt(context.getContentResolver(), android.provider.Settings.Global.MOBILE_DATA+"0",
                		isOpen ? 1 : 0);
            	Settings.Global.putInt(context.getContentResolver(), android.provider.Settings.Global.MOBILE_DATA+"1",
                		isOpen ? 1 : 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setBatteryPercentage(Context context, boolean isOpen) {
        Settings.Global.putInt(context.getContentResolver(),
                "battery_percentage_enabled", isOpen ? 1 : 0);
        Intent intent = new Intent(Intent.ACTION_BATTERY_CHANGED);
        intent.putExtra("state", isOpen);
        context.sendBroadcastAsUser(intent, UserHandle.ALL);
    }

    @Override
    public void setSOSHelp() {
        // TODO Auto-generated method stub

    }

    @Override
    public void setBrightness(Context context, int brightness) {
        Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS,
                brightness);
    }

    @Override
    public void setSleepTime(Context context, long sleepTime) {
        Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT,
                (int) sleepTime);
    }

    @Override
    public void setRingVolume(Context context, int volume) {
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.setStreamVolume(AudioManager.STREAM_RING, volume,
                AudioManager.FLAG_ALLOW_RINGER_MODES);

    }

    @Override
    public void setRingtone(Context context, int type, int position) {
        RingtoneManager manager = new RingtoneManager(context);
        manager.getCursor();
        RingtoneManager
                .setActualDefaultRingtoneUri(context, type, manager.getRingtoneUri(position));
    }

    @Override
    public void setDateTime(Context context, long dateTime, OnSetFinish onSetFinish) {
        boolean mIsSetDataTimeSucess = false;
        if ((dateTime > 0) && (dateTime / 1000 < Integer.MAX_VALUE)) {
            mIsSetDataTimeSucess = true;
            ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).setTime(dateTime);
        }
        onSetFinish.onFinish(mIsSetDataTimeSucess);
    }

    @Override
    public void setLanguage(Locale locale) {
        try {
            IActivityManager am = ActivityManagerNative.getDefault();
            Configuration config = am.getConfiguration();

            // Will set userSetLocale to indicate this isn't some passing
            // default - the user
            // wants this remembered
            config.setLocale(locale);

            am.updateConfiguration(config);
            // Trigger the dirty bit for the Settings Provider.
            BackupManager.dataChanged("com.android.providers.settings");
        } catch (RemoteException e) {
            // Intentionally left blank
        }
    }

    @Override
    public void setMasterReset(Context context) {
        //modify begin by lzy@20160630, for cause build error at android_6.0 platform.
        /*
        Intent intent = new Intent(ExternalStorageFormatter.FORMAT_AND_FACTORY_RESET);
        intent.setComponent(ExternalStorageFormatter.COMPONENT_NAME);
        intent.putExtra(ExternalStorageFormatter.EXTRA_FORMAT_INTERNAL, true);
        context.startService(intent);
        */
        Intent intent = new Intent(Intent.ACTION_MASTER_CLEAR);
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        intent.putExtra(Intent.EXTRA_REASON, "MasterClearConfirm");
        intent.putExtra(Intent.EXTRA_WIPE_EXTERNAL_STORAGE, "0"/*mEraseSdCard*/);
        context.sendBroadcast(intent);
        //modify end.
    }

    @Override
    public void setFunctionKey(Context context, int shortcut, String title, String folder,
            int ordering, Intent intent) {
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        if (title != null)
            values.put(TITLE, title);
        if (folder != null)
            values.put(FOLDER, folder);
        values.put(INTENT, intent != null ? intent.toUri(0) : "");
        if (shortcut != 0)
            values.put(SHORTCUT, shortcut);
        values.put(ORDERING, ordering);
        int updateCount = contentResolver.update(Bookmarks.CONTENT_URI, values, sShortcutSelection,
                new String[] {
                    String.valueOf(shortcut)
                });
        if (updateCount <= 0) {
            contentResolver.insert(Bookmarks.CONTENT_URI, values);
        }
    }

    @Override
    public void getRingtoneTitleList(final Context context, final int type,
            final OnGetRingtoneFinish onGetRingtoneFinish) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> ringToneTitles = new ArrayList<String>();
                RingtoneManager manager = new RingtoneManager(context);
                manager.setType(type);
                Cursor cursor = manager.getCursor();
                if (cursor.moveToFirst()) {
                    do {
                        ringToneTitles.add(cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX));
                    } while (cursor.moveToNext());

                }
                cursor.close();
                mOnGetRingtoneFinish = onGetRingtoneFinish;
                Message message = mHandler.obtainMessage(Constant.GET_RINGTONE_TITLE,
                        ringToneTitles);
                mHandler.sendMessage(message);
            }
        }).start();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.GET_RINGTONE_TITLE:
                    List<String> ringToneTitles = (List<String>) msg.obj;
                    if (mOnGetRingtoneFinish != null && ringToneTitles != null) {
                        mOnGetRingtoneFinish.onGetRingToneTitleFinish(ringToneTitles);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void setAutotimeState(Context context, boolean isOpen) {
        Settings.Global.putInt(context.getContentResolver(), Settings.Global.AUTO_TIME,
                isOpen ? 1 : 0);
    }

    public void setHourFormat(Context context, boolean isOpen) {
        Settings.System.putString(context.getContentResolver(),
                Settings.System.TIME_12_24,
                isOpen ? Constant.Settings.HOURS_24 : Constant.Settings.HOURS_12);
    }

    @Override
    public void setCallVoiceBroadcast(Context context, boolean isOpen) {
        Settings.System.putInt(context.getContentResolver(),
                Constant.Settings.CALL_VOICE_BROADCAST,
                isOpen ? 1 : 0);
    }

    @Override
    public void setTimeVoiceBroadcast(Context context, boolean isOpen) {
        Settings.System.putInt(context.getContentResolver(),
                Constant.Settings.TIME_VOICE_BROADCAST,
                isOpen ? 1 : 0);
    }

    @Override
    public void setKeyVoiceBroadcast(Context context, boolean isOpen) {
        Settings.System.putInt(context.getContentResolver(),
                Constant.Settings.KEY_VOICE_BROADCAST,
                isOpen ? 1 : 0);
    }

}
