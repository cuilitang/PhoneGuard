package cui.litang.phoneguard;

import cui.litang.phoneguard.ui.SettingItemView;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class SettingActivity extends Activity{
	
	private SharedPreferences sp;
	private SettingItemView clt_update;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		sp = getSharedPreferences("config", MODE_PRIVATE);
		clt_update = (SettingItemView) findViewById(R.id.clt_update);
		boolean updateStatus = sp.getBoolean("update", false);
		
		if(updateStatus){
			clt_update.setCheck(updateStatus);
			clt_update.setDesc("自动升级已经启动");
		}else {
			clt_update.setCheck(updateStatus);
			clt_update.setDesc("自动升级已经停止");
		}
		
		clt_update.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Editor editor = sp.edit();
				if(clt_update.isChecked()){
					clt_update.setCheck(false);
					clt_update.setDesc("自动升级已经停止");
					editor.putBoolean("update", false);
					
				}else {
					clt_update.setCheck(true);
					clt_update.setDesc("自动升级已经启动");
					editor.putBoolean("update", true);
				}
				
				editor.commit();
			}
		});
	}

}
