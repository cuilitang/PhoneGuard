package cui.litang.phoneguard;

import cui.litang.phoneguard.service.AutoCleanService;
import cui.litang.phoneguard.utils.ServiceUtils;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class TaskSettingActivity extends Activity {
	
	private CheckBox cb_show_system;
	private CheckBox cb_auto_clean;
	private SharedPreferences sp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_setting);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		cb_show_system = (CheckBox) findViewById(R.id.cb_show_system);
		cb_auto_clean = (CheckBox) findViewById(R.id.cb_auto_clean);
		cb_show_system.setChecked(sp.getBoolean("show_system", false));
		cb_show_system.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
				Editor editor = sp.edit();
				editor.putBoolean("show_system", isChecked);
				editor.commit();
				
			}
		});
		
		cb_auto_clean.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
				Intent intent = new Intent(TaskSettingActivity.this, AutoCleanService.class);
				if(isChecked){
					startService(intent);
				}else {
					stopService(intent);
				}
				
			}
		});
	}
	
	/**
	 * 服务是否启动，设置cb状态
	 */
	@Override
	protected void onStart() {
		super.onStart();
		boolean isRunning = ServiceUtils.isServiceRunning(this, "cui.litang.phoneguard.service.AutoCleanService");
		cb_auto_clean.setChecked(isRunning);
	}
	
	

}
