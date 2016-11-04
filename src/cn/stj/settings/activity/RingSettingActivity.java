
package cn.stj.settings.activity;

import android.content.Context;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;

import cn.stj.settings.R;
import cn.stj.settings.adapter.OpenOffListAdapter;
import cn.stj.settings.presenter.SettingPresenter;
import cn.stj.settings.presenter.SettingPresenterImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * ring setting activity
 * 
 * @author jackey
 */
public class RingSettingActivity extends BaseActivity implements
        BaseActivity.BottomKeyClickListener {

    private static final String TAG = RingSettingActivity.class.getSimpleName();
    public static final String KEY_RINGTONE_LIST = "key_ringtone_list";
    public static final String KEY_RINGTONETITLE_LIST = "key_ringtonetitle_list";
    private OpenOffListAdapter mSettingListAdapter;
    private int mSelectedPosition;
    // save the selected sleep time
    private static final String SELECTED_RING = "selected_ring";
    private HashMap<String, Integer> mRingSelectedMap = new HashMap<String, Integer>();
    private SettingPresenter mSettingPresenter;
    private List<String> mRingToneTitles = new ArrayList<String>();
    private RingtoneManager mRingtoneManager;
    private Handler mHandler = new Handler();
    private Ringtone mPlayingRingtone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAboveViewStub.setLayoutResource(R.layout.activity_settings_listview);
        setBottomKeyClickListener(this);

        initView();

        mRingtoneManager = new RingtoneManager(this);
        mRingtoneManager.setType(RingtoneManager.TYPE_RINGTONE);
        mSettingPresenter = new SettingPresenterImpl(this);
        mSettingPresenter.getRingtoneTitleList(this, RingtoneManager.TYPE_RINGTONE);
    }

    /**
     * 初始化系统设置界面
     */
    private void initView() {
        View listView = mAboveViewStub.inflate();
        mSettingListAdapter = new OpenOffListAdapter(this, mRingToneTitles);
        final ListView lv = (ListView) listView.findViewById(R.id.main_list_view);
        lv.setAdapter(mSettingListAdapter);
        lv.setOnItemClickListener(new ListItemClickListener());
        lv.setOnItemSelectedListener(new ListItemSelectedListener());
    }

    private class ListItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mSelectedPosition = position;
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

    private void onSettingItemClick(final int position) {
        int selectedPosition = 0;
        if (mRingSelectedMap.get(SELECTED_RING) != null) {
            selectedPosition = mRingSelectedMap.get(SELECTED_RING);
            if (selectedPosition != position) {
                mSettingListAdapter.getmOpenOffStatusMap().put(selectedPosition, false);
            }
        }
        mSettingListAdapter.getmOpenOffStatusMap().put(position, true);
        mRingSelectedMap.put(SELECTED_RING, position);
        mSettingListAdapter.setSelectedItemPosition(position);
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                mPlayingRingtone = getRingtone(RingSettingActivity.this, position);
                mPlayingRingtone.play();
            }
        });
    }

    @Override
    public void onLeftKeyPress() {
        onSettingItemClick(mSelectedPosition);
        confirm();
        finish();
    }

    private void confirm() {
        setRingtone(mSelectedPosition);
    }

    private void setRingtone(int position) {
        mSettingPresenter.setRingtone(this, RingtoneManager.TYPE_RINGTONE, position);
    }

    @Override
    public void onMiddleKeyPress() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onRightKeyPress() {
        if (mPlayingRingtone != null && mPlayingRingtone.isPlaying()) {
            mPlayingRingtone.stop();
        }
        launchActivity(SoundSettingActivity.class);
    }

    public Ringtone getDefaultRingtone(int type) {
        return RingtoneManager.getRingtone(this,
                RingtoneManager.getActualDefaultRingtoneUri(this, type));
    }

    public Ringtone getRingtone(Context context, int pos) {
        // 不加这行代码，查询的uri会出错；
        mRingtoneManager.getCursor();
        mRingtoneManager.setStopPreviousRingtone(true);
        return mRingtoneManager.getRingtone(pos);
    }

    public void onSuccess(HashMap<String, Object> respDatas) {
        mRingToneTitles = (List<String>) respDatas.get(KEY_RINGTONETITLE_LIST);
        if (mRingToneTitles != null) {
            updateRingtoneListView();
        }
    };

    private void updateRingtoneListView() {
        Ringtone mDefaultRingtone = getDefaultRingtone(RingtoneManager.TYPE_RINGTONE);
        String defaultRingtoneTitle = mDefaultRingtone.getTitle(this);
        for (int i = 0; i < mRingToneTitles.size(); i++) {
            if (defaultRingtoneTitle.equals(mRingToneTitles.get(i))) {
                mSelectedPosition = i;
            }
        }
        mSettingListAdapter.setDatas(mRingToneTitles);
        mSettingListAdapter.updateOpenOffStatusMap(mSelectedPosition, true);
        mRingSelectedMap.put(SELECTED_RING, mSelectedPosition);
        mSettingListAdapter.notifyDataSetChanged();
    }

}
