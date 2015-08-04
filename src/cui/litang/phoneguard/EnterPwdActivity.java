package cui.litang.phoneguard;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 看门狗界面
 * @author Cuilitang
 * @Date 2015年8月4日
 */
public class EnterPwdActivity extends Activity {
	
	private EditText et_password;
	private String packageName;
	private TextView tv_name;
	private ImageView iv_icon;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enter_pwd);
		
		et_password = (EditText) findViewById(R.id.et_password);
		Intent intent = getIntent();
		packageName = intent.getStringExtra("package_name");
		tv_name = (TextView) findViewById(R.id.tv_name);
		iv_icon = (ImageView) findViewById(R.id.iv_icon);
		PackageManager pm = getPackageManager();
		try {
			ApplicationInfo info = pm.getApplicationInfo(packageName, 0);
			tv_name.setText(info.loadLabel(pm));
			iv_icon.setImageDrawable(info.loadIcon(pm));
		} catch (NameNotFoundException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	//回桌面
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent intent = new Intent();
		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.HOME");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addCategory("android.intent.category.MONKEY");
		startActivity(intent);
		//所有的activity最小化 不会执行ondestory 只执行 onstop方法。
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		finish();
	}
	
	public void ok(View view){
		String pwd = et_password.getText().toString().trim();
		if(TextUtils.isEmpty(pwd)){
			Toast.makeText(this, "密码不能为空", 0).show();
			return;
		}
		
		//告诉看门狗这个程序密码输入正确了。 可以临时的停止保护。
		//自定义的广播,临时停止保护。
		if ("123".equals(pwd)) {   //此处密码是死的，应该设置
			
			Intent intent = new Intent();
			intent.setAction("cui.litang.phonegrund.LOGINED");
			intent.putExtra("logined_name", packageName);
			sendBroadcast(intent);
			finish();
		}else {
			Toast.makeText(this, "密码错误", 0).show();
		}
	}

}
