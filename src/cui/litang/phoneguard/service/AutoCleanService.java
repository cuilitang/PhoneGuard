package cui.litang.phoneguard.service;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

/**
 * 自动清理进程 服务
 * @author Cuilitang
 * @Date 2015年8月2日
 */
public class AutoCleanService extends Service {

	private ActivityManager am;
	private SrceenOffRecevier receiver;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		receiver = new SrceenOffRecevier();
		registerReceiver(receiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
		receiver = null;
	}

	
	private class SrceenOffRecevier extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {

			Log.i("ScreenOffReceiver","屏幕锁屏了。。。");
			List<RunningAppProcessInfo> list = am.getRunningAppProcesses();
			for (RunningAppProcessInfo runningAppProcessInfo : list) {
				
				am.killBackgroundProcesses(runningAppProcessInfo.processName);
			}
			
		}
		
	}
}
