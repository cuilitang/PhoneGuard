package cui.litang.phoneguard.service;

import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.telephony.gsm.SmsMessage;
import android.util.Log;
import cui.litang.phoneguard.db.dao.BlackNumberDAO;

/**
 * 黑名单功能拦截短信和电话服务
 * @author Cuilitang
 * @Date 2015年7月19日
 */
public class BlackNumberService extends Service {

	private BlackNumberDAO dao;
	private TelephonyManager tm;
	private MyListener listener;
	private InnerSMSRecevier receiver;
	private static final String TAG = "BlackNumberService";

	@Override
	public IBinder onBind(Intent intent) {
		// Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		
		super.onCreate();
		dao = new BlackNumberDAO(this);
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		
		//拦截短信
		receiver = new InnerSMSRecevier();
		IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
		intentFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		registerReceiver(receiver, intentFilter);
		
		//拦截电话
		listener = new MyListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		unregisterReceiver(receiver);
		receiver = null;
	}
	
	private class InnerSMSRecevier extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			
			Object[] objects = (Object[]) intent.getExtras().get("pdus");
			for (Object object : objects) {
				SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) object);
				String sender = smsMessage.getOriginatingAddress();
				String mode = dao.getMode(sender);
				if("3".equals(mode)||"1".equals(mode)){
					Log.i(TAG,"拦截短信");
					abortBroadcast();
				}
				
				//演示代码。 实际中需要分词器（lucene）和敏感词词库数据库支持（类似号码查询归属地的那种 "免费领奖"："诈骗"）
				String body = smsMessage.getMessageBody();
				if(body.contains("fapiao")){
					//你的头发票亮的很  语言分词技术。
					Log.i(TAG,"拦截发票短信");
					abortBroadcast();
				}
			}
		}		
	}

	/**
	 * 来电监听器
	 * @author Cuilitang
	 * @Date 2015年7月19日
	 */
	private class MyListener extends PhoneStateListener{
		
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				
				String mode = dao.getMode(incomingNumber);
				if("3".equals(mode)||"2".equals(mode)){
					Log.i(TAG, "挂断电话，删除当前号码通话记录");
					endCall();
					Uri uri = Uri.parse("content://call_log/calls");
					getContentResolver().registerContentObserver(uri, true, new CallRecordsObserver(incomingNumber, new Handler()));
				}
				break;

			default:
				break;
			}
		}
		
		/**
		 * 挂断电话
		 */
		public void endCall() {
			
			try {
				Class clazz = BlackNumberService.class.getClassLoader().loadClass("android.os.ServiceManager");
				Method method = clazz.getDeclaredMethod("getService", String.class);
				IBinder ibinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);
				ITelephony.Stub.asInterface(ibinder).endCall();
			} catch (Exception e) {
				e.printStackTrace();
			} 
			
		}
		
		
		
	}
	
	private class CallRecordsObserver extends  ContentObserver{
		
		private String incomeNumber;
		
		/**
		 * 构造
		 * @param incomeNumber 
		 * @param handler
		 */
		public CallRecordsObserver(String incomeNumber,Handler handler) {
			super(handler);
			
			this.incomeNumber = incomeNumber;
		}
		
		/**
		 * This method is called when a content change occurs. 
		 * Subclasses should override this method to handle content changes. 
		 */
		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			
			Log.i(TAG,"数据库的内容变化了，产生了呼叫记录");
			getContentResolver().unregisterContentObserver(this);
			delteCallLog(incomeNumber);
			
			
			
		}
		
	}
	
	/**
	 * 删除呼入电话的呼叫记录
	 * @param incomeNumber 
	 */
	private void delteCallLog(String incomeNumber) {
		
		ContentResolver resolver = getContentResolver();
		Uri uri = Uri.parse("content://call_log/calls");
		resolver.delete(uri, "number=?", new String[]{incomeNumber});
		
		
	}
	
	
	
	
	
	

}
