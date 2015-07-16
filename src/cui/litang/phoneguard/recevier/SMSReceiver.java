package cui.litang.phoneguard.recevier;

import cui.litang.phoneguard.R;
import cui.litang.phoneguard.service.GPSService;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

/**
 * 安全号码发过来短信之后根据短信内容执行相应操作
 * @author Cuilitang
 * @Date 2015年7月14日
 */
public class SMSReceiver extends BroadcastReceiver{

	private static final String TAG = "SMSReceiver";
	SharedPreferences sp ;
	private DevicePolicyManager dpm;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		boolean isStart = sp.getBoolean("start", false);
		String telNum = sp.getString("telnum", null);
		
		Object [] objects = (Object[]) intent.getExtras().get("pdus");
		for (int i = 0; i < objects.length; i++) {
			SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) objects[i]);
			String sender = smsMessage.getOriginatingAddress();
			String body = smsMessage.getMessageBody();
			if (sender.contains(telNum)) {
				if("send_jps".equals(body)){
					Log.i(TAG, "send_jps");
					//启动jps服务
					Intent intentGPSService = new Intent(context,GPSService.class);
					context.startService(intentGPSService);
					String position = sp.getString("position", null);
					if(TextUtils.isEmpty(position)){
						SmsManager.getDefault().sendTextMessage(sender, null, "location getting...", null, null);
									
					}else{
						SmsManager.getDefault().sendTextMessage(sender, null, position, null, null);
					}

					abortBroadcast();
				}else if("delete_data".equals(body)){
					deleteData(context);
					Log.i(TAG, "delete_data");
					
					abortBroadcast();
				}else if("lock_srceen".equals(body)){
					lockSrceen(context);
					Log.i(TAG, "lock_srceen");
					abortBroadcast();
				}else if("play_alarm".equals(body)){
					playAlarm(context);
					Log.i(TAG, "play_alarm");
					abortBroadcast();

				}
				
				
			}
			
			
			
		}
		
		
	}

	/**
	 * 播放报警音乐
	 */
	private void playAlarm(Context context) {
		MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);
		player.setLooping(false);
		player.setVolume(1.0f, 1.0f);
		player.start();
		
	}

	/**
	 * 开启管理员功能
	 */
	private void openAdmin(Context context) {
		dpm = (DevicePolicyManager) context.getSystemService(context.DEVICE_POLICY_SERVICE);
		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		ComponentName cn = new ComponentName(context,MyAdmin.class);
		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, cn);
		intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "开启设备管理功能方能远程删除数据和锁屏");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
		

	}
	
	/**
	 * 锁屏
	 */
	private void lockSrceen(Context context) {
		openAdmin(context);
		ComponentName cn = new ComponentName(context,MyAdmin.class);
		if(dpm.isAdminActive(cn)){
			dpm.lockNow();
			dpm.resetPassword("123", 0);
		}else {
			Toast.makeText(context, "还没有打开设备管理权限", Toast.LENGTH_SHORT).show();
			return;
		}
		
		
	}
	
	/**
	 * 删除数据
	 */
	private void deleteData(Context context) {
		openAdmin(context);
		ComponentName cn = new ComponentName(context,MyAdmin.class);
		if(dpm.isAdminActive(cn)){
			dpm.wipeData(dpm.WIPE_EXTERNAL_STORAGE);//删除SD卡数据
			dpm.wipeData(0);//恢复出厂设置
			
		}else {
			Toast.makeText(context, "还没有打开设备管理权限", Toast.LENGTH_SHORT).show();
			return;
		}
		
		
	}

}
