package cui.litang.phoneguard.service;

import java.util.List;

import cui.litang.phoneguard.EnterPwdActivity;
import cui.litang.phoneguard.MyApplication;
import cui.litang.phoneguard.db.dao.AppLockDAO;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

/**
 * 程序锁
 * 省电优化：监听锁屏事件，锁屏后直接将程序锁服务关掉，等屏幕开启时候再将程序锁服务启动起来。
 * 这样，用户的设置状态就需要在sharedperformence中存储一下，使用广播接收器开启.与开机启动服务相同。
 * 若是服务被异常终止，也可以在服务中发个广播重启服务，当前需要先读取shareperformence的配置文件判断一下，是不是用户操作。
 * 
 * 问题：当前程序用了太多广播，一个应用程序中，应该尽量少使用广播吧，广播主要用于不同程序之间的信息传递。 认证成功与否完全可以用startActivityForResult来写，数据库变化完全可以用方法调用来写
 * @author Cuilitang
 * @Date 2015年8月6日
 */
public class AppLockService extends Service {

	private static final String TAG = "AppLockService";
	private ScreenOffReceiver offReceiver;
	private InnerReceiver innerReceiver;
	private AppLockDAO dao;
	public List<String> packageNames;
	private DataChangeReceiver dataChangeReceiver;
	private ActivityManager am;
	private boolean flag;
	private Intent intent;
	public Object loginedName;
	private SharedPreferences sp;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		offReceiver = new ScreenOffReceiver();
		registerReceiver(offReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));    //接收锁屏广播
		innerReceiver = new InnerReceiver();
		registerReceiver(innerReceiver, new IntentFilter("cui.litang.phonegrund.LOGINED"));  //接收认证成功广播
		dataChangeReceiver = new DataChangeReceiver();
		registerReceiver(dataChangeReceiver, new IntentFilter("cui.litang.phonegrund.APPLOCK_CHANGE"));  //接收加锁程序变化广播
		
		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		dao = new AppLockDAO(this);
		packageNames = dao.findAll();
		flag = true;
		intent = new Intent(getApplicationContext(),EnterPwdActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		new Thread(){
			public void run() {
				while(flag){
					List<RunningTaskInfo> list = am.getRunningTasks(1);
					String name = list.get(0).topActivity.getPackageName();
					if(packageNames.contains(name)){
						if(name.equals(loginedName)){
							//认证成功的程序，啥都不用干
						}else {
							intent.putExtra("package_name", name);
							startActivity(intent);
						}
					}
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
		}.start();
	}
	
	public void onDestroy() {
		flag = false;
		unregisterReceiver(innerReceiver);
		innerReceiver = null;
		unregisterReceiver(offReceiver);
		offReceiver = null;
		unregisterReceiver(dataChangeReceiver);
		dataChangeReceiver = null;
		super.onDestroy();
		
		//判断是正常停止还是异常停止。
				Log.i(TAG, "程序锁服务停止了！！！");
				sp = getSharedPreferences("config", MODE_PRIVATE);
				boolean isUser = sp.getBoolean(MyApplication.ISEXEAPPLOCK, false);
				if(isUser){
					Log.i(TAG, "程序锁服务被异常停止了！！！应该重新启动起来！！！");
					Intent intent = new Intent(getApplicationContext(),AppLockService.class);
					startService(intent);
				}
	}
	
	/**
	 * 锁屏之后将认证成功的应用名设为空，再次进入应用会提示重新认证
	 * @author Cuilitang
	 * @Date 2015年8月4日
	 */
	private class ScreenOffReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			loginedName = null;
		}
	}
	
	/**
	 *  将认证成功的程序放到List中
	 * @author Cuilitang
	 * @Date 2015年8月11日
	 */
	private class InnerReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("认证成功的广播事件");
			loginedName = intent.getStringExtra("logined_name");
		}
	}
	
	/**
	 * 自定义保护名单改变广播接收器
	 * @author Cuilitang
	 * @Date 2015年8月4日
	 */
	private class DataChangeReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			System.out.println("数据库的内容变化了");
			packageNames = dao.findAll();
		}
	}

}
