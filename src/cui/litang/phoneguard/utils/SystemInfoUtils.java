package cui.litang.phoneguard.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;

/**
 * 获取系统信息工具类
 * @author Cuilitang
 * @Date 2015年7月25日
 */
public class SystemInfoUtils {

	/**
	 * 获取当前系统正在运行的进程个数
	 * @param context
	 * @return
	 */
	public static int getRunningProcessCount(Context context){
		
		ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> list = am.getRunningAppProcesses();
		return list.size();
	}
	
	/**
	 * 获取手机可用内存
	 * @param context
	 * @return 可用内存
	 */
	public static long getAvailMemo(Context context) {
		
		ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo outInfo = new MemoryInfo();
		am.getMemoryInfo(outInfo);
		return outInfo.availMem;
	}
	
	/**
	 * 获取手机可用总内存
	 * @param context
	 * @return
	 */
	public static long getTotalMemo(Context context) {
		
		File file = new File("/proc/meminfo");
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
			//MemTotal:         513000 kB
			String line = reader.readLine();
			StringBuilder sb = new StringBuilder();
			for(char c : line.toCharArray()){
				if(c>='0'&&c <='9'){
					sb.append(c);
				}
			}
			
			return Long.parseLong(sb.toString())*1024;
		} catch (IOException e) {
			e.printStackTrace();
			return 0; 
		}
		
	}

}
