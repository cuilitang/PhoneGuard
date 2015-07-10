package cui.litang.phoneguard;

import cui.litang.phoneguard.utils.MD5Utils;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends Activity {
	
	private GridView gv_jiugongge;
	private MyAdapter adapter;
	private static String [] names = {
		"手机防盗","通讯卫士","软件管理",
		"进程管理","流量统计","手机杀毒",
		"缓存清理","高级工具","设置中心"
		
	};
	private static int[] ids = {
		R.drawable.safe,R.drawable.callmsgsafe,R.drawable.app,
		R.drawable.taskmanager,R.drawable.netmanager,R.drawable.trojan,
		R.drawable.sysoptimize,R.drawable.atools,R.drawable.settings
		
	};
	
	private AlertDialog dialog; //进入手机防盗时候用到的密码对话框 
	private SharedPreferences sp;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		
		gv_jiugongge = (GridView) findViewById(R.id.gv_jiugongge);
		adapter = new MyAdapter();
		gv_jiugongge.setAdapter(adapter);
		
		gv_jiugongge.setOnItemClickListener(new OnItemClickListener() {

			private Intent intent;
			private EditText et_setup_pwd;
			private EditText et_setup_confirm;
			private Button ok;
			private Button cancel;

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 0://手机防盗功能：若未设置进入密码则设置密码，若已设置密码，则进行密码校验
					showSetupPwdDialog();
					break;
				
				case 8://进入设置中心
					intent = new Intent(HomeActivity.this,SettingActivity.class);
					startActivity(intent);
					break;

				default:
					break;
				}
			}

			/**
			 * 设置密码对话框
			 */
			private void showSetupPwdDialog() {
				AlertDialog.Builder builder = new Builder(HomeActivity.this);
				View view = View.inflate(HomeActivity.this, R.layout.dialog_setup_password, null);
				
				et_setup_pwd = (EditText) view.findViewById(R.id.et_setup_pwd);
				et_setup_confirm = (EditText) view.findViewById(R.id.et_setup_confirm);
				ok = (Button) view.findViewById(R.id.ok);
				cancel = (Button) view.findViewById(R.id.cancel);
				
				
				cancel.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						
					}
				});
				ok.setOnClickListener(new OnClickListener() {
					

					@Override
					public void onClick(View v) {
						//取出密码，进行非空校验，一致性校验
						//存储到SharedPerformance
						
						String pwd = et_setup_pwd.getText().toString().trim();
						String repwd = et_setup_confirm.getText().toString().trim();
						if (TextUtils.isEmpty(pwd)||TextUtils.isEmpty(repwd)) {
							Toast.makeText(HomeActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
							
						} else if (pwd.equals(repwd)) { //消掉对话框，进入手机防盗功能
							Editor editor = sp.edit();
							editor.putString("password", MD5Utils.md5Password(pwd));
							editor.commit();
							dialog.dismiss();
							
							Intent intent = new Intent(HomeActivity.this, LostAndfindActivity.class);
							startActivity(intent);
							
						}else{
							
							Toast.makeText(HomeActivity.this, "密码不一致", Toast.LENGTH_SHORT).show();
							
						}
					}
				});
				
				dialog = builder.create();
				dialog.setView(view,0,0,0,0);
				dialog.show();
				
				
				
			}

			
		});
		
		
		
		
	}
	
	private class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return names.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(HomeActivity.this, R.layout.list_item_home, null);
			ImageView iv_item = (ImageView) view.findViewById(R.id.iv_item);
			TextView tv_item = (TextView) view.findViewById(R.id.tv_item);
			
			tv_item.setText(names[position]);
			iv_item.setImageResource(ids[position]);
			
			return view;
		}
		
	}

}
