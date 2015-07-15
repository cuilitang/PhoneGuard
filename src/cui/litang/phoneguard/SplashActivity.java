package cui.litang.phoneguard;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import cui.litang.phoneguard.utils.StreamTools;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;

public class SplashActivity extends Activity {

	protected static final String TAG = "SplashActivity";
	protected static final int ENTER_HOME = 0;
	protected static final int SHOW_UPDATE_DIALOG = 1;
	protected static final int URL_ERROR = 2;
	protected static final int NETWORK_ERROR = 3;
	protected static final int JSON_ERROR = 4;
	private TextView tv_splash_version;
	private String version;
	private String desc;
	private String apkurl;
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case ENTER_HOME:
				Log.i(TAG, "进入主页面");
				enterHome();
				Toast.makeText(getApplicationContext(), "进入主页面", Toast.LENGTH_LONG).show();
				break;
			case SHOW_UPDATE_DIALOG:
				Log.i(TAG, "显示升级对话框");
				showProgress();
				break;
			case URL_ERROR:
				Log.i(TAG, "URL错误");
				enterHome();
				Toast.makeText(getApplicationContext(), "URL错误", Toast.LENGTH_LONG).show();
				break;
			case NETWORK_ERROR:
				Log.i(TAG, "网络错误");
				enterHome();
				Toast.makeText(getApplicationContext(), "网络错误", Toast.LENGTH_LONG).show();

				break;
			case JSON_ERROR:
				Log.i(TAG, "JSON解析错误");
				enterHome();
				Toast.makeText(getApplicationContext(), "JSON解析错误", Toast.LENGTH_LONG).show();
				break;

			default:
				break;
			}
		};
	};
	Message msg = new Message();
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		
		tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
		tv_splash_version.setText("版本："+getVersionName());
		
		sp = getSharedPreferences("config", MODE_PRIVATE);
		boolean update = sp.getBoolean("update", false);
		
		//拷贝数据库
		copyDB();
		
		if(update){
			checkUpdate();
		}else {
			handler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					enterHome();
				}
			}, 2000);
		}
		
		
		//渐变动画 （暗--->明）
		AlphaAnimation animation = new AlphaAnimation(0.2f, 1.0f);
		animation.setDuration(1000);
		findViewById(R.id.rl_layout_splash).startAnimation(animation);
		
	}
	
	/**
	 * 拷贝号码归属地数据库
	 */
	private void copyDB() {
		try{
			File file = new File(getFilesDir(),"address.db");
			if(file.exists()&&file.length()>0){
				Log.i(TAG, "已经拷贝过");
			}else {
				InputStream is = getAssets().open("address.db");
				FileOutputStream fos = new FileOutputStream(file);
				byte[] buffer = new byte[1024];
				int len = 0;
				while((len=is.read(buffer))!=-1){
					fos.write(buffer,0,len);
				}
				is.close();
				fos.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 显示升级对话框
	 */
	protected void showProgress() {
		AlertDialog.Builder adb = new Builder(SplashActivity.this);
		adb.setTitle("升级提示");
		adb.setMessage(desc);

		adb.setOnCancelListener(new OnCancelListener(){

			@Override
			public void onCancel(DialogInterface dialog) {
				enterHome();
				dialog.dismiss();
			}});
		
		adb.setPositiveButton("立刻升级", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//检测是否存在sd卡
				//若是存在sd卡，则直接升级
				//若是不存在sd卡，提示安装sd卡
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					FinalHttp fh = new FinalHttp();
					 
					fh.download(apkurl, Environment.getExternalStorageDirectory().getAbsolutePath()+"/mobliesafe.apk", new AjaxCallBack<File>() {
						@Override
						public void onFailure(Throwable t, int errorNo, String strMsg) {
							t.printStackTrace();
							Toast.makeText(SplashActivity.this, "下载失败", 1).show();
							super.onFailure(t, errorNo, strMsg);
							 
						};
						@Override
						public void onLoading(long count, long current) {
							super.onLoading(count, current);
							tv_splash_version.setVisibility(View.VISIBLE);
							int progress = (int)(current*100/count);
							tv_splash_version.setText("应用已经下载了 "+progress+"%");
						};
						@Override
						public void onSuccess(File t) {
							super.onSuccess(t);
							installAPK(t);
						}

						private void installAPK(File t) {
							Intent intent = new Intent();
							intent.setAction("android.intent.action.VIEW");
							intent.addCategory("android.intent.category.DEFAULT");
							intent.setDataAndType(Uri.fromFile(t),"application/vnd.android.package-archive");
							startActivity(intent);
							
						};
					
					});
				}else{
					Toast.makeText(getApplicationContext(), "未检测到sd卡，请安装sd卡后再试", 0).show();
					return;
				}
			}
		});
		
		adb.setNegativeButton("下次再说	",new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				enterHome();
			}
		});
		
		adb.show();
		
	}

	/**
	 * 进入主页面
	 */
	protected void enterHome() {
		Intent intent = new Intent(this,HomeActivity.class);
		startActivity(intent);
		finish();
		 
		
	}

	/**
	 * 检查是否有新版本，有就升级
	 */
	private void checkUpdate() {
		new Thread(new Runnable() {
			
			

			@Override
			public void run() {
				Long startTime = System.currentTimeMillis();//记录开始时间
				try {
					
					
					URL url = new URL(getString(R.string.serverurl));
					//联网
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(4000);
					int code = connection.getResponseCode();
					if(code==200){
						InputStream inputStream = connection.getInputStream();
						String json = StreamTools.readFromStream(inputStream);
						Log.i(TAG, "联网成功了！"+json);
						
						//json解析
						JSONObject jsonObject = new JSONObject(json);
						version = jsonObject.getString("version");
						desc = jsonObject.getString("description");
						apkurl = jsonObject.getString("apkurl");
						Log.i(TAG, version);
						Log.i(TAG, desc);
						Log.i(TAG, apkurl);
						
						//检查是否有新版本
						if(getVersionName().equals(version)){
							//没有新版本，进入主页面
							msg.what = ENTER_HOME;
						}else{
							//弹出升级对话框
							msg.what = SHOW_UPDATE_DIALOG;
						}
						
						
					}
					
				} catch (MalformedURLException e) {
					msg.what = URL_ERROR;
					e.printStackTrace();
				}catch (IOException e) {
					msg.what = NETWORK_ERROR;
					e.printStackTrace();
				} catch (JSONException e) {
					msg.what = JSON_ERROR;
					e.printStackTrace();
				}finally{
					Long endTime = System.currentTimeMillis();//记录结束时间
					Long costTime = endTime - startTime;
					if(costTime < 2000){
						try {
							Thread.sleep(2000-costTime);   //splash 页面停顿两秒，让用户看到这个页面
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					handler.sendMessage(msg);
				}
				
			}
		}).start();
	}

	private String getVersionName(){
		PackageManager packageManager = getPackageManager();
		try{
			PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
			return packageInfo.versionName;
		}catch(Exception e){
			e.printStackTrace();
			return "";
		}
		
	}

	

}
