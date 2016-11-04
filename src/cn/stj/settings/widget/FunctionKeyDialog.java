
package cn.stj.settings.widget;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cn.stj.settings.R;

public class FunctionKeyDialog extends BaseDialog {
    private static final String TAG = FunctionKeyDialog.class.getSimpleName();
    private Context mContext;
    private ListView mListView;
    protected OnConfirmListener listener;
    private FunctionKeyAdapter mAdapter;
    private int mSelectedPosition;
    private int mShortcut;
    private String mFunctionName;
    /** Intent used to get all the activities that are launch-able */
    private static Intent sLaunchIntent;
    /** Extra in the returned intent from this activity. */
    public static final String EXTRA_TITLE = "com.android.settings.quicklaunch.TITLE";
    // List adapter stuff
    private static final String KEY_TITLE = "TITLE";
    private static final String KEY_RESOLVE_INFO = "RESOLVE_INFO";
    private Handler mUiHandler = new Handler();
    private ArrayList<ResolveInfo> mResolveList = new ArrayList<ResolveInfo>();
    private static final ArrayList<String> APP_FILTER = new ArrayList<String>();
    // 触宝输入法 系统设置 视频 视频播放器 通话记录 通讯录 信息 闹钟
    private static final String FILTER_PKG_NAME_TOUCHPAL = "com.emoji.keyboard.touchpal/com.cootek.smartinput5.GateActivity";
    private static final String FILTER_PKG_NAME_SETTINGS = "com.android.settings/.Settings";
    private static final String FILTER_PKG_NAME_VIDEOBROWSER = "com.android.music/.VideoBrowserActivity";
    private static final String FILTER_PKG_NAME_VIDEO = "com.android.gallery3d/com.sprd.gallery3d.app.VideoActivity";
    private static final String FILTER_PKG_NAME_CALLLOG = "com.android.dialer/.calllog.CallLogActivity";
    private static final String FILTER_PKG_NAME_CONTACTS = "com.android.contacts/.activities.PeopleActivity";
    private static final String FILTER_PKG_NAME_MESSAGING = "com.android.messaging/.ui.conversationlist.ConversationListActivity";
    private static final String FILTER_PKG_NAME_ALARMCLOCK = "cn.stj.alarmclock/.activity.AlarmSettingActivity";

    static {
        APP_FILTER.add(FILTER_PKG_NAME_TOUCHPAL);
        APP_FILTER.add(FILTER_PKG_NAME_SETTINGS);
        APP_FILTER.add(FILTER_PKG_NAME_VIDEOBROWSER);
        APP_FILTER.add(FILTER_PKG_NAME_VIDEO);
        APP_FILTER.add(FILTER_PKG_NAME_CALLLOG);
        APP_FILTER.add(FILTER_PKG_NAME_CONTACTS);
        APP_FILTER.add(FILTER_PKG_NAME_MESSAGING);
        APP_FILTER.add(FILTER_PKG_NAME_ALARMCLOCK);
    }

    private PackageManager mPackageManager;

    public FunctionKeyDialog(Context context) {
        super(context);
        mContext = context;
        mPackageManager = mContext.getPackageManager();
    }

    @Override
    protected View onLoadView(Bundle saveInstanceState) {
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.functionkey_dialog, null);
        return dialogView;
    }

    @Override
    protected void onLoadData(Bundle saveInstanceState) {
        mListView = (ListView) mView.findViewById(R.id.functionkey_dialog_listview);
        updateListAndAdapter();

        mListView.setItemsCanFocus(true);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick mSelectedPosition:" + mSelectedPosition);
                if (position >= mResolveList.size())
                    return;
                mSelectedPosition = position;
                mAdapter.selectPosition = mSelectedPosition;
                mAdapter.notifyDataSetChanged();
            }
        });
        // add by hhj@20160722
        setOnKeyListener(new DialogOnKeyListener());

    }

    private class DialogOnKeyListener implements DialogInterface.OnKeyListener {

        @Override
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                cancel_btn.performClick();
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_MENU) {
                // 菜单键
                confirm_btn.performClick();
                return true;
            }
            return false;
        }

    }

    private static Intent getIntentForResolveInfo(ResolveInfo info, String action) {
        Intent intent = new Intent(action);
        ActivityInfo ai = info.activityInfo;
        intent.setClassName(ai.packageName, ai.name);
        return intent;
    }

    @Override
    protected void onConfirm() {
        if (this.listener != null) {
            ResolveInfo info = mResolveList.get(mSelectedPosition);
            // We can go ahead and return the clicked info's intent
            Intent intent = getIntentForResolveInfo(info, Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            // Put our information
            intent.putExtra(EXTRA_TITLE, getResolveInfoTitle(info));
            this.listener.onConfirm(mShortcut, intent);

        }
    }

    public void setOnConfirmListener(OnConfirmListener listener) {
        this.listener = listener;
    }

    public interface OnConfirmListener {
        public void onConfirm(int shortcut, Intent data);
    }

    @Override
    protected void OnCancel() {
        this.dismiss();
    }

    private class FunctionKeyAdapter extends BaseAdapter {

        private int selectPosition = 0;
        private ArrayList<Map<String, ?>> newAdapterList = new ArrayList<Map<String, ?>>();

        public FunctionKeyAdapter(ArrayList<Map<String, ?>> newAdapterList) {
            super();
            this.newAdapterList = newAdapterList;
        }

        @Override
        public int getCount() {
            return newAdapterList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return newAdapterList.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.functionkey_dialog_item_view, null);
                holder.title_tv = (TextView) convertView
                        .findViewById(R.id.functionkey_dialog_item_tv);
                holder.title_rbtn = (RadioButton) convertView
                        .findViewById(R.id.functionkey_dialog_item_rbtn);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.title_tv.setText(newAdapterList.get(position).get(KEY_TITLE).toString());
            holder.title_rbtn.setClickable(false);
            if (selectPosition == position) {
                holder.title_rbtn.setChecked(true);
            } else {
                holder.title_rbtn.setChecked(false);
            }
            return convertView;
        }

        public class ViewHolder {
            TextView title_tv;
            RadioButton title_rbtn;
        }
    }

    private FunctionKeyAdapter createResolveAdapter(ArrayList<Map<String, ?>> list) {
        FunctionKeyAdapter functionKeyAdapter = new FunctionKeyAdapter(list);
        return functionKeyAdapter;
    }

    /**
     * This should be called from the UI thread.
     */
    private void updateListAndAdapter() {
        // Get the activities in a separate thread
        new Thread("data updater") {
            @Override
            public void run() {
                synchronized (FunctionKeyDialog.this) {
                    /*
                     * Don't touch any of the lists that are being used by the
                     * adapter in this thread!
                     */
                    ArrayList<ResolveInfo> newResolveList = new ArrayList<ResolveInfo>();
                    ArrayList<Map<String, ?>> newAdapterList = new ArrayList<Map<String, ?>>();

                    fillResolveList(newResolveList);
                    Collections.sort(newResolveList,
                            new ResolveInfo.DisplayNameComparator(mContext.getPackageManager()));
                    filterResolveList(newResolveList);
                    fillAdapterList(newAdapterList, newResolveList);

                    updateAdapterToUseNewLists(newAdapterList, newResolveList);
                }
            }

        }.start();
    }

    private void updateAdapterToUseNewLists(final ArrayList<Map<String, ?>> newAdapterList,
            final ArrayList<ResolveInfo> newResolveList) {
        // Post this back on the UI thread
        mUiHandler.post(new Runnable() {
            public void run() {
                /*
                 * SimpleAdapter does not support changing the lists after it
                 * has been created. We just create a new instance.
                 */
                mAdapter = createResolveAdapter(newAdapterList);
                for (int i = 0; i < newAdapterList.size(); i++) {
                    Map<String, ?> map = newAdapterList.get(i);
                    if (mFunctionName.equals(map.get(KEY_TITLE).toString())) {
                        mSelectedPosition = i;
                    }

                }
                mAdapter.selectPosition = mSelectedPosition;
                mListView.setSelected(true);
                mResolveList = newResolveList;
                mListView.setAdapter(mAdapter);
                mListView.setSelection(mSelectedPosition);
            }
        });
    }

    /**
     * Gets all activities matching our current display mode.
     * 
     * @param list The list to fill.
     */
    private void fillResolveList(List<ResolveInfo> list) {
        ensureIntents();
        PackageManager pm = mContext.getPackageManager();
        list.clear();

        list.addAll(pm.queryIntentActivities(sLaunchIntent, 0));

    }

    private void fillAdapterList(List<Map<String, ?>> list,
            List<ResolveInfo> resolveList) {
        list.clear();
        int resolveListSize = resolveList.size();
        for (int i = 0; i < resolveListSize; i++) {
            ResolveInfo info = resolveList.get(i);

            /*
             * Simple adapter craziness. For each item, we need to create a map
             * from a key to its value (the value can be any object--the view
             * binder will take care of filling the View with a representation
             * of that object).
             */
            Map<String, Object> map = new TreeMap<String, Object>();
            map.put(KEY_TITLE, getResolveInfoTitle(info));
            map.put(KEY_RESOLVE_INFO, info);
            list.add(map);
        }
    }

    private boolean isInFilter(ResolveInfo info) {
        if (info == null) {
            return false;
        }

        if (TextUtils.isEmpty(info.activityInfo.packageName)
                || TextUtils.isEmpty(info.activityInfo.name)) {
            return false;
        }

        ComponentName componentName = new ComponentName(info.activityInfo.packageName,
                info.activityInfo.name);
        String componentStr = componentName.flattenToShortString();
        return APP_FILTER.contains(componentStr);
    }

    /** Get the title for a resolve info. */
    private String getResolveInfoTitle(ResolveInfo info) {
        CharSequence label = info.loadLabel(mContext.getPackageManager());
        if (label == null)
            label = info.activityInfo.name;
        return label != null ? label.toString() : null;
    }

    private void ensureIntents() {
        if (sLaunchIntent == null) {
            sLaunchIntent = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER);
        }
    }

    public void setmShortcut(int mShortcut) {
        this.mShortcut = mShortcut;
    }

    public void setmFunctionName(String mFunctionName) {
        this.mFunctionName = mFunctionName;
    }

    private void filterResolveList(ArrayList<ResolveInfo> newResolveList) {
        ArrayList<ResolveInfo> resolveList = new ArrayList<ResolveInfo>();
        for (int i = 0; i < newResolveList.size(); i++) {
            ResolveInfo info = newResolveList.get(i);
            if (!isInFilter(info)) {
                resolveList.add(info);
            }
        }
        newResolveList.clear();
        newResolveList.addAll(resolveList);
    }

}
