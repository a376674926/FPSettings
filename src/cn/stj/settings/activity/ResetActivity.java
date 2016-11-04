
package cn.stj.settings.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import cn.stj.settings.R;
import cn.stj.settings.presenter.SettingPresenter;
import cn.stj.settings.presenter.SettingPresenterImpl;
import cn.stj.settings.widget.MasterClearConfirmDialog;
import cn.stj.settings.widget.MasterClearConfirmDialog.OnConfirmListener;

/**
 * master reset activity
 * 
 * @author jackey
 */
public class ResetActivity extends BaseActivity implements BaseActivity.BottomKeyClickListener {

    private SettingPresenter mSettingPresenter;
    private MasterClearConfirmDialog mMasterClearConfirmDialog;
    private static final String TAG = "ResetActivity" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAboveViewStub.setLayoutResource(R.layout.activity_reset);
        setBottomKeyClickListener(this);

        initView();

        mSettingPresenter = new SettingPresenterImpl(this);

    }

    private void initView() {
        View resetView = mAboveViewStub.inflate();
    }

    @Override
    public void onLeftKeyPress() {
        showMasterClearConfirmDialog();
    }

    private void showMasterClearConfirmDialog() {
        mMasterClearConfirmDialog = new MasterClearConfirmDialog(this);
        mMasterClearConfirmDialog.show();
        mMasterClearConfirmDialog.setOnConfirmListener(new MasterClearConfirmListener());
    }

    private class MasterClearConfirmListener implements OnConfirmListener {

        @Override
        public void onConfirm() {
            mSettingPresenter.setMasterReset(ResetActivity.this);
            mMasterClearConfirmDialog.dismiss();
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
}
