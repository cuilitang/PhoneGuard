package cui.litang.phoneguard.recevier;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 杀死全部进程的广播接收器
 * @author Cuilitang
 * @Date 2015年8月3日
 */
public class KillAllReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> list = activityManager.getRunningAppProcesses();
		for (RunningAppProcessInfo runningAppProcessInfo : list) {
			activityManager.killBackgroundProcesses(runningAppProcessInfo.processName);
		}

	}

}
