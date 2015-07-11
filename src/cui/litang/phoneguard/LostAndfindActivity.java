package cui.litang.phoneguard;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

/**
 * 判断是否运行过手机防盗设置向导：
 * 若是运行过则进入手机防盗主页面；
 * 若是没有运行过，则运行手机防盗设置向导（Setup1Activity）。
 * @author Cuilitang
 * @Date 2015年7月10日
 */
public class LostAndfindActivity extends Activity {
	
	private SharedPreferences sp; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		boolean isSetup = sp.getBoolean("setup", false);
		
		if(isSetup){
			setContentView(R.layout.activity_lost_find);
		}else {
			Intent intent = new Intent(LostAndfindActivity.this, Setup1Activity.class);
			startActivity(intent);
			finish();
		}
		
		
	}

}
