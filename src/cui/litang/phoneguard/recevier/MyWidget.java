package cui.litang.phoneguard.recevier;

import cui.litang.phoneguard.service.UpdateWidgetService;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

/**
 * AppWidgetProvider 生命周期管理
 * http://mobile.51cto.com/aprogram-445954.htm
 * AppWidgetProvider 是 BroadcastReceiver 的子类，本质是个 广播接收器，它专门用来接收来自 Widget组件的各种请求（用Intent传递过来），
 * 所以如果让我给他起名的话 我会给他命名为AppWidgetReceiver,每一个Widget都要有一个AppWidgetProvider. 
 * @author Cuilitang
 * @Date 2015年8月3日
 */
public class MyWidget extends AppWidgetProvider {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		Intent intent2 = new Intent(context, UpdateWidgetService.class);
		context.startService(intent2);
	}
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
	
	@Override
	public void onEnabled(Context context) {
		
		super.onEnabled(context);
		Intent intent2 = new Intent(context, UpdateWidgetService.class);
		context.startService(intent2);
	}
	
	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
		Intent intent2 = new Intent(context, UpdateWidgetService.class);
		context.stopService(intent2);
	}

}
