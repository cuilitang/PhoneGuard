package cui.litang.phoneguard.service;

import cui.litang.phoneguard.R;
import cui.litang.phoneguard.db.TelAreaQueryUtils;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class TelAreaService extends Service {

	private TelephonyManager tm;
	private View view;
	private WindowManager wm;
	private MyPhoneStateListener myPhoneStateListener;
	private OutCallReceiver mReceiver;
	


	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		myPhoneStateListener = new MyPhoneStateListener();
		tm.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
		
		mReceiver = new OutCallReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.intent.action.NEW_OUTGOING_CALL");
		registerReceiver(mReceiver, intentFilter);
		
		//自定义Toast时候使用
		wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		

		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		tm.listen(myPhoneStateListener, PhoneStateListener.LISTEN_NONE);
		myPhoneStateListener=null;
		
		unregisterReceiver(mReceiver);
		mReceiver = null;
	}
	
	/**
	 * 来电 监听器
	 * @author Cuilitang
	 * @Date 2015年7月16日
	 */
	private class MyPhoneStateListener extends PhoneStateListener{
		
		/**
		 * Callback invoked when device call state changes.
		 */
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				String area = TelAreaQueryUtils.queryNumber(incomingNumber);
				//Toast.makeText(getApplicationContext(), area, Toast.LENGTH_LONG).show();
				myToast(area);
				break;
				 
				case TelephonyManager.CALL_STATE_IDLE:
					if(view!=null){
						wm.removeView(view);
					}
					break;
					 

			default:
				break;
			}
		}
		
		
	}
	
	
	
	/**
	 * 外拨电话 广播接收器
	 * @author Cuilitang
	 * @Date 2015年7月16日
	 */
	class OutCallReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			String tel = getResultData();
			String area = TelAreaQueryUtils.queryNumber(tel);
			//Toast.makeText(context, area, 1).show();
			myToast(area);
		}
		
	}
	
	/**
	 * 自定义来电弹窗Toast
	 * @param address 地址
	 */
	public void myToast(String area) {
		view =   View.inflate(this,R.layout.toast_area, null);
		TextView tv_area = (TextView) view.findViewById(R.id.tv_area);
		int [] ids = {R.drawable.call_locate_white,R.drawable.call_locate_orange,R.drawable.call_locate_blue
			    ,R.drawable.call_locate_gray,R.drawable.call_locate_green};
		
		SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
		int which = sp.getInt("which", 0);
		view.setBackgroundResource(ids[which]);
		tv_area.setText(area);
		
		LayoutParams lp = new WindowManager.LayoutParams();
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
		lp.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE|
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		lp.format = PixelFormat.TRANSLUCENT;
		lp.type = WindowManager.LayoutParams.TYPE_TOAST;
		wm.addView(view, lp);
		
		
	}
	

}
