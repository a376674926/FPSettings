
package cn.stj.settings.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 适配器基类
 * 
 * @author jackey
 * @param <T>
 */
public abstract class MBaseAdapter<T> extends BaseAdapter {

    protected Context mContext;
    protected List<T> mDatas;
    protected LayoutInflater mInflater;

    /**
     * 显示Toast提示框
     * 
     * @param msg 显示提示的字符串
     */
    protected void showToastMsg(String msg)
    {
        Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
    }

    public MBaseAdapter(Context context, List<T> datas) {
        mContext = context;
        this.mDatas = datas;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        return createView(position, view, parent);
    }

    public abstract View createView(int position, View view, ViewGroup parent);

    public List<T> getDatas() {
        return mDatas;
    }

    public T getData(int position) {
        return mDatas.get(position);
    }

    public void setDatas(List<T> datas) {
        this.mDatas = datas;
    }

    public void clear() {
        mDatas.clear();
        notifyDataSetChanged();
    }

    public void remove(int position) {
        mDatas.remove(position);
        notifyDataSetChanged();
    }
}
