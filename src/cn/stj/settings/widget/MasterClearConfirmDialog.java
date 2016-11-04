
package cn.stj.settings.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import cn.stj.settings.R;

import java.util.ArrayList;

public class MasterClearConfirmDialog extends BaseDialog {
    private static final String TAG = MasterClearConfirmDialog.class.getSimpleName();
    private Context mContext;
    protected OnConfirmListener listener;

    public MasterClearConfirmDialog(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected View onLoadView(Bundle saveInstanceState) {
        View dialogView = LayoutInflater.from(mContext).inflate(
                R.layout.masterclear_confirm_dialog, null);
        return dialogView;
    }

    @Override
    protected void onLoadData(Bundle saveInstanceState) {
//        setOnKeyListener(new DialogOnKeyListener());
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

    @Override
    protected void onConfirm() {
        if (this.listener != null) {
            this.listener.onConfirm();
        }
    }

    public void setOnConfirmListener(OnConfirmListener listener) {
        this.listener = listener;
    }

    public interface OnConfirmListener {
        public void onConfirm();
    }

    @Override
    protected void OnCancel() {
        this.dismiss();
    }

}
