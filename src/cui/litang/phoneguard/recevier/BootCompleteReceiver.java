package cui.litang.phoneguard.recevier;

import cui.litang.phoneguard.MyApplication;
import cui.litang.phoneguard.service.AppLockService;
import cui.litang.phoneguard.service.BlackNumberService;
import cui.litang.phoneguard.service.TelAreaService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.sax.StartElementListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class BootCompleteReceiver extends  BroadcastReceiver{

	private static final String TAG = "BootCompleteReceiver";
	private SharedPreferences sp;
	private TelephonyManager tm;

	@Override
	public void onReceive(Context context, Intent intent) {
		
		Toast.makeText(context, "手机启动完成", Toast.LENGTH_SHORT).show();
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		
		//启动程序锁服务
		if(sp.getBoolean(MyApplication.ISEXEAPPLOCK, false)){
			context.startService(new Intent(context,AppLockService.class));
			Log.i(TAG, "启动黑名单服务");
			Toast.makeText(context, "启动黑名单服务", Toast.LENGTH_SHORT).show();
		}
		
		//启动黑名单服务
		if(sp.getBoolean(MyApplication.ISEXEBLACKLIST, false)){
			context.startService(new Intent(context,BlackNumberService.class));
			Log.i(TAG, "启动程序锁服务");
			Toast.makeText(context, "启动程序锁服务", Toast.LENGTH_SHORT).show();
		}
		
		//启动号码归属地吐司服务
		if(sp.getBoolean(MyApplication.ISEXEAPPLOCK, false)){
			context.startService(new Intent(context,TelAreaService.class));
			Log.i(TAG, "启动号码归属地吐司服务");
			Toast.makeText(context, "启动号码归属地吐司服务", Toast.LENGTH_SHORT).show();
		}
		//手机防盗服务是否开启
		boolean isOn = sp.getBoolean("start", false);
		if(isOn){
			Toast.makeText(context, "手机防盗服务打开", Toast.LENGTH_SHORT).show();
			
			tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			String savedSIM = sp.getString("sim", null);
			
			//String currentSIM = tm.getSimSerialNumber()+"测试SIM卡变更";
			String currentSIM = tm.getSimSerialNumber();
			if(savedSIM.equals(currentSIM)){
				
			}else {
			 
				Toast.makeText(context, "SIM卡已经变更", Toast.LENGTH_SHORT).show();
				
				//TODO 第一句发不出去，第三句是乱码，只有第二句正常。以后有时间再说。
				//SmsManager.getDefault().sendTextMessage(sp.getString("telnum", null), null, "【手机伴侣提醒】SIM卡已经 变更，若是本人请重新运行设置向导绑定新的SIM卡；若非本人可向发件人发送清除数据等命令，命令具体用法见手机防盗主页。感谢使用手机伴侣！", null, null);
				SmsManager.getDefault().sendTextMessage(sp.getString("telnum", null), null, "SIM Changed", null, null);
				//SmsManager.getDefault().sendTextMessage(sp.getString("telnum", null), null, "今天天气晴天", null, null);
				
				
			}
		}
	}

}
