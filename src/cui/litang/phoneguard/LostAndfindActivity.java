package cui.litang.phoneguard;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 判断是否运行过手机防盗设置向导：
 * 若是运行过则进入手机防盗主页面；
 * 若是没有运行过，则运行手机防盗设置向导（Setup1Activity）。
 * @author Cuilitang
 * @Date 2015年7月10日
 */
public class LostAndfindActivity extends Activity {
	
	private SharedPreferences sp;
	private ImageView iv_face;
	private TextView tv_safe_phone_number; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		boolean isSetup = sp.getBoolean("setup", false);
		String telNum = sp.getString("telnum", null);
		/*
		if(isSetup){
			setContentView(R.layout.activity_lost_find);
		}else {
			Intent intent = new Intent(LostAndfindActivity.this, Setup1Activity.class);
			startActivity(intent);
			finish();
		}
		*/
		
		setContentView(R.layout.activity_lost_find);
		
		
		tv_safe_phone_number = (TextView) findViewById(R.id.tv_safe_phone_number);
		iv_face = (ImageView) findViewById(R.id.iv_face);
		if(telNum!=null){
			tv_safe_phone_number.setText(telNum);
		}
		
		if(isSetup){
			iv_face.setBackgroundResource(R.drawable.lock);
		}else {
			iv_face.setBackgroundResource(R.drawable.unlock);
		}
		
		
	}
	
	public void enterSetup(View view) {
		Intent intent = new Intent(this, Setup1Activity.class);
		startActivity(intent);
		finish();
	}

}
