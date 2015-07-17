package cui.litang.phoneguard.service;

import cui.litang.phoneguard.R;
import cui.litang.phoneguard.db.TelAreaQueryUtils;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class TelAreaService extends Service {

	protected static final String TAG = "TelAreaService";
	private TelephonyManager tm;
	private View view;
	private WindowManager wm;
	private MyPhoneStateListener myPhoneStateListener;
	private OutCallReceiver mReceiver;
	private LayoutParams lp;
	private SharedPreferences sp;
	


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
	
	long[] mHits = new long[2];
	
	/**
	 * 自定义来电弹窗Toast
	 * @param address 地址
	 */
	public void myToast(String area) {
		view =   View.inflate(this,R.layout.toast_area, null);
		addListenerForView();
		TextView tv_area = (TextView) view.findViewById(R.id.tv_area);
		int [] ids = {R.drawable.call_locate_white,R.drawable.call_locate_orange,R.drawable.call_locate_blue
			    ,R.drawable.call_locate_gray,R.drawable.call_locate_green};
		
		sp = getSharedPreferences("config", MODE_PRIVATE);
		int which = sp.getInt("which", 0);
		view.setBackgroundResource(ids[which]);
		tv_area.setText(area);
		
		lp = new WindowManager.LayoutParams();
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
		lp.gravity = Gravity.TOP+Gravity.LEFT;
		lp.x= sp.getInt("lastX", 0);
		lp.y= sp.getInt("lastY", 0);
		lp.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		lp.format = PixelFormat.TRANSLUCENT;
		//lp.type = WindowManager.LayoutParams.TYPE_TOAST;
		lp.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
		
		wm.addView(view, lp);
		
		
		
	}


	private void addListenerForView() {
		//给view添加点击监听
		view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				System.arraycopy(mHits, 1, mHits, 0, mHits.length-1);
				mHits[mHits.length - 1] = SystemClock.uptimeMillis();
				if(mHits[0]>=SystemClock.uptimeMillis()-500){
					//双击事件
					lp.x= wm.getDefaultDisplay().getWidth()/2-view.getWidth()/2;
					wm.updateViewLayout(view, lp);
					Editor editor = sp.edit();
					editor.putInt("lastX", lp.x);
					editor.commit();
				}
			}
		});
		
		//给view设置一个触摸的监听器
		view.setOnTouchListener(new OnTouchListener() {
			
			int startX;
			int startY;
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						startX = (int) event.getRawX();
						startY = (int) event.getRawY();
						Log.i(TAG, "手指摸到控件");
						break;
					
					case MotionEvent.ACTION_MOVE:
						int movingRawX = (int) event.getRawX();
						int movingRawY = (int) event.getRawY();
						int dx = movingRawX - startX;
						int dy = movingRawY - startY;
						Log.i(TAG, "手指在控件上移动");
						lp.x += dx;
						lp.y += dy;
						
						//边界问题   cuilitang:在4.1.2上测试并未发现这个问题，或许是为了兼容吧。
						//左
						if(lp.x < 0){
							lp.x = 0;
						}
						//上
						if(lp.y<0){
							lp.y = 0;
						}
						//右
						if (lp.x>(wm.getDefaultDisplay().getWidth()-view.getWidth())) {
							lp.x= (wm.getDefaultDisplay().getWidth()-view.getWidth());
						}
						//下
						if(lp.y>(wm.getDefaultDisplay().getHeight()-view.getHeight())){
							lp.y = (wm.getDefaultDisplay().getHeight()-view.getHeight());
						}
						
						wm.updateViewLayout(view, lp);
						//重新初始起始位置
						startX = (int) event.getRawX();
						startY = (int) event.getRawY();
						break;
						
					case MotionEvent.ACTION_UP:
						Log.i(TAG, "手指离开控件");
						Editor editor = sp.edit();
						editor.putInt("lastX", lp.x);
						editor.putInt("lastY", lp.y);
						editor.commit();
						break;
				}
				
				return false;//不要让父控件 父布局响应触摸事件了。
			}
		});
		
		
	}
	
	
	

}
