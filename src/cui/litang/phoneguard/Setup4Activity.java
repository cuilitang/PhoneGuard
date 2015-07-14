package cui.litang.phoneguard;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.Toast;

/**
 * 手机防盗-设置向导-界面-第四步
 * @author Cuilitang
 * @Date 2015年7月12日
 */
public class Setup4Activity extends BaseSetupActivity {


	private CheckBox cb_on_off;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup4);
		cb_on_off = (CheckBox) findViewById(R.id.cb_on_off);
		
		//回显
		boolean isStart = sp.getBoolean("start", false);
		if(isStart){
			cb_on_off.setChecked(true);
		}
		
		
		
		
		
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
		Editor edtor = sp.edit();
		if(cb_on_off.isChecked()){
			edtor.putBoolean("start", true);
		}else {
			Toast.makeText(this, "您尚未开启手机防盗服务！请开启服务，不然本功能无法正常使用!", Toast.LENGTH_SHORT).show();;
			return;
		}
		
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
