package cui.litang.phoneguard.entity;

import android.graphics.drawable.Drawable;

/**
 * 进程实体
 * @author Cuilitang
 * @Date 2015年7月25日
 */
public class TaskInfo {

	private Drawable icon;
	private String name;
	private String packageName;
	private long memoSize;
	private boolean checked;
	private boolean userTask;  //true用户进程 false系统进程
	
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public long getMemoSize() {
		return memoSize;
	}
	public void setMemoSize(long memoSize) {
		this.memoSize = memoSize;
	}
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	public boolean isUserTask() {
		return userTask;
	}
	public void setUserTask(boolean userTask) {
		this.userTask = userTask;
	}
	
	
	
	

}
