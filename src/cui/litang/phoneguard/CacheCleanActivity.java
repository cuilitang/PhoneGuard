package cui.litang.phoneguard;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 缓存清理
 * 若是要想实现更丰富的ui效果，应该吧扫描出来的缓存信息放在listView里面
 * @author Cuilitang
 * @Date 2015年8月6日
 */
public class CacheCleanActivity extends Activity {
	
	private ProgressBar pb;
	private TextView tv_scan_status;
	private PackageManager manager;
	private LinearLayout ll_container;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cache_clean);
		
		tv_scan_status = (TextView) findViewById(R.id.tv_scan_status);
		pb = (ProgressBar) findViewById(R.id.pb);
		ll_container = (LinearLayout) findViewById(R.id.ll_container);
		
		scanCache();
	}
	
	/**
	 * 清理缓存
	 */
	private void scanCache() {
		
		manager = getPackageManager();
		
		new Thread(){
			
			public void run() {
				
				Method getPackageSizeInfoMethod = null;
				Method[] methods = PackageManager.class.getMethods();
				
				for (Method method : methods) {
					     
					if ("getPackageSizeInfo".equals(method.getName())) {
						getPackageSizeInfoMethod = method;
						break;
					}
				}	
				
				List<PackageInfo> installedPackages = manager.getInstalledPackages(0);
				pb.setMax(installedPackages.size());
				int progress = 0;
				for (PackageInfo packageInfo : installedPackages) {
					
					try {
						getPackageSizeInfoMethod.invoke(manager, packageInfo.packageName,new MyPackageStatsObserver());
						Thread.sleep(200);
					} catch (Exception e) {
						//  Auto-generated catch block
						e.printStackTrace();
					} 
					
					progress++;
					pb.setProgress(progress);
				}
				
				
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						
						tv_scan_status.setText("扫描完毕");
						
					}
				});
				
			};
		
		}.start();
		
		
		
		
	}
	
	/**
	 * 得到所有应用信息
	 * @author Cuilitang
	 * @Date 2015年8月5日
	 */
	class MyPackageStatsObserver extends IPackageStatsObserver.Stub{
		
		/**
		 * 得到 PackageStats 之后的回调
		 */
		@Override
		public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
				throws RemoteException {
			
			
			final long cache = pStats.cacheSize;
			//long code = pStats.codeSize;
			//long data = pStats.dataSize;
			final String packageName = pStats.packageName;
			final ApplicationInfo appInfo;
			
			try {
				appInfo = manager.getApplicationInfo(packageName, 0);
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						
						tv_scan_status.setText("正在扫描:"+appInfo.loadLabel(manager));
						
						if(cache>0){
							View view = View.inflate(getApplicationContext(), R.layout.list_item_cacheinfo, null);
							TextView tv_cache = (TextView) view.findViewById(R.id.tv_cache_size);
							tv_cache.setText("缓存大小:"+Formatter.formatFileSize(getApplicationContext(), cache));
							TextView tv_name = (TextView) view.findViewById(R.id.tv_app_name);
							tv_name.setText(appInfo.loadLabel(manager));
							ImageView iv_delete = (ImageView) view.findViewById(R.id.iv_delete);
							iv_delete.setOnClickListener(new OnClickListener() {
								

								// 点击child时响应的点击事件
								@Override
								public void onClick(View v) {
									// 判断SDK的版本号
									if (Build.VERSION.SDK_INT >= 9) {
										// 跳转至“清理缓存”的界面（可以通过：设置-->应用程序-->点击任意应用程序后的界面）
										Intent intent = new Intent();
										intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
										intent.addCategory(Intent.CATEGORY_DEFAULT);
										intent.setData(Uri.parse("package:" + packageName));
										startActivity(intent);
										
									} else {
										Intent intent = new Intent();
										intent.setAction("android.intent.action.VIEW");
										intent.addCategory(Intent.CATEGORY_DEFAULT);
										intent.addCategory("android.intent.category.VOICE_LAUNCH");
										intent.putExtra("pkg", packageName);
										startActivity(intent);
									}
								}
							
							
								/**   因为删除缓存的操作权限只有系统应用才可以申请，所以此处会有异常
								@Override
								public void onClick(View v) {
									//deleteApplicationCacheFiles
									try {
										Method method = PackageManager.class.getMethod("deleteApplicationCacheFiles", String.class,
												MyPackageDataObserver.class
												);
										method.invoke(manager, packageName,new MyPackageDataObserver());
									} catch (Exception e) {
										e.printStackTrace();
									}
									
								}
								*/
							});
							ll_container.addView(view,0);
						}
					}
				});
			} catch (NameNotFoundException e) {
				// Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
		}
		
	}
	
										  
	private class MyPackageDataObserver extends IPackageDataObserver.Stub{
		
		/**
		 * 是否清理成功
		 */
		@Override
		public void onRemoveCompleted(String packageName, boolean succeeded)
				throws RemoteException {
			System.out.println(packageName+succeeded);
		}
	}
	
	/**
	 * 全部清理
	 * @param view
	 */
	public void clearAll(View view){
		Method[] methods = PackageManager.class.getMethods();
		for(Method method:methods){
			if("freeStorageAndNotify".equals(method.getName())){
				try {
					method.invoke(manager, Integer.MAX_VALUE,new MyPackageDataObserver());
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		}
		
		TextView tv_clear_all = (TextView) findViewById(R.id.tv_clear_all);
		tv_clear_all.setVisibility(TextView.VISIBLE);
	}

}
