package cui.litang.phoneguard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * 手机防盗-设置向导-界面-第一步
 * @author Cuilitang
 * @Date 2015年7月10日
 */
public class Setup2Activity extends BaseSetupActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup2);
	}

	@Override
	public void showNext() {
		Intent intent = new Intent(Setup2Activity.this, Setup3Activity.class);
		startActivity(intent);
		finish();
		
	}

	@Override
	public void showPre() {
		Intent intent = new Intent(Setup2Activity.this, Setup1Activity.class);
		startActivity(intent);
		finish();
		
	}
	
	

}
