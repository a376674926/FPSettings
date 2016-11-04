
package cn.stj.settings.presenter;

import cn.stj.settings.activity.RingSettingActivity;
import cn.stj.settings.model.SettingModel;
import cn.stj.settings.model.SettingModel.OnGetRingtoneFinish;
import cn.stj.settings.model.SettingModel.OnSetFinish;
import cn.stj.settings.model.SettingModelImpl;
import cn.stj.settings.view.SettingView;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class SettingPresenterImpl implements SettingPresenter {

    private SettingModel mSettingModel;
    private SettingView mSettingView;
    private Context mContext;

    public SettingPresenterImpl(SettingView mSettingView) {
        super();
        this.mSettingModel = new SettingModelImpl();
        this.mSettingView = mSettingView;
        this.mContext = (Context) mSettingView;
    }

    @Override
    public void setAirplaneMode(Context context, boolean isOpen) {
        mSettingModel.setAirplaneMode(context, isOpen);
    }

    @Override
    public void setDataConnection(Context context, boolean isOpen) {
        mSettingModel.setDataConnection(context, isOpen);
    }

    public void setBatteryPercentage(Context context, boolean isOpen) {
        mSettingModel.setBatteryPercentage(context, isOpen);
    }

    @Override
    public void setSOSHelp() {
        // TODO Auto-generated method stub

    }

    @Override
    public void setBrightness(Context context, int brightness) {
        mSettingModel.setBrightness(context, brightness);
    }

    @Override
    public void setSleepTime(Context context, long sleepTime) {
        mSettingModel.setSleepTime(context, sleepTime);
    }

    @Override
    public void setRingVolume(Context context, int volume) {
        mSettingModel.setRingVolume(context, volume);
    }

    @Override
    public void setRingtone(Context context, int type, int position) {
        mSettingModel.setRingtone(context, type, position);
    }

    public void getRingtoneTitleList(Context context, int type) {
        mSettingModel.getRingtoneTitleList(context, type, new OnGetRingtoneFinish() {
            @Override
            public void onGetRingToneTitleFinish(List<String> ringtoneTitles) {
                HashMap<String, Object> respDatas = new HashMap<String, Object>();
                respDatas.put(RingSettingActivity.KEY_RINGTONETITLE_LIST, ringtoneTitles);
                mSettingView.onSuccess(respDatas);
            }

        });
    }

    @Override
    public void setAutotimeState(Context context, boolean isOpen) {
        mSettingModel.setAutotimeState(context, isOpen);
    }

    @Override
    public void setDateTime(Context context, long dateTime) {
        mSettingModel.setDateTime(context, dateTime, new OnSetFinish() {

            @Override
            public void onFinish(boolean isSetSuccess) {
                if (isSetSuccess) {
                    mSettingView.onSuccess(null);
                } else {
                    mSettingView.onFail();
                }

            }
        });
    }

    @Override
    public void setLanguage(Locale locale) {
        mSettingModel.setLanguage(locale);
    }

    @Override
    public void setMasterReset(Context context) {
        mSettingModel.setMasterReset(context);
    }

    @Override
    public void setFunctionKey(Context context, int shortcut, String title, String folder,
            int ordering, Intent intent) {
        mSettingModel.setFunctionKey(context, shortcut, title, folder, ordering, intent);
    }

    @Override
    /**
     * set hour format
     * @param isOpen
     */
    public void setHourFormat(Context context, boolean isOpen) {
        mSettingModel.setHourFormat(context, isOpen);
    }

    @Override
    public void setCallVoiceBroadcast(Context context, boolean isOpen) {
        mSettingModel.setCallVoiceBroadcast(context, isOpen);
    }

    @Override
    public void setTimeVoiceBroadcast(Context context, boolean isOpen) {
        mSettingModel.setTimeVoiceBroadcast(context, isOpen);
    }

    @Override
    public void setKeyVoiceBroadcast(Context context, boolean isOpen) {
        mSettingModel.setKeyVoiceBroadcast(context, isOpen);
    }
}
