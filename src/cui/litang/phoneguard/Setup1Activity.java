package cui.litang.phoneguard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * 手机防盗-设置向导-界面-第一步
 * @author Cuilitang
 * @Date 2015年7月10日
 */
public class Setup1Activity extends BaseSetupActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup1);
	}
	
	
	@Override
	public void showNext() {
		Intent intent = new Intent(Setup1Activity.this, Setup2Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);

	}


	@Override
	public void showPre() {
		// TODO Auto-generated method stub
		
	}
	
	

}
