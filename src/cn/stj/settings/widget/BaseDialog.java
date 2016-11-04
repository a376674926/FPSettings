
package cn.stj.settings.widget;

import cn.stj.settings.R;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * 自定义dialog
 */
public abstract class BaseDialog extends Dialog {
    protected Context mContext;
    protected View mView;
    protected Button confirm_btn;
    protected Button cancel_btn;

    public BaseDialog(Context context) {
        super(context, R.style.CustomDialogStyle);
        this.mContext = context;
    }

    /**
     * 加载自定义View
     * 
     * @param saveInstanceState
     * @return
     */
    protected abstract View onLoadView(Bundle saveInstanceState);

    protected abstract void onLoadData(Bundle saveInstanceState);

    /**
     * 确定按钮响应函数
     */
    protected abstract void onConfirm();

    /**
     * 取消按钮响应函数
     */
    protected abstract void OnCancel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = onLoadView(savedInstanceState);
        confirm_btn = (Button) mView.findViewById(R.id.confirm_btn);
        cancel_btn = (Button) mView.findViewById(R.id.cancel_btn);
        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onConfirm();
            }
        });
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnCancel();
            }

        });
        setContentView(mView);
        onLoadData(savedInstanceState);
    }

}
