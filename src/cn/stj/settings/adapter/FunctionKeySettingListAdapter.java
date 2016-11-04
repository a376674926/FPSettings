
package cn.stj.settings.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.stj.settings.Constant;
import cn.stj.settings.R;

import java.util.HashMap;
import java.util.List;

public class FunctionKeySettingListAdapter extends MBaseAdapter<HashMap<String, Object>> {

    private static final String TAG = FunctionKeySettingListAdapter.class.getSimpleName();
    private int mSelectedPosition = -1;

    public FunctionKeySettingListAdapter(Context context, List<HashMap<String, Object>> datas) {
        super(context, datas);
    }

    @Override
    public View createView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = mInflater.inflate(R.layout.function_key_setting_item_view, null);
            holder.itemIndex = ((TextView) view
                    .findViewById(R.id.function_key_item_tv_index));
            holder.itemKeyText = (TextView) view
                    .findViewById(R.id.function_key_item_tv_key);
            holder.itemFunctionText = (TextView) view
                    .findViewById(R.id.function_key_item_tv_function);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.itemIndex.setText(String.valueOf(position + 1));
        HashMap<String, Object> map = mDatas.get(position);
        holder.itemKeyText.setText(map.get(Constant.ITEM_SHORTCUT).toString());
        holder.itemFunctionText.setText(map.get(Constant.ITEM_FUNCTION).toString());
        if(mSelectedPosition == position){
            holder.itemFunctionText.setSelected(true) ;
        }else{
            holder.itemFunctionText.setSelected(false) ;
        }
        return view;
    }

    private class ViewHolder {
        TextView itemIndex;
        TextView itemKeyText;
        TextView itemFunctionText;
    }

    public void setSelectedItemPosition(int position) {
        mSelectedPosition = position;
        notifyDataSetChanged();
    }

}
