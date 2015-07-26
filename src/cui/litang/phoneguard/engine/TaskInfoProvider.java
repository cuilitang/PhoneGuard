package cui.litang.phoneguard.engine;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Debug.MemoryInfo;
import cui.litang.phoneguard.R;
import cui.litang.phoneguard.entity.TaskInfo;

/**
 * 获取进程信息和进程相关的应用信息，存储到TaskInfo实体中
 * @author Cuilitang
 * @Date 2015年7月25日
 */
public class TaskInfoProvider {

	public static List<TaskInfo> getTaskInfos(Context context) {
		
		ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> processes = am.getRunningAppProcesses();
		PackageManager pm = context.getPackageManager();
		ArrayList<TaskInfo> taskInfos = new ArrayList<TaskInfo>();
		
		for (RunningAppProcessInfo runningAppProcessInfo : processes) {
			
			TaskInfo taskInfo = new TaskInfo();
			String taskPackageName = runningAppProcessInfo.processName;
			MemoryInfo[] memoryInfos = am.getProcessMemoryInfo(new int[]{runningAppProcessInfo.pid});
	        //Return total private dirty memory usage in kB.     dirty 操纵，脏
			long taskMemoSize = memoryInfos[0].getTotalPrivateDirty()*1024L;
			taskInfo.setMemoSize(taskMemoSize);
			taskInfo.setPackageName(taskPackageName);
			
			try {
				ApplicationInfo appInfo = pm.getApplicationInfo(taskPackageName, 0);
				Drawable taskIcon = appInfo.loadIcon(pm);
				String taskLabel = appInfo.loadLabel(pm).toString();
				
				taskInfo.setIcon(taskIcon);
				taskInfo.setName(taskLabel);
				
				if((appInfo.flags&appInfo.FLAG_SYSTEM)==0){
					taskInfo.setUserTask(true);
				}else {
					taskInfo.setUserTask(false);
				}
			} catch (NameNotFoundException e) {
				
				new RuntimeException("系统底层的应用不是用java写的，获取不到包名和ICON").printStackTrace();
				taskInfo.setIcon(context.getResources().getDrawable(R.drawable.ic_default));
				taskInfo.setName(taskPackageName);
			}
			
			taskInfos.add(taskInfo);
		}

		return taskInfos;
	}

}
