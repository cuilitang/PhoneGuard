package cui.litang.phoneguard;

import cui.litang.phoneguard.db.TelAreaQueryUtils;
import android.os.Bundle;
import android.app.Activity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class TelAreaActivity extends Activity {

	private EditText et_tel;
	private TextView tv_area;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tel_area);
		
		et_tel = (EditText) findViewById(R.id.et_tel);
		tv_area = (TextView) findViewById(R.id.tv_area);
		
		et_tel.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(s!=null&&s.length()>3){
					String area = TelAreaQueryUtils.queryNumber(s.toString());
					tv_area.setText(area);
				}
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
	
	}

	
	
	/**
	 * 查询手机号码归属地
	 * @param view
	 */
	public void numberAddressQuery(View view) {
		String tel = et_tel.getText().toString().trim();
		if(TextUtils.isEmpty(tel)){
			Toast.makeText(this, "号码不能为空", Toast.LENGTH_SHORT).show();
			Animation sharke = AnimationUtils.loadAnimation(this, R.anim.shake);
			et_tel.startAnimation(sharke);
			return;
		}else {
			String area = TelAreaQueryUtils.queryNumber(tel);
			tv_area.setText(area);
		}
		
	}

}
