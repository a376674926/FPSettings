package cn.stj.settings.view;

import java.util.HashMap;
import java.util.Map;

public interface SettingView {

	public void onSuccess(HashMap<String, Object> respDatas) ;
	
	public void onFail() ;
}
