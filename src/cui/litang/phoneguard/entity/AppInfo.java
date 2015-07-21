package cui.litang.phoneguard.entity;

import android.graphics.drawable.Drawable;

/**
 * App信息实体类，用来存储App的描述信息
 * @author Cuilitang
 * @Date 2015年7月21日
 */
public class AppInfo {
	
	private Drawable icon;
	private String name;
	private String packname;
	private boolean isInRAM;
	private boolean isUserApp;
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
	public String getPackname() {
		return packname;
	}
	public void setPackname(String packname) {
		this.packname = packname;
	}
	public boolean isInRAM() {
		return isInRAM;
	}
	public void setInRAM(boolean isInRAM) {
		this.isInRAM = isInRAM;
	}
	public boolean isUserApp() {
		return isUserApp;
	}
	public void setUserApp(boolean isUserApp) {
		this.isUserApp = isUserApp;
	}
	@Override
	public String toString() {
		return "AppInfo [icon=" + icon + ", name=" + name + ", packname="
				+ packname + ", isInRAM=" + isInRAM + ", isUserApp="
				+ isUserApp + "]";
	}
	
	
	

}
