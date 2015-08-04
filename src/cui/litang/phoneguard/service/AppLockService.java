package cui.litang.phoneguard.service;

import java.util.List;

import cui.litang.phoneguard.EnterPwdActivity;
import cui.litang.phoneguard.db.dao.AppLockDao;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class AppLockService extends Service {

	private ScreenOffReceiver offReceiver;
	private InnerReceiver innerReceiver;
	private AppLockDao dao;
	public List<String> packageNames;
	private DataChangeReceiver dataChangeReceiver;
	private ActivityManager am;
	private boolean flag;
	private Intent intent;
	public Object loginedName;

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
		dao = new AppLockDao(this);
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
