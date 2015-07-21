package cui.litang.phoneguard;

import java.util.ArrayList;
import java.util.List;

import cui.litang.phoneguard.BlackListActivity.ViewHolder;
import cui.litang.phoneguard.engine.AppInfoProvider;
import cui.litang.phoneguard.entity.AppInfo;
import android.os.Bundle;
import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * App管理
 * 1.ListView 列出所有软件
 * 2.可以执行卸载、启动、分享操作
 * @author Cuilitang
 * @Date 2015年7月21日
 */
public class AppManagerActivity extends Activity {

	protected ArrayList<AppInfo> userAppInfos;    //用户应用集合
	protected ArrayList<AppInfo> systemAppInfos;  //系统应用集合
	private AppManagerAdpter adapter;             //listView适配器
	private ListView lv_app_manager;   //程序列表
	private LinearLayout ll_loading;   //正在加载...
	private TextView tv_freeze_row;   //用户程序 XX个
	private TextView tv_avail_ram;		//可用内存
	private TextView tv_avail_sdcard;		//可用SD卡容量
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_manager);
		ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
		lv_app_manager = (ListView) findViewById(R.id.lv_app_manager);
		tv_freeze_row = (TextView) findViewById(R.id.tv_freeze_row);
		tv_avail_ram = (TextView) findViewById(R.id.tv_avail_ram);
		tv_avail_sdcard = (TextView) findViewById(R.id.tv_avail_sdcard);
		
		
		fillData();
		
		
		
		
	}

	private void fillData() {
		
		ll_loading.setVisibility(View.VISIBLE);
		
		new Thread(){
			public void run() {
				List<AppInfo> appInfos = AppInfoProvider.getAppInfos(AppManagerActivity.this);
				userAppInfos = new ArrayList<AppInfo>();
				systemAppInfos = new ArrayList<AppInfo>();
				
				for (AppInfo appInfo : appInfos) {
					if(appInfo.isUserApp()){
						userAppInfos.add(appInfo);
					}else {
						systemAppInfos.add(appInfo);
					}
				}
				
				//更新UI
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						if(adapter == null){
							adapter = new AppManagerAdpter();
							lv_app_manager.setAdapter(adapter);
						}else {
							adapter.notifyDataSetChanged();
						}
						ll_loading.setVisibility(View.INVISIBLE);
						
					}
				});
			};
		}.start();
		
		
	}
	
	/**
	 * ListView适配器
	 * @author Cuilitang
	 * @Date 2015年7月21日
	 */
	private class AppManagerAdpter extends BaseAdapter{

		@Override
		public int getCount() {

			return userAppInfos.size() +1 +systemAppInfos.size()+1;
		}

		@Override
		public Object getItem(int position) {
			//  Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			//  Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			AppInfo appInfo;
			if(position == 0){
				TextView tv = new TextView(AppManagerActivity.this);
				tv.setText("用户程序："+userAppInfos.size()+"个");
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				return tv;
			}else if(position == userAppInfos.size()+1){
				TextView tv = new TextView(AppManagerActivity.this);
				tv.setText("系统程序："+systemAppInfos.size()+"个");
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				return tv;
			}else if(position <= userAppInfos.size()){
				int newPositon = position - 1; //因为多了一个TextView占用了位置
				appInfo = userAppInfos.get(newPositon);
			}
			
			else{
				
				int newPositon = position - 1; //因为多了一个TextView占用了位置   
				appInfo = systemAppInfos.get(newPositon);
			}
			
			View view;
			ViewHolder viewHolder;
			
			if(convertView!=null&&convertView instanceof RelativeLayout){
				view = convertView;
				viewHolder = (ViewHolder) view.getTag();
			}else {
				view = View.inflate(AppManagerActivity.this, R.layout.listview_item_app_info, null);
				viewHolder = new ViewHolder();
				viewHolder.iv_icon = (ImageView) view.findViewById(R.id.iv_app_icon);
				viewHolder.tv_location = (TextView) view.findViewById(R.id.tv_app_location);
				viewHolder.tv_name = (TextView) view.findViewById(R.id.tv_app_name);
				
				view.setTag(viewHolder);
			}
			
			viewHolder.iv_icon.setImageDrawable(appInfo.getIcon());
			viewHolder.tv_name.setText(appInfo.getName());
			if(appInfo.isInRAM()){
				
				viewHolder.tv_location.setText("手机内存");
			}else {
				viewHolder.tv_location.setText("外部存储");
			}			
			
			
			return view;
		}
		
	}
	
	static class ViewHolder {
		TextView tv_name;
		TextView tv_location;
		ImageView iv_icon;
	}
	

}
