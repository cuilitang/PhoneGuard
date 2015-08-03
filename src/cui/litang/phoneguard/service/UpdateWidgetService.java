package cui.litang.phoneguard.service;

import java.util.Timer;
import java.util.TimerTask;

import cui.litang.phoneguard.R;
import cui.litang.phoneguard.recevier.MyWidget;
import cui.litang.phoneguard.utils.SystemInfoUtils;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;

/**
 * 手动更新widget服务
 * @author Cuilitang
 * @Date 2015年8月3日
 */
public class UpdateWidgetService extends Service {
	
	private ScreenOffReceiver offReceiver;
	private ScreenOnReceiver onReceiver;
	private Timer timer;
	private TimerTask task;
	private AppWidgetManager awm;
	
	
	@Override
	public IBinder onBind(Intent intent) {
		//  Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		onReceiver = new ScreenOnReceiver();
		offReceiver = new ScreenOffReceiver();
		registerReceiver(onReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
		registerReceiver(offReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
		awm = AppWidgetManager.getInstance(this);
		startTimer();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(offReceiver);
		unregisterReceiver(onReceiver);
		offReceiver = null;
		onReceiver = null;
		stopTimer();
	}
	
	private class ScreenOffReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			
			stopTimer();
			
		}
		
	}
	
	private class ScreenOnReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			
			startTimer();
		}
		
	}
	

	


	public void startTimer() {
		
		if(timer == null && task == null){
			timer= new Timer();
			task = new TimerTask() {
				
				@Override
				public void run() {
					
					ComponentName name = new ComponentName(UpdateWidgetService.this,MyWidget.class);
					RemoteViews views = new RemoteViews(getPackageName(), R.layout.process_widget);
					views.setTextViewText(R.id.process_count, "正在运行的进程："+SystemInfoUtils.getRunningProcessCount(getApplicationContext())+"个");
					long availMemo = SystemInfoUtils.getAvailMemo(getApplicationContext());
					views.setTextViewText(R.id.process_memory, "可用内存："+Formatter.formatFileSize(getApplicationContext(), availMemo));
					
					// 自定义一个广播事件,杀死后台进度的事件
					Intent intent = new Intent();
					intent.setAction("cui.litang.phonegrand.KILL_ALL");
					PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
					views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);
					awm.updateAppWidget(name, views);   //provider:The ComponentName for the BroadcastReceiver provider for your AppWidget.

				}
			};
			timer.schedule(task, 0,3000);
		}
	}


	public void stopTimer() {
		if (timer != null && task != null) {
			timer.cancel();
			task.cancel();
			timer = null;
			task = null;
		}
		
	}
	
	

}
