
package cn.stj.settings.activity;

import cn.stj.settings.R;
import cn.stj.settings.presenter.SettingPresenter;
import cn.stj.settings.presenter.SettingPresenterImpl;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * Brightness and volume Setting Activity
 * 
 * @author jackey
 */
public class BrightnessVolumeSettingActivity extends BaseActivity implements
        BaseActivity.BottomKeyClickListener {
    private static final String TAG = BrightnessVolumeSettingActivity.class.getSimpleName();

    public static final String SETTING_OPTIONS = "setting_options";
    public static final String BRIGHTNESS = "brightness";
    public static final String VOLUME = "volume";
    private static final int MAXPROCESS = 255;

    private SeekBar mSeekBar;
    private int mProgress;
    private SettingOptions settingOption;
    private SettingPresenter mSettingPresenter;
    private int mScreenBrightness;
    private int mRingVolume;
    private AudioManager mAudioManager;
    private boolean mIsSeeking ;

    private enum SettingOptions {
        BRIGHTNESS, VOLUME
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAboveViewStub.setLayoutResource(R.layout.activity_brightness_setting);
        setBottomKeyClickListener(this);

        initView();

        mSettingPresenter = new SettingPresenterImpl(this);

        handleIntent(getIntent());
    }

    private void handleIntent(Intent intent) {
        if (intent != null && intent.hasExtra(SETTING_OPTIONS)) {
            String setting_options = intent.getStringExtra(SETTING_OPTIONS);
            String topTitleText = null;
            if (BRIGHTNESS.equals(setting_options)) {
                settingOption = SettingOptions.BRIGHTNESS;
                topTitleText = getResources().getString(R.string.display_brightness);
                mSeekBar.setMax(MAXPROCESS);
                updateScreenBrightness();
            } else if (VOLUME.equals(setting_options)) {
                settingOption = SettingOptions.VOLUME;
                topTitleText = getResources().getString(R.string.volume);
                mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                mSeekBar.setMax(getMaxRingVolume());
                updateRingVolume();
            }
            setTopTitleText(topTitleText);
        }
    }

    private void initView() {
        View brightnessView = mAboveViewStub.inflate();
        mSeekBar = (SeekBar) brightnessView.findViewById(R.id.brightness_seekbar);
        mSeekBar.setOnSeekBarChangeListener(new BrightnessSeekBarChangeListener());
    }

    private class BrightnessSeekBarChangeListener implements OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                boolean fromUser) {
            mProgress = progress;
            if(!mIsSeeking){
                showWindowBrightness();
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            mIsSeeking = true;
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            showWindowBrightness();
            mIsSeeking =false;
        }

        private void showWindowBrightness() {
            if (settingOption == SettingOptions.BRIGHTNESS) {
                WindowManager.LayoutParams lp = BrightnessVolumeSettingActivity.this.getWindow()
                        .getAttributes();
                lp.screenBrightness = mProgress / 255.0f;
                BrightnessVolumeSettingActivity.this.getWindow().setAttributes(lp);
            }
        }

    }

    @Override
    public void onLeftKeyPress() {
        confirm();
        finish();
    }

    /**
     * confirm
     */
    private void confirm() {
        switch (settingOption) {
            case BRIGHTNESS:
                setBrightness(mProgress);
                break;
            case VOLUME:
                setRingVolume(mProgress);
                break;
            default:
                break;
        }
    }

    private void setRingVolume(int ringVolume) {
        mSettingPresenter.setRingVolume(this, ringVolume);
    }

    private void setBrightness(int screenBrightness) {
        mSettingPresenter.setBrightness(this, screenBrightness);
    }

    @Override
    public void onMiddleKeyPress() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onRightKeyPress() {
        switch (settingOption) {
            case BRIGHTNESS:
                launchActivity(DisplaySettingActivity.class);
                break;
            case VOLUME:
                launchActivity(SoundSettingActivity.class);
                break;
            default:
                break;
        }
    }

    /**
     * update screen brightness
     */
    private void updateScreenBrightness() {
        mScreenBrightness = getScreenBrightness();
        mSeekBar.setProgress(mScreenBrightness);
    }

    /**
     * update ring volume
     */
    private void updateRingVolume() {
        mRingVolume = getRingVolume();
        mSeekBar.setProgress(mRingVolume);
    }

    /**
     * 获得当前屏幕亮度的模式 SCREEN_BRIGHTNESS_MODE_AUTOMATIC=1 为自动调节屏幕亮度
     * SCREEN_BRIGHTNESS_MODE_MANUAL=0 为手动调节屏幕亮度
     */
    private int getScreenMode() {
        int screenMode = 0;
        try {
            screenMode = Settings.System.getInt(getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE);
        } catch (Exception localException) {

        }
        return screenMode;
    }

    /**
     * 获得当前屏幕亮度值 0--255
     */
    private int getScreenBrightness() {
        int screenBrightness = 255;
        try {
            screenBrightness = Settings.System.getInt(getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception localException) {

        }
        return screenBrightness;
    }

    private int getRingVolume() {
        return mAudioManager.getStreamVolume(AudioManager.STREAM_RING);
    }

    private int getMaxRingVolume() {
        return mAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode, event);
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                Log.d(TAG, "onKeyDown--->>KEYCODE_DPAD_LEFT");
                // 物理返回键
                if (mProgress < MAXPROCESS) {
                    mSeekBar.setProgress(mProgress + 1);
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                Log.d(TAG, "onKeyDown--->>KEYCODE_MENU");
                if (mProgress > 0) {
                    mSeekBar.setProgress(mProgress - 1);
                }
                break;
            default:
                break;
        }
        return false;
    }
}
