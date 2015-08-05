package cui.litang.phoneguard;

import java.util.List;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.os.Bundle;

public class FlowmeterActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_flowmeter);
		PackageManager manager = getPackageManager();
		List<ApplicationInfo> list = manager.getInstalledApplications(0);
		for (ApplicationInfo applicationInfo : list) {
			int uid = applicationInfo.uid;
			long uidTxBytes = TrafficStats.getUidTxBytes(uid);
			long uidRxBytes = TrafficStats.getUidRxBytes(uid);
		}
		
		long mobileRxBytes = TrafficStats.getMobileRxBytes();//下行移动流量
		long mobileTxBytes = TrafficStats.getMobileTxBytes();//上行移动流量
		
		long totalRxBytes = TrafficStats.getTotalRxBytes();//下行总流量 ，包含wifi
		long totalTxBytes = TrafficStats.getTotalTxBytes();//上行总流量 ，包含wifi
		
	}

}
