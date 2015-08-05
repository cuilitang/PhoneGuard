package cui.litang.phoneguard;

import cui.litang.phoneguard.utils.SmsUtils;
import cui.litang.phoneguard.utils.SmsUtils.BackupCallBack;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class TookitActivity extends Activity {

	private ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tookit);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tookit, menu);
		return true;
	}
	
	public void queryArea(View view) {
		
		Intent intent = new Intent(this,TelAreaActivity.class);
		startActivity(intent);
	}
	
	
	/**
	 *  备份短信
	 * @param view
	 */
	public void backupSMS(View view) {
		
		dialog = new ProgressDialog(this);
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.setMessage("正在备份短信");
		dialog.show();
		
		new Thread(){
			
			@Override
			public void run() {
				
				try {
					SmsUtils.backupSms(TookitActivity.this, new BackupCallBack() {
						
						@Override
						public void onBackup(int process) {
							dialog.setProgress(process);
							
						}
						
						@Override
						public void beforeBackup(int count) {
							dialog.setMax(count);
						}
					});
					
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(TookitActivity.this, "已经将数据备份到SD卡根目录backup.xml", Toast.LENGTH_LONG).show();
						}
					});
				} catch (Exception e) {

					e.printStackTrace();
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {

							Toast.makeText(TookitActivity.this, "备份失败", Toast.LENGTH_SHORT).show();
						}
					});
				}finally{
					dialog.dismiss();
					
				}
			};
		}.start();
		
		
	}
	
	/**
	 * 还原短信
	 * @param view
	 */
	public void restoreSMS(View view) {
		Toast.makeText(this, "测试功能，无法使用", Toast.LENGTH_SHORT).show();
		return;
		/*
		SmsUtils.restoreSms(TookitActivity.this, true);
		Toast.makeText(this, "还原成功", 0).show();
		*/
	}


}
