package cui.litang.phoneguard;

import java.util.ArrayList;
import java.util.List;

import cui.litang.phoneguard.BlackListActivity.ViewHolder;
import cui.litang.phoneguard.engine.AppInfoProvider;
import cui.litang.phoneguard.entity.AppInfo;
import cui.litang.phoneguard.utils.DensityUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.renderscript.ProgramFragmentFixedFunction.Builder.Format;
import android.text.format.Formatter;
import android.util.Log;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * App管理
 * 1.ListView 列出所有软件
 * 2.可以执行卸载、启动、分享操作
 * @author Cuilitang
 * @Date 2015年7月21日
 */
public class AppManagerActivity extends Activity implements OnClickListener {

	private static final String TAG = "AppManagerActivity";
	protected ArrayList<AppInfo> userAppInfos;    //用户应用集合
	protected ArrayList<AppInfo> systemAppInfos;  //系统应用集合
	AppInfo appInfo;
	private AppManagerAdpter adapter;             //listView适配器
	private ListView lv_app_manager;   //程序列表
	private LinearLayout ll_loading;   //正在加载...
	private TextView tv_freeze_row;   //用户程序 XX个
	private TextView tv_avail_ram;		//可用内存
	private TextView tv_avail_sdcard;		//可用SD卡容量
	private PopupWindow popupWindow;    //弹出窗口
	
	private LinearLayout ll_start;   //弹出窗口-启动
	private LinearLayout ll_share;   //弹出窗口-分享
	private LinearLayout ll_uninstall;  //弹出窗口-下载
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_manager);
		ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
		lv_app_manager = (ListView) findViewById(R.id.lv_app_manager);
		tv_freeze_row = (TextView) findViewById(R.id.tv_freeze_row);
		tv_avail_ram = (TextView) findViewById(R.id.tv_avail_ram);
		tv_avail_sdcard = (TextView) findViewById(R.id.tv_avail_sdcard);
		tv_avail_ram = (TextView) findViewById(R.id.tv_avail_ram);
		tv_avail_sdcard = (TextView) findViewById(R.id.tv_avail_sdcard);
		tv_freeze_row = (TextView) findViewById(R.id.tv_freeze_row);
		
		//内存可用  XX sd卡可用XX
		long SDCardAvailSpace = getAvailSpace(Environment.getExternalStorageDirectory().getAbsolutePath());
		long RamAvailSpace = getAvailSpace(Environment.getDataDirectory().getAbsolutePath());
		
		tv_avail_ram.setText("内存可用："+Formatter.formatFileSize(AppManagerActivity.this, RamAvailSpace));
		tv_avail_sdcard.setText("SD可用："+Formatter.formatFileSize(AppManagerActivity.this, SDCardAvailSpace));
		
		fillData();
		
		//添加滚动监听
		lv_app_manager.setOnScrollListener(new OnScrollListener() {
			
			/**
			 * Callback method to be invoked while the list view or grid view is being scrolled. 
			 * If the view is being scrolled, this method will be called before the next frame of the scroll is rendered.
			 *  In particular, it will be called before any calls to Adapter.getView(int, View, ViewGroup).
			 *  render  ['rendə] n. 打底；交纳；粉刷   android. 渲染
			 */
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
			}
			
			/**
			 * Callback method to be invoked when the list or grid has been scrolled. 
			 * This will be called after the scroll has completed
			 * 
			 */
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				
				dismissPopupwindow();
				
				//position 大于用户程序程序个数时 显示系统程序个数
				if (userAppInfos!=null&&systemAppInfos!=null) {
					if(firstVisibleItem>userAppInfos.size()){
						tv_freeze_row.setText("系统程序:"+systemAppInfos.size()+"个");
					}else {
						tv_freeze_row.setText("用户程序:"+userAppInfos.size()+"个");
					}
				}
			}
		});
		
		//点击监听
		lv_app_manager.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				if(position == 0){
					return;
				}else if (position == userAppInfos.size()+1) {
					return;
				}else if (position <=userAppInfos.size()) {
					int newPosition = position -1;
					appInfo = userAppInfos.get(newPosition);
				}else {
					int newPosition = position -1 - userAppInfos.size() -1;
					appInfo = systemAppInfos.get(newPosition);
				}
				//popupWindow 显示 （打开，关闭，分享）
				//popup ['pɒp,ʌp] n. 弹出；
				
				dismissPopupwindow();
				
				View popupView = View.inflate(AppManagerActivity.this, R.layout.popupwindow_item_app_info, null);
				ll_start = (LinearLayout) popupView.findViewById(R.id.ll_start);
				ll_share = (LinearLayout) popupView.findViewById(R.id.ll_share);
				ll_uninstall = (LinearLayout) popupView.findViewById(R.id.ll_uninstall);
				
				ll_start.setOnClickListener(AppManagerActivity.this);
				ll_share.setOnClickListener(AppManagerActivity.this);
				ll_uninstall.setOnClickListener(AppManagerActivity.this);
				
				popupWindow = new PopupWindow(popupView,-2,-2);   //view width,height
				
				// 动画效果的播放必须要求窗体有背景颜色。
				// 透明颜色也是颜色      transparent 美 [træns'pærənt]adj. 透明的；显然的；坦率的；易懂的
				popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
				int[] location = new int[2];
				view.getLocationInWindow(location);     //本剧执行完之后将给location数组赋值，值是popupwindow的x，y的位置
				int dip = 60;
				int px = DensityUtil.dip2px(getApplicationContext(), dip);
				popupWindow.showAtLocation(parent, Gravity.LEFT|Gravity.TOP, px, location[1]);
				
				//添加ScaleAnimation    scale [skeɪl] android. 缩放   n. 规模；比例；   pivot ['pɪvət] n. 枢轴；中心点；旋转运动
				ScaleAnimation sa = new ScaleAnimation(0.3f, 1.0f, 0.3f, 1.0f, Animation.RELATIVE_TO_SELF,0,Animation.RELATIVE_TO_SELF,0.5f);
				sa.setDuration(100);
				//透明度动画
				AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);
				aa.setDuration(100);
				
				AnimationSet set = new AnimationSet(false);
				set.addAnimation(aa);
				set.addAnimation(sa);
				
				popupView.startAnimation(set);
			}
		});
		
		
		
		
	}
	
	
	@Override
	protected void onDestroy() {
		
		super.onDestroy();
		dismissPopupwindow();
	}
	
	/**
	 * 弹出窗口的点击事件
	 */
	@Override
	public void onClick(View v) {
		dismissPopupwindow();
		switch (v.getId()) {
		case R.id.ll_share:
			Log.i(TAG, "分享：" + appInfo.getName());
			shareApplication();
			break;
		case R.id.ll_start:
			Log.i(TAG, "启动：" + appInfo.getName());
			startApplication();
			break;
		case R.id.ll_uninstall:
			if(appInfo.isUserApp()){
			
				Log.i(TAG, "卸载：" + appInfo.getName());
				uninstallApplication();
			}else {
				Toast.makeText(this, "系统应用只有获取root权限才可以卸载", 0).show();
			}
			break;

		default:
			break;
		}
		
	}

	/**
	 * 分享程序
	 */
	private void shareApplication() {
		
		Intent intent = new Intent();
		intent.setAction("android.intent.action.SEND");
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, "推荐您使用一款软件，名称叫:"+appInfo.getName());
		startActivity(intent);
	}

	/**
	 * 启动程序
	 */
	private void startApplication() {

		PackageManager pm = getPackageManager();
		Intent intent = pm.getLaunchIntentForPackage(appInfo.getPackname());
		if(intent!=null){
			startActivity(intent);
		}else {
			Toast.makeText(this, "不能启动当前应用", 0).show();
		}
	}

	/**
	 * 卸载当前应用程序
	 */
	private void uninstallApplication() {
		
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		intent.setAction("android.intent.action.DELETE");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setData(Uri.parse("package:"+appInfo.getPackname()));
		startActivityForResult(intent, 0);
		
	}
	
	/**
	 * Same as calling startActivityForResult(Intent, int, Bundle) with no options.

		Parameters:
		intent The intent to start.
		requestCode If >= 0, this code will be returned in onActivityResult() when the activity exits.

	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		fillData();
		super.onActivityResult(requestCode, resultCode, data);
	}


	/**
	 * 关闭原来的弹出窗口
	 */
	protected void dismissPopupwindow() {
		
		if(popupWindow!=null&&popupWindow.isShowing()){
			
			popupWindow.dismiss();
			popupWindow = null;
		}
	}

	private long getAvailSpace(String absolutePath) {
		
		StatFs fs = new StatFs(absolutePath);
		fs.getBlockCount();
		long size = fs.getBlockSize();
		long blocks = fs.getAvailableBlocks();
		
		return size*blocks;
		
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
			
			
			if(position == 0){
				TextView tv = new TextView(AppManagerActivity.this);
				tv.setText("用户程序："+userAppInfos.size()+"个");
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				tv.setGravity(Gravity.CENTER);
				return tv;
			}else if(position == userAppInfos.size()+1){
				TextView tv = new TextView(AppManagerActivity.this);
				tv.setGravity(Gravity.CENTER);
				tv.setText("系统程序："+systemAppInfos.size()+"个");
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);
				return tv;
			}else if(position <= userAppInfos.size()){
				int newPositon = position - 1; //因为多了一个TextView占用了位置
				appInfo = userAppInfos.get(newPositon);
			}
			
			else{
				
				int newPositon = position - userAppInfos.size() - 2; //因为多了2个TextView占用了位置   
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
