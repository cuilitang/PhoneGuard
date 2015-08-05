package cui.litang.phoneguard;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.List;

import cui.litang.phoneguard.db.dao.AntivirsuDAO;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
/**
 * 杀毒
 * @author Cuilitang
 * @Date 2015年8月5日
 */
public class AntivirusActivity extends Activity {
	
	private ImageView iv_scan;
	private TextView tv_scan_status;
	private LinearLayout ll_container;

	protected static final int SCANING = 0;
	protected static final int FINISH = 2;
	private ProgressBar progressBar;
	private PackageManager packageManager;
	private Handler handler = new Handler(){
		
		public void handleMessage(Message msg) {
			
			switch (msg.what) {
			
			case SCANING:
				ScanInfo scanInfo = (ScanInfo) msg.obj;
				tv_scan_status.setText("正在扫描:"+scanInfo.name);
				
				TextView tv = new TextView(AntivirusActivity.this);
				if(scanInfo.isVirus){
					
					tv.setText("发现病毒:"+scanInfo.name);
				}else {
					
					tv.setText("扫描安全:"+scanInfo.name);
				}
				
				ll_container.addView(tv,0);
				break;

			case FINISH:
				tv_scan_status.setText("扫描完毕");
				iv_scan.clearAnimation();
				break;
				
			default:
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_antivirus);
		
		tv_scan_status = (TextView) findViewById(R.id.tv_scan_status);
		ll_container = (LinearLayout) findViewById(R.id.ll_container);
		iv_scan = (ImageView) findViewById(R.id.iv_scan);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		//new RotateAnimation(fromDegrees, toDegrees, pivotXType, pivotXValue, pivotYType, pivotYValue)
		RotateAnimation animation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		animation.setDuration(1000);
		animation.setRepeatCount(Animation.INFINITE);
		iv_scan.startAnimation(animation);

		scanVirus();
	}
	/**
	 * 扫毒
	 */
	private void scanVirus() {
		
		packageManager = getPackageManager();
		tv_scan_status.setText("正在启动杀毒引擎");
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		
		new Thread(){
			public void run() {
				
				List<PackageInfo> packages = packageManager.getInstalledPackages(0);
				
				progressBar.setMax(packages.size());
				int progress = 0;
				
				for (PackageInfo packageInfo : packages) {
					
					String sourceDir = packageInfo.applicationInfo.sourceDir;
					String md5 = getFileMD5(sourceDir);
					ScanInfo scanInfo = new ScanInfo();
					scanInfo.name = packageInfo.applicationInfo.loadLabel(packageManager).toString();
					scanInfo.packageName = packageInfo.packageName;
					System.out.println(scanInfo.packageName+":"+md5);
					if(AntivirsuDAO.isVirus(md5)){//查询md5信息，是否在病毒数据库里面存在。
						
						scanInfo.isVirus = true;//发现病毒
					}else{
						
						scanInfo.isVirus = false;//扫描安全
					}
					
					Message msg = Message.obtain();
					msg.obj = scanInfo;
					msg.what = SCANING;
					handler.sendMessage(msg);
					progress++;
					progressBar.setProgress(progress);
				}
				
				Message message = Message.obtain();
				message.what = FINISH;
				handler.sendMessage(message);
				
			};
		}.start();
	}
	
	/**
	 * 得到文件的MD5码
	 * @param sourceDir 文件路径
	 */
	protected String getFileMD5(String sourceDir) {
		
		try{
			File file = new File(sourceDir);
			MessageDigest digester = MessageDigest.getInstance("md5");
			FileInputStream inputStream = new FileInputStream(file);
			byte [] buffer = new byte[1024];
			int len = -1;
			
			while((len=inputStream.read(buffer))!=-1){
				digester.update(buffer,0,len);
			}
			inputStream.close();
			
			byte[] result = digester.digest();
			StringBuffer stringBuffer = new StringBuffer();
			
			for (byte b : result) {
				
				int number = b & 0xff;// 加盐
				String str = Integer.toHexString(number);
				if (str.length()==1) {
					stringBuffer.append("0");
				}
				stringBuffer.append(str);
			}
			
			return stringBuffer.toString();
		}catch(Exception e){
			e.printStackTrace();
			return "";
		}
	}
	
	/**
	 * 扫描信息的内部类
	 */
	private class ScanInfo{
		String packageName;
		String name;
		boolean isVirus;
	}

}
