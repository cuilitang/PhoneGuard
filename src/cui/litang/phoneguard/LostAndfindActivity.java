package cui.litang.phoneguard;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class LostAndfindActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//1.输入密码界面  xml
		//2.询问是否设置了密码   读取SharedPerformence
		//3.设置密码，或者判断密码是否正确
		
		setContentView(R.layout.activity_lost_find);
		
		
	}

}
