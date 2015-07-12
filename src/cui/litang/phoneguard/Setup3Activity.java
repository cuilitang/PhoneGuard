package cui.litang.phoneguard;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 手机防盗-设置向导-界面-第三步
 * @author Cuilitang
 * @Date 2015年7月12日
 */
public class Setup3Activity extends BaseSetupActivity {
	
	private EditText ed_telnum;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup3);
		//回显
		ed_telnum = (EditText) findViewById(R.id.ed_telnum);
		ed_telnum.setText(sp.getString("telnum", null));
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.setup3, menu);
		return true;
	}

	@Override
	public void showNext() {
		
		//判空
		String telnum = ed_telnum.getText().toString().trim();
		if(TextUtils.isEmpty(telnum)){
			Toast.makeText(this, "安全号码不能为空，请设置安全号码", 0).show();
			return;
		}
		//保存安全号码
		Editor editor = sp.edit();
		editor.putString("telnum", telnum);
		editor.commit();
		
		Intent intent = new Intent(Setup3Activity.this, Setup4Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
		
	}

	@Override
	public void showPre() {
		Intent intent = new Intent(Setup3Activity.this, Setup2Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
		
	}
	
	/**
	 * 按钮的onClick事件,启动联系人列表页面
	 * @param view  按钮
	 */
	public void selectContact(View view) {
			Intent intent = new Intent(this,SelectContactAcctivity.class);
			startActivityForResult(intent, 0);
			

	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(data == null){
			return;
		}
		
		String phoneNum = data.getStringExtra("phone").toString().replace("-", "");
		ed_telnum.setText(phoneNum);
	}

}
