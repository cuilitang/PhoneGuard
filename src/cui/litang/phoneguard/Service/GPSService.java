package cui.litang.phoneguard.Service;

import java.text.Format;
import java.text.SimpleDateFormat;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * 这个服务应该从app启动时候就启动啊，不应该是从短信的广播接收器启动啊，
 * 广播接收器只是在提示说现在马上给我一个postion，
 * 而现在却变成了马上去启动服务获取position，然后发回来，这样也可以，但是为什么要存sharedPerfrences呢？
 * @author Cuilitang
 * @Date 2015年7月15日
 */

public class GPSService extends Service {
	
	
	private static final String TAG = "GPSService";
	private LocationManager lm;
	private MyLocationListener myll;

	@Override
	public void onCreate() {
		
		super.onCreate();
		lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		myll = new MyLocationListener();
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		
		String provider = lm.getBestProvider(criteria, true);
		lm.requestLocationUpdates(provider, 0, 0, myll);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		lm.removeUpdates(myll);
		myll = null;
		
		
	}
	
	class MyLocationListener implements LocationListener{

		
		private SharedPreferences sp;

		@Override
		public void onLocationChanged(Location location) {
			String longitude = "j:"+location.getLongitude()+"\n";
			String latitude = "j:"+location.getLatitude()+"\n";
			String accuracy = "j:"+location.getAccuracy()+"\n";
			String time = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(location.getTime());
			
			//存储
			sp = getSharedPreferences("config", MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putString("position", longitude+latitude+accuracy+time);
			editor.commit();
			Log.i(TAG, longitude+latitude+accuracy+time);
			
			//发送短信给安全号码
			
			
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}

}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
