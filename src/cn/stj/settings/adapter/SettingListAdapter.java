
package cn.stj.settings.adapter;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import cn.stj.settings.R;

public class SettingListAdapter extends MBaseAdapter<String> {

    private static final String TAG = SettingListAdapter.class.getSimpleName();
    private int mSelectedPosition = -1;

    public SettingListAdapter(Context context, List<String> datas) {
        super(context, datas);
    }

    @Override
    public View createView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = mInflater.inflate(R.layout.setting_list_item_view, null);
            holder.itemIndex = ((TextView) view
                    .findViewById(R.id.setting_list_item_tv_index));
            holder.itemText = (TextView) view
                    .findViewById(R.id.setting_list_item_tv_text);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.itemIndex.setText(String.valueOf(position + 1));
        holder.itemText.setText(mDatas.get(position));
        if (mSelectedPosition == position) {
            holder.itemText.setSelected(true);
        } else {
            holder.itemText.setSelected(false);
        }
        return view;
    }

    private class ViewHolder {
        TextView itemIndex;
        TextView itemText;
    }

    public void setSelectedItemPosition(int position) {
        mSelectedPosition = position;
        notifyDataSetChanged();
    }

}
