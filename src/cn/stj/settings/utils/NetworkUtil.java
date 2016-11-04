
package cn.stj.settings.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemProperties;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * network util
 * 
 * @author jackey
 */
public class NetworkUtil {

    private static final String TAG = NetworkUtil.class.getSimpleName();

    /**
     * 妫�鏌ョ綉缁滅姸鎬�
     * 
     * @param context
     * @return true 鏈夌綉缁� false 娌＄綉缁�
     */
    public static boolean checkNetwork(Context context) {
        try {
            if (context == null)
                return false;
            ConnectivityManager mConnMgr = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (mConnMgr == null)
                return false;
            NetworkInfo aActiveInfo = mConnMgr.getActiveNetworkInfo(); // 鑾峰彇娲诲姩缃戠粶杩炴帴淇℃伅

            if (aActiveInfo != null && aActiveInfo.isConnected()
                    && aActiveInfo.getState() == NetworkInfo.State.CONNECTED) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }

    }

    /**
     * 妫�鏌obile缃戠粶鐘舵��
     * 
     * @param context
     * @return true 鏈夌綉缁� false 娌＄綉缁�
     */
    public static boolean checkMobileNetwork(Context context) {
            if (context == null)
                return false;
            boolean retVal = "true".equalsIgnoreCase(SystemProperties.get(
                    "ro.com.android.mobiledata", "true"));
            try {
                if (TelephonyManager.getDefault().getSimCount() == 1) {
                    retVal = Settings.Global.getInt(context.getContentResolver(), android.provider.Settings.Global.MOBILE_DATA,
                            retVal ? 1 : 0) != 0;
                } else {
                    retVal = TelephonyManager.getIntWithSubId(context.getContentResolver(), android.provider.Settings.Global.MOBILE_DATA,
                            0) != 0  || TelephonyManager.getIntWithSubId(context.getContentResolver(), android.provider.Settings.Global.MOBILE_DATA,
                                    1) != 0;
                }
            } catch (SettingNotFoundException snfe) {
                retVal = "true".equalsIgnoreCase(
                        SystemProperties.get("ro.com.android.mobiledata", "true"));
            }
            return retVal;

    }
}
