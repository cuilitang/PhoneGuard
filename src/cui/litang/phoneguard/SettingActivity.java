package cui.litang.phoneguard;

import cui.litang.phoneguard.service.BlackNumberService;
import cui.litang.phoneguard.service.TelAreaService;
import cui.litang.phoneguard.ui.SettingClickView;
import cui.litang.phoneguard.ui.SettingItemView;
import cui.litang.phoneguard.utils.ServiceUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class SettingActivity extends Activity{
	
	private SharedPreferences sp;
	private SettingItemView clt_update;
	private SettingItemView clt_tel_area;
	private SettingItemView clt_black_list;
	private SettingClickView clt_changebg;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		
		//设置是否自动升级
		isAutoUpdate();
		
		//设置是否显示来电去电号码归属地
		isShowTelArea();
		
		//设置号码归属地背景
		setMyToastBg();
		
		//是否开启黑名单服务
		isExeBlackList();
	}
	
	

	/**
	 * 设置是否自动升级
	 */
	private void isAutoUpdate() {
		clt_update = (SettingItemView) findViewById(R.id.clt_update);
		boolean updateStatus = sp.getBoolean("update", false);
		clt_update.setCheck(updateStatus);
		clt_update.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Editor editor = sp.edit();
				if(clt_update.isChecked()){
					clt_update.setCheck(false);
					//clt_update.setDesc("自动升级已经停止");
					editor.putBoolean("update", false);
					
				}else {
					clt_update.setCheck(true);
					//clt_update.setDesc("自动升级已经启动");
					editor.putBoolean("update", true);
				}
				
				editor.commit();
			}
		});
		
	}
	
	/**
	 * 是否显示号码归属地
	 */
	private void isShowTelArea() {
		
		clt_tel_area = (SettingItemView) findViewById(R.id.clt_tel_area);
		final Intent areaIntent = new Intent(this,TelAreaService.class);

		boolean telareaStatus = ServiceUtils.isServiceRunning(SettingActivity.this,"cui.litang.phoneguard.service.TelAreaService");
		clt_tel_area.setCheck(telareaStatus);
		
		
		
		clt_tel_area.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				
				if(clt_tel_area.isChecked()){
					clt_tel_area.setCheck(false);
					stopService(areaIntent);
					
				}else {
					clt_tel_area.setCheck(true);
					startService(areaIntent);
				}
				
			}
		});
		
	}

	/**
	 * 设置号码归属地自定义吐司背景
	 */
	private void setMyToastBg() {
		clt_changebg = (SettingClickView) findViewById(R.id.clt_changebg);
		final String [] items = {"半透明","活力橙","卫士蓝","金属灰","苹果绿"};
		int which = sp.getInt("which", 0);
		clt_changebg.setDesc(items[which]);
		
		clt_changebg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int which = sp.getInt("which", 0);
				AlertDialog.Builder builder = new Builder(SettingActivity.this);
				builder.setTitle("选择风格");
				builder.setSingleChoiceItems(items, which, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						Editor editor = sp.edit();
						editor.putInt("which", which);
						editor.commit();
						clt_changebg.setDesc(items[which]);
						dialog.dismiss();
					}
				});
				
				builder.setNegativeButton("取消", null);
				builder.show();
				
			}
		});
		
		
	}
	
	/**
	 * 是否启用黑名单
	 */
	private void isExeBlackList() {
		
		clt_black_list = (SettingItemView) findViewById(R.id.clt_black_list);
		final Intent blackListIntent = new Intent(this,BlackNumberService.class);

		boolean blackListStatus = ServiceUtils.isServiceRunning(SettingActivity.this,"cui.litang.phoneguard.service.BlackNumberService");
		clt_black_list.setCheck(blackListStatus);
		
		
		
		clt_black_list.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				
				if(clt_black_list.isChecked()){
					clt_black_list.setCheck(false);
					stopService(blackListIntent);
					
				}else {
					clt_black_list.setCheck(true);
					startService(blackListIntent);
				}
				
			}
		});
		
	}

}
