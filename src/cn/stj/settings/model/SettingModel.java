package cn.stj.settings.model;

import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;

import java.util.List;
import java.util.Locale;

public interface SettingModel {
	
	/**
	 * set airplane mode
	 * @param isOpen
	 */
	public void setAirplaneMode(Context context,boolean isOpen) ;
	
	/**
	 * set data connection
	 * @param isOpen
	 */
	public void setDataConnection(Context context,boolean isOpen);
	
	/**
     * set battery percentage
     * @param isOpen
     */
    public void setBatteryPercentage(Context context,boolean isOpen);
	
	/**
	 * set SOS help
	 */
	public void setSOSHelp() ;
	
	/**
	 * set brightness
	 * @param brightness
	 */
	public void setBrightness(Context context,int brightness) ;
	
	/**
	 * set volume
	 * @param volume
	 */
	public void setRingVolume(Context context,int volume) ;
	
	/**
	 * set sleepTime
	 * @param sleepTime
	 */
	public void setSleepTime(Context context,long sleepTime) ;
	
	/**
	 * set ring
	 * @param position
	 */
	public void setRingtone(Context context,int type,int position) ;
	
    /**
     * get ringtone title list
     * @param type
     * @return
     */
    public void getRingtoneTitleList(Context context, int type, OnGetRingtoneFinish onGetRingtoneFinish) ;
    
    /**
     * set auto time state
     * @param context
     * @param isOpen
     */
    public void setAutotimeState(Context context, boolean isOpen) ;
    
	/**
	 * set dateTime
	 * @param dateTime
	 */
	public void setDateTime(Context context,long dateTime,OnSetFinish onSetFinish) ;
	
	/**
     * set hour format
     * @param isOpen
     */
    public void setHourFormat(Context context, boolean isOpen) ;
    
    /**
     * set call voice broadcast
     * @param context
     * @param isOpen
     */
    public void setCallVoiceBroadcast(Context context,boolean isOpen) ;
    
    /**
     * set time voice broadcast
     * @param context
     * @param isOpen
     */
    public void setTimeVoiceBroadcast(Context context,boolean isOpen) ;
    
    /**
     * set key voice broadcast
     * @param context
     * @param isOpen
     */
    public void setKeyVoiceBroadcast(Context context,boolean isOpen) ;
	
	/**
	 * set Locale
	 * @param locale
	 */
	public void setLanguage(Locale locale) ;
	
	/**
	 * set master reset
	 * @param
	 */
	public void setMasterReset(Context context) ;
	
	/**
	 * set function key
	 * @param context
	 * @param shortcut
	 * @param title
	 * @param folder
	 * @param ordering
	 * @param intent
	 */
	public void setFunctionKey(Context context,int shortcut,String title, String folder,int ordering,Intent intent) ;
	
	public static interface OnSetFinish {

		/**
		 * execute onFinish after Setup is complete
		 * @param isSetSuccess
		 */
		public void onFinish(boolean isSetSuccess);
	}
	
    public static interface OnGetRingtoneFinish {
        
        /**
         * execute onGetRingToneTitleFinish after get ringtone
         * @param ringtonetitle 
         */
        public void onGetRingToneTitleFinish(List<String> ringtoneTitles);
    }

}
