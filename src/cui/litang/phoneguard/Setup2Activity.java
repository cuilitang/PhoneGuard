package cui.litang.phoneguard;

import cui.litang.phoneguard.ui.SettingItemView;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

/**
 * 手机防盗-设置向导-界面-第二步
 * @author Cuilitang
 * @Date 2015年7月10日
 */
public class Setup2Activity extends BaseSetupActivity {
	
	private TelephonyManager tm;
	private SettingItemView clt_bandle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup2);
		
		clt_bandle = (SettingItemView) findViewById(R.id.clt_bandle);
		tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
		String sim = sp.getString("sim", null);
		if(TextUtils.isEmpty(sim)){
			clt_bandle.setCheck(false);
		}else{
			clt_bandle.setCheck(true);
		}
		
		clt_bandle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Editor editor = sp.edit();
				if(clt_bandle.isChecked()){
					clt_bandle.setCheck(false);
					editor.putString("sim", null);
				}else {
					clt_bandle.setCheck(true);
					String sim = tm.getSimSerialNumber();
					editor.putString("sim", sim);
					
				}
				editor.commit();
			}
		});
		
		
	}

	@Override
	public void showNext() {
		
		//若sim卡没有绑定无法往下进行设置
		String string = sp.getString("sim", null);
		if (TextUtils.isEmpty(string)) {
			Toast.makeText(this, "SIM没有绑定手机防盗功能将无法使用", Toast.LENGTH_SHORT).show();
			return;
			
		}
		Intent intent = new Intent(Setup2Activity.this, Setup3Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
		
	}

	@Override
	public void showPre() {
		Intent intent = new Intent(Setup2Activity.this, Setup1Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);

		
	}
	
	

}
