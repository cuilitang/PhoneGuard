package cui.litang.phoneguard.engine;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import cui.litang.phoneguard.entity.AppInfo;

/**
 * 获取手机安装的所有应用信息
 * @author Cuilitang
 * @Date 2015年7月21日
 */
public class AppInfoProvider {
	
	/**
	 * 获取已经安卓的所有程序信息的List集合
	 * @param context 上下文
	 * @return  List<AppInfo>
	 */
	public static List<AppInfo> getAppInfos(Context context){
		
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> pi = pm.getInstalledPackages(0);
		 ArrayList<AppInfo> appInfos = new ArrayList<AppInfo>();
		 
		for (PackageInfo packageInfo : pi) {
			
			AppInfo appInfo = new AppInfo();
			String packname = packageInfo.packageName;
			Drawable icon = packageInfo.applicationInfo.loadIcon(pm);
			String name = packageInfo.applicationInfo.loadLabel(pm).toString();
			int flags = packageInfo.applicationInfo.flags;
			
			if((flags&ApplicationInfo.FLAG_SYSTEM)==0){
				appInfo.setUserApp(true);
			}else {
				appInfo.setUserApp(false);
			}
			
			if((flags&ApplicationInfo.FLAG_EXTERNAL_STORAGE)==0){
				appInfo.setInRAM(true);
			}else {
				appInfo.setInRAM(false);
			}
			
			appInfo.setName(name);
			appInfo.setIcon(icon);
			appInfo.setPackname(packname);
			appInfos.add(appInfo);
		}
		return appInfos;
		
	}
	
	

}
