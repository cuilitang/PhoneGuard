package cui.litang.phoneguard;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Menu;

/**
 * 手机防盗-设置向导-界面-第四步
 * @author Cuilitang
 * @Date 2015年7月12日
 */
public class Setup4Activity extends BaseSetupActivity {

	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup4);
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.setup4, menu);
		return true;
	}

	@Override
	public void showNext() {
		//记录下来已经运行过设置向导，下次就直接进入手机防盗主界面了
		sp = getSharedPreferences("config", MODE_PRIVATE);
		Editor edtor = sp.edit();
		edtor.putBoolean("setup", true);
		edtor.commit();
		
		Intent intent = new Intent(Setup4Activity.this, LostAndfindActivity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
		
	}

	@Override
	public void showPre() {
		Intent intent = new Intent(Setup4Activity.this, Setup3Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
		
	}

}
