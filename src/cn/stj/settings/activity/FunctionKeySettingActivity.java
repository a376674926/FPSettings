
package cn.stj.settings.activity;

import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.StaticLayout;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import cn.stj.settings.Constant;
import cn.stj.settings.R;
import cn.stj.settings.adapter.FunctionKeySettingListAdapter;
import cn.stj.settings.adapter.SettingListAdapter;
import cn.stj.settings.presenter.SettingPresenter;
import cn.stj.settings.presenter.SettingPresenterImpl;
import cn.stj.settings.widget.FunctionKeyDialog;
import cn.stj.settings.widget.FunctionKeyDialog.OnConfirmListener;

import android.provider.Settings;

/**
 * Function key setting activity
 * 
 * @author jackey
 */
public class FunctionKeySettingActivity extends BaseActivity implements
        BaseActivity.BottomKeyClickListener {

    private static final String TAG = FunctionKeySettingActivity.class
            .getSimpleName();
    private int mSelectedPosition;
    private SettingPresenter mSettingPresenter;
    private FunctionKeyDialog mFunctionKeyDialog;
    private FunctionKeySettingListAdapter mAdapter;
    private List<HashMap<String, Object>> mFuncitonKeyList = new ArrayList<HashMap<String, Object>>();
    private static final int COLUMN_SHORTCUT = 0;
    private static final int COLUMN_TITLE = 1;
    private static final int COLUMN_INTENT = 2;
    private static final String TITLE = "title";
    private static final String INTENT = "intent";
    private static final int DPAD_UP = 19;
    private static final int DPAD_DOWN = 20;
    private static final int DPAD_LEFT = 21;
    private static final int DPAD_RIGHT = 22;
    private static final int[] DPAD_KEYS = new int[] {
            DPAD_UP, DPAD_DOWN, DPAD_LEFT, DPAD_RIGHT
    };
    private static final int[] DPAD_KEYS_VALUE = new int[] {
            R.string.key_up, R.string.key_down,
            R.string.key_left, R.string.key_right
    };
    private HashMap<Integer, Object> mShortcutMap = new HashMap<Integer, Object>();
    private Handler mUiHandler = new Handler();

    private PackageManager mPackageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAboveViewStub.setLayoutResource(R.layout.activity_settings_listview);
        setBottomKeyClickListener(this);

        initView();

        mSettingPresenter = new SettingPresenterImpl(this);
        mPackageManager = getApplicationContext().getPackageManager();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshShortcuts();
    }

    /**
     * 初始化系统设置界面
     */
    private void initView() {
        View listView = mAboveViewStub.inflate();
        mAdapter = new FunctionKeySettingListAdapter(this, mFuncitonKeyList);
        final ListView lv = (ListView) listView
                .findViewById(R.id.main_list_view);
        lv.setAdapter(mAdapter);
        lv.setOnItemClickListener(new ListItemClickListener());
        lv.setOnItemSelectedListener(new ListItemSelectedListener());

    }

    private class ListItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            mSelectedPosition = position;
            onSettingItemClick(position);
        }
    }

    private class ListItemSelectedListener implements OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                int position, long id) {
            mSelectedPosition = position;
            mAdapter.setSelectedItemPosition(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }

    }

    private void onSettingItemClick(int position) {
        showFunctionKeyDialog();
    }

    /**
     * show function dialog
     */
    private void showFunctionKeyDialog() {
        HashMap<String, Object> map = mFuncitonKeyList.get(mSelectedPosition);
        mFunctionKeyDialog = new FunctionKeyDialog(this);
        mFunctionKeyDialog.setmShortcut(DPAD_KEYS[mSelectedPosition]);
        mFunctionKeyDialog.setmFunctionName(map.get(Constant.ITEM_FUNCTION).toString());
        mFunctionKeyDialog.setOnConfirmListener(new DialogOnConfirmListener());
        mFunctionKeyDialog.show();
    }

    private class DialogOnConfirmListener implements OnConfirmListener {

        @Override
        public void onConfirm(int shortcut, Intent data) {
            if (mFunctionKeyDialog != null && mFunctionKeyDialog.isShowing()) {
                mFunctionKeyDialog.dismiss();
                mFunctionKeyDialog = null;
            }
            updateFuncionKeyListView(shortcut, data);
            mShortcutMap.put(shortcut, data);
        }

    }

    public void updateFuncionKeyListView(int shortcut, Intent data) {
        HashMap<String, Object> map = mFuncitonKeyList.get(mSelectedPosition);
        map.put(Constant.ITEM_FUNCTION, data.getStringExtra(FunctionKeyDialog.EXTRA_TITLE));
        mAdapter.notifyDataSetChanged();
    }

    private void updateShortcut(int shortcut, Intent intent) {
        saveCompentName(shortcut, intent
                .getComponent().flattenToShortString());
    }

    @Override
    public void onLeftKeyPress() {
        confirm();
        finish();
    }

    private void confirm() {
        Iterator<Map.Entry<Integer, Object>> entries = mShortcutMap.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<Integer, Object> entry = entries.next();
            int shortcut = entry.getKey();
            Intent intent = (Intent) entry.getValue();
            updateShortcut(shortcut, intent);
        }
    }

    @Override
    public void onMiddleKeyPress() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onRightKeyPress() {
        launchActivity(OtherSettingActivity.class);
    }

    private synchronized void refreshShortcuts() {
        mFuncitonKeyList.clear();
        for (int i = 0; i < DPAD_KEYS.length; i++) {
            ComponentName compentName = getCompentName(DPAD_KEYS[i]);
            String title = getApplicationName(compentName);
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put(Constant.ITEM_SHORTCUT, getString(DPAD_KEYS_VALUE[i]));
            map.put(Constant.ITEM_FUNCTION, title == null ? "" : title);
            mFuncitonKeyList.add(map);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void saveCompentName(int shortCut, String compentName) {
        String keyName = null;
        switch (shortCut) {
            case DPAD_UP:
                keyName = Settings.Global.DIRECT_KEY_FUN_UP;
                break;
            case DPAD_DOWN:
                keyName = Settings.Global.DIRECT_KEY_FUN_DOWN;
                break;
            case DPAD_LEFT:
                keyName = Settings.Global.DIRECT_KEY_FUN_LEFT;
                break;
            case DPAD_RIGHT:
                keyName = Settings.Global.DIRECT_KEY_FUN_RIGHT;
                break;
            default:
                break;
        }
        Settings.Global.putString(getContentResolver(), keyName, compentName);
    }

    public ComponentName getCompentName(int shortCut) {
        String compentName = null;
        switch (shortCut) {
            case DPAD_UP:
                compentName = Settings.Global.getString(getContentResolver(),
                        Settings.Global.DIRECT_KEY_FUN_UP);
                break;
            case DPAD_DOWN:
                compentName = Settings.Global.getString(getContentResolver(),
                        Settings.Global.DIRECT_KEY_FUN_DOWN);
                break;
            case DPAD_LEFT:
                compentName = Settings.Global.getString(getContentResolver(),
                        Settings.Global.DIRECT_KEY_FUN_LEFT);
                break;
            case DPAD_RIGHT:
                compentName = Settings.Global.getString(getContentResolver(),
                        Settings.Global.DIRECT_KEY_FUN_RIGHT);
                break;
            default:
                break;
        }
        ComponentName componentName = ComponentName.unflattenFromString(compentName);
        return componentName;
    }

    public String getApplicationName(ComponentName componentName) {
        ActivityInfo activityInfo = null;
        try {
            activityInfo = mPackageManager.getActivityInfo(componentName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String applicationName = (String) activityInfo.loadLabel(mPackageManager);
        return applicationName;
    }

}
