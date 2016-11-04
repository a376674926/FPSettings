package cn.stj.settings.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.view.Window;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import cn.stj.settings.R;
import cn.stj.settings.view.SettingView;

/**
 * 界面的基类
 * 
 * @author jackey
 * 
 */
public abstract class BaseActivity extends Activity implements
		View.OnClickListener,SettingView{

	private static final String TAG = BaseActivity.class.getSimpleName();

	private View mRootView;
	protected ViewStub mAboveViewStub;
	protected ViewStub mMiddleViewStub;
	protected ViewStub mBelowViewStub;
	private Button mInitLeftBtn;
	private Button mInitMidBtn;
	private Button mInitRightBtn;

	private Button mLeftBtn;
	private Button mMidBtn;
	private Button mRightBtn;
	private TextView mTopTitle;

	private View mInitTopTitle;
	private BottomKeyClickListener mBottomKeyClickListener;

	protected PopupWindow mDeletePop;
	private Button mOkBtn;
	private Button mNoBtn;
	
	/**
	 * show toast
	 * 
	 * @param msg 
	 *            
	 */
	protected void showToast(String msg) {
		showToast(msg, -1) ;
	}
	
	/**
	 * show toast
	 * 
	 * @param msg
	 *            
	 */
	protected void showToast(String msg,int gravity) {
		Toast toast = Toast.makeText(this, msg, Toast.LENGTH_LONG) ;
		if(gravity != -1){
			toast.setGravity(Gravity.CENTER, 0, 0) ;
			toast.show();
		}else{
			toast.show() ;
		}
	}

	/**
	 * launch Activity by class
	 * 
	 * @param pClass class type
	 *            
	 */
	protected void launchActivity(Class<?> pClass) {
		launchActivity(pClass, null);
	}

	/**
	 * launch Activity by class
	 * 
	 * @param pClass
	 * @param pBundle
	 */
	protected void launchActivity(Class<?> pClass, Bundle pBundle) {
		launchActivity(pClass, pBundle, -1);
	}

	/**
	 * launch Activity by class，contain bundle
	 * 
	 * @param pClass
	 * @param pBundle
	 */
	protected void launchActivity(Class<?> pClass, Bundle pBundle,
			int intentFlag) {
		Intent intent = new Intent(this, pClass);
		if (pBundle != null) {
			intent.putExtras(pBundle);
		}
		if (intentFlag != -1) {
			intent.addFlags(intentFlag);
		}
		startActivity(intent);
	}

	/**
	 * launch Activity by action
	 * 
	 * @param pAction
	 */
	protected void launchActivity(String pAction) {
		launchActivity(pAction, null);
	}

	/**
	 * launch Activity by action
	 * 
	 * @param pAction
	 * @param pBundle
	 */
	protected void launchActivity(String pAction, Bundle pBundle) {
		Intent intent = new Intent(pAction);
		if (pBundle != null) {
			intent.putExtras(pBundle);
		}
		startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		mRootView = LayoutInflater.from(this).inflate(R.layout.activity_base,
				null);
		setContentView(mRootView);

		mAboveViewStub = (ViewStub) findViewById(R.id.middle_list_above_viewstub);
		mMiddleViewStub = (ViewStub) findViewById(R.id.middle_list_middle_viewstub);
		mBelowViewStub = (ViewStub) findViewById(R.id.middle_list_below_viewstub);
		buildButtons();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	protected void setActivityBgDrawable(Drawable drawable) {
		mRootView.setBackgroundDrawable(drawable);
	}

	protected void setActivityBgResource(int resid) {
		mRootView.setBackgroundResource(resid);
	}

	protected void setTopTitleDrawable(Drawable drawable) {
		if (mInitTopTitle != null) {
			mInitTopTitle.setBackgroundDrawable(drawable);
		}
	}

	protected void setTopTitleBgResource(int resid) {
		if (mInitTopTitle != null) {
			mInitTopTitle.setBackgroundResource(resid);
		}
	}

	protected void setBottomButtonsDrawable(Drawable drawable) {
		RelativeLayout layout = (RelativeLayout) mRootView
				.findViewById(R.id.bottom_layout);
		layout.setBackgroundDrawable(drawable);
	}

	protected void setBottomButtonsResource(int resid) {
		RelativeLayout layout = (RelativeLayout) mRootView
				.findViewById(R.id.bottom_layout);
		layout.setBackgroundResource(resid);
	}

	private void buildButtons() {
		mLeftBtn = (Button) findViewById(R.id.bottom_left_button);
		mMidBtn = (Button) findViewById(R.id.bottom_middle_button);
		mRightBtn = (Button) findViewById(R.id.bottom_right_button);

		mLeftBtn.setOnClickListener(this);
		mMidBtn.setOnClickListener(this);
		mRightBtn.setOnClickListener(this);

		mTopTitle = (TextView) findViewById(R.id.top_title);
		
		setRightBtnText(getResources().getString(R.string.back)) ;
        setLeftBtnText(getResources().getString(R.string.option_ok)) ;
        setTopTitleText(getTitle().toString()) ;
	}

	protected void setLeftBtnText(String text) {
		if (TextUtils.isEmpty(text)) {
			mLeftBtn.setVisibility(View.GONE);
		} else {
			mLeftBtn.setVisibility(View.VISIBLE);
			mLeftBtn.setText(text);
		}

	}

	protected void setMidBtnText(String text) {
		if (TextUtils.isEmpty(text)) {
			mMidBtn.setVisibility(View.GONE);
		} else {
			mMidBtn.setVisibility(View.VISIBLE);
			mMidBtn.setText(text);
		}
	}

	protected void setRightBtnText(String text) {
		if (TextUtils.isEmpty(text)) {
			mRightBtn.setVisibility(View.GONE);
		} else {
			mRightBtn.setVisibility(View.VISIBLE);
			mRightBtn.setText(text);
		}
	}

	protected void setTopTitleText(String text) {
		if(TextUtils.isEmpty(text)){
			mTopTitle.setVisibility(View.GONE) ;
		}else{
			mTopTitle.setText(text);
			mTopTitle.setVisibility(View.VISIBLE) ;
		}
	}

	@Override
	public void onClick(View v) {

		if (mBottomKeyClickListener == null) {
			return;
		}

		switch (v.getId()) {
		case R.id.bottom_left_button:
			mBottomKeyClickListener.onLeftKeyPress();
			break;
		case R.id.bottom_middle_button:
			mBottomKeyClickListener.onMiddleKeyPress();
			break;
		case R.id.bottom_right_button:
			mBottomKeyClickListener.onRightKeyPress();
			break;
		default:
			break;
		}
	}

	public interface BottomKeyClickListener {

		public void onLeftKeyPress();

		public void onMiddleKeyPress();

		public void onRightKeyPress();
	}

	public void setBottomKeyClickListener(BottomKeyClickListener l) {
		if (l != null) {
			mBottomKeyClickListener = l;
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			mBottomKeyClickListener.onRightKeyPress() ;
			break ;
		case KeyEvent.KEYCODE_MENU:
			mBottomKeyClickListener.onLeftKeyPress() ;
			break ;
		default:
			break;
		}
		return false ;
	}
	
    public void onSuccess(HashMap<String, Object> respDatas){
    	
    };
	
	public void onFail() {
		
	};
	
}
