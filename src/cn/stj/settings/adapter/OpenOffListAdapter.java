
package cn.stj.settings.adapter;

import java.util.HashMap;
import java.util.List;

import android.R.integer;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import cn.stj.settings.R;

/**
 * open and off list adapter
 * 
 * @author jackey
 */
public class OpenOffListAdapter extends MBaseAdapter<String> {

    private static final String TAG = OpenOffListAdapter.class.getSimpleName();
    // save open and off status ;
    private HashMap<Integer, Boolean> mOpenOffStatusMap = new HashMap<Integer, Boolean>();
    private int mSelectedPosition = -1;

    public OpenOffListAdapter(Context context, List<String> datas) {
        super(context, datas);
        initOpenOffStatusMap();
    }

    private void initOpenOffStatusMap() {
        for (int i = 0; i < getCount(); i++) {
            mOpenOffStatusMap.put(i, false);
        }
    }

    @Override
    public View createView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = mInflater.inflate(R.layout.setting_openoff_list_item_view,
                    null);
            holder.openOffRadioButton = (RadioButton) view
                    .findViewById(R.id.openoff_item_rbtn);
            holder.openOffTextView = (TextView) view
                    .findViewById(R.id.openoff_item_tv_text);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        Log.d(TAG, " openoffStatus:" + mOpenOffStatusMap.get(position));
        holder.openOffRadioButton.setChecked(mOpenOffStatusMap.get(position) == null ? false
                : mOpenOffStatusMap.get(position));
        holder.openOffTextView.setText(mDatas.get(position));
        if (mSelectedPosition == position) {
            holder.openOffTextView.setSelected(true);
        } else {
            holder.openOffTextView.setSelected(false);
        }
        return view;
    }

    private class ViewHolder {
        RadioButton openOffRadioButton;
        TextView openOffTextView;
    }

    public HashMap<Integer, Boolean> getmOpenOffStatusMap() {
        return mOpenOffStatusMap;
    }

    public void updateOpenOffStatusMap(int position, boolean status) {
        for (int i = 0; i < getCount(); i++) {
            if (i == position) {
                mOpenOffStatusMap.put(i, status);
            } else {
                mOpenOffStatusMap.put(i, false);
            }
        }
    }

    public void setSelectedItemPosition(int position) {
        mSelectedPosition = position;
        notifyDataSetChanged();
    }

}
