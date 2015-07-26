package cui.litang.phoneguard;

import java.util.ArrayList;
import java.util.List;

import cui.litang.phoneguard.engine.TaskInfoProvider;
import cui.litang.phoneguard.entity.TaskInfo;
import cui.litang.phoneguard.utils.SystemInfoUtils;
import android.os.Bundle;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TaskManagerActivity extends Activity {

	private TextView tv_task_count;
	private TextView tv_mem_info;
	private LinearLayout ll_loading;
	private ListView lv_task_manager;
	private TextView tv_task_status;
	
	private List<TaskInfo> allTaskInfos;
	private List<TaskInfo> userTaskInfos;
	private List<TaskInfo> systemTaskInfos;
	
	private TaskManagerAdapter adapter;
	

	private int taskCount;
	private long availMemo;
	private long totalMemo;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_manager);
		
		tv_mem_info = (TextView) findViewById(R.id.tv_mem_info);   //内存共计/可用
		tv_task_count = (TextView) findViewById(R.id.tv_task_count);  //进程格式
		ll_loading = (LinearLayout) findViewById(R.id.ll_loading);  //正在加载
		lv_task_manager = (ListView) findViewById(R.id.lv_task_manager);  //listview
		
		fillData();
		
		tv_task_status = (TextView) findViewById(R.id.tv_task_status);  //用户进程XX个
		
		//滚动监听
		lv_task_manager.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				//  Auto-generated method stub
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {


				if(userTaskInfos!=null&&systemTaskInfos!=null){
					if(firstVisibleItem>userTaskInfos.size()){
						tv_task_status.setText("系统进程:"+systemTaskInfos.size()+"个");
					}else {
						tv_task_status.setText("用户进程:"+userTaskInfos.size()+"个");
					}
				}
				
			}
		});
		
		//Item 点击监听
		lv_task_manager.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TaskInfo taskInfo;
				if(position == 0){
					return;
				}else if(position == userTaskInfos.size()+1){
					return;
				}else if (position<=userTaskInfos.size()) {
					taskInfo = userTaskInfos.get(position-1);
				}else {
					taskInfo = systemTaskInfos.get(position-1-userTaskInfos.size()-1);
				}
				
				if (getPackageName().equals(taskInfo.getPackageName())) {
					return;
				}
				ViewHolder holder = (ViewHolder) view.getTag();
				if(taskInfo.isChecked()){
					taskInfo.setChecked(false);
					holder.cb_task_status.setChecked(false);
				}else {
					taskInfo.setChecked(true);
					holder.cb_task_status.setChecked(true);
				}
				
			}
		});
		
	


	}
	
	
	
	/**
	 * 填充ListView 数据
	 */
	private void fillData() {
		ll_loading.setVisibility(View.VISIBLE);
	
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				allTaskInfos = TaskInfoProvider.getTaskInfos(TaskManagerActivity.this);
				userTaskInfos = new ArrayList<TaskInfo>();
				systemTaskInfos = new ArrayList<TaskInfo>();
				
				for (TaskInfo ti : allTaskInfos) {
					if(ti.isUserTask()){
						userTaskInfos.add(ti);
					}else {
						systemTaskInfos.add(ti);
					}
				}
				
				//更新设置页面
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						
						ll_loading.setVisibility(View.INVISIBLE);
						if(adapter == null){
							adapter = new TaskManagerAdapter();
							lv_task_manager.setAdapter(adapter);
						}else{
							adapter.notifyDataSetChanged();
						}
						
						setHeader();
					}

					
				});
				
				
			}
		}).start();
		
	}

	/**
	 * 设置进程和内存情况概览tv
	 */
	private void setHeader() {
		
		taskCount = SystemInfoUtils.getRunningProcessCount(this);
		tv_task_count.setText("运行中的进程:"+taskCount+"个");
		availMemo = SystemInfoUtils.getAvailMemo(this);
		totalMemo = SystemInfoUtils.getTotalMemo(this);
		tv_mem_info.setText("剩余/总内存："+Formatter.formatFileSize(this, availMemo)+"/"+Formatter.formatFileSize(this, totalMemo));
		
		
		
	}
	
	/**
	 * ListView 适配器
	 * @author Cuilitang
	 * @Date 2015年7月25日
	 */
	private class TaskManagerAdapter extends BaseAdapter{

		private SharedPreferences sp;

		@Override
		public int getCount() {
			
			/*
			sp = getSharedPreferences("config", MODE_PRIVATE);
			if(sp.getBoolean("show_system", false)){
				return userTaskInfos.size()+1+systemTaskInfos.size()+1;
			}else {
				return userTaskInfos.size()+1;
			}*/
			
			return userTaskInfos.size()+1+systemTaskInfos.size()+1;
		}

		@Override
		public Object getItem(int position) {
			// Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			TaskInfo taskInfo;
			if(position == 0){
				TextView tv = new TextView(TaskManagerActivity.this);
				tv.setBackgroundColor(Color.GRAY);
				tv.setTextColor(Color.WHITE);
				tv.setText("用户进程:"+userTaskInfos.size()+"个");
				tv.setGravity(Gravity.CENTER);
				return tv;
			}else if(position==userTaskInfos.size()+1){
				TextView tv = new TextView(TaskManagerActivity.this);
				tv.setBackgroundColor(Color.GRAY);
				tv.setTextColor(Color.WHITE);
				tv.setText("系统进程:"+systemTaskInfos.size()+"个");
				tv.setGravity(Gravity.CENTER);
				return tv;
			}else if (position<=userTaskInfos.size()) {
				taskInfo = userTaskInfos.get(position - 1);
			}else {
				taskInfo = systemTaskInfos.get(position - 1 - userTaskInfos.size()-1);
			}
			
			View view;
			ViewHolder viewHolder;
			if(convertView!=null&&convertView instanceof RelativeLayout){
				view = convertView;
				viewHolder = (ViewHolder)view.getTag();
			}else {
				view = View.inflate(getApplicationContext(), R.layout.listview_item_task_info, null);
				viewHolder = new ViewHolder();
				viewHolder.iv_task_icon = (ImageView) view.findViewById(R.id.iv_task_icon);
				viewHolder.tv_task_memsize = (TextView) view.findViewById(R.id.tv_task_memo_size);
				viewHolder.tv_task_name = (TextView) view.findViewById(R.id.tv_task_name);
				viewHolder.cb_task_status = (CheckBox) view.findViewById(R.id.cb_task_status); 
				view.setTag(viewHolder);
			}
			
			viewHolder.iv_task_icon.setImageDrawable(taskInfo.getIcon());
			viewHolder.tv_task_memsize.setText("内存占用:"+Formatter.formatFileSize(TaskManagerActivity.this, taskInfo.getMemoSize()));
			viewHolder.tv_task_name.setText(taskInfo.getName());
			viewHolder.cb_task_status.setChecked(taskInfo.isChecked());
			if(getPackageName().equals(taskInfo.getPackageName())){
				viewHolder.cb_task_status.setVisibility(View.INVISIBLE);
			}else {
				viewHolder.cb_task_status.setVisibility(View.VISIBLE);
			}
			return view;
		}

		
		
	}
	
	/**
	 * 缓存ListView的View对象进行复用
	 */
	static class ViewHolder {
		ImageView iv_task_icon;
		TextView tv_task_name;
		TextView tv_task_memsize;
		CheckBox cb_task_status;
	}
	
	/**
	 * 全选按钮相应方法
	 * @param view
	 */
	public void selectAll(View view) {
		
		for (TaskInfo taskInfo : allTaskInfos) {
			
			if(getPackageName().equals(taskInfo.getPackageName())){
				continue;
			}
			
			taskInfo.setChecked(true);
		}
		adapter.notifyDataSetChanged();
		
	}
	
	/**
	 * 反选按钮相应方法
	 * @param view
	 */
	public void invertSelection(View view) {
		
		for (TaskInfo taskInfo : allTaskInfos) {
			
			if(getPackageName().equals(taskInfo.getPackageName())){
				continue;
			}else if(taskInfo.isChecked()){
				taskInfo.setChecked(false);
			}else {
				taskInfo.setChecked(true);
			}
			
			
		}
		adapter.notifyDataSetChanged();
		
	}
	

	/**
	 * 清理按钮相应方法
	 * @param view
	 */
	public void killTask(View view) {
		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		int count = 0;
		int savedMemo = 0;
		ArrayList<TaskInfo> killedList = new ArrayList<TaskInfo>();
		for (TaskInfo taskInfo : allTaskInfos) {
			if(taskInfo.isChecked()){
				am.killBackgroundProcesses(taskInfo.getPackageName());   //后台执行杀死动作
				
				if(taskInfo.isUserTask()){           //更新ui用     
					userTaskInfos.remove(taskInfo);
				}else {								//更新ui用
					systemTaskInfos.remove(taskInfo);
				}
				killedList.add(taskInfo);
				count++;
				savedMemo += taskInfo.getMemoSize();
			}
		}
		
		allTaskInfos.removeAll(killedList);  //后台杀死的时候是 不分user和system的，直接按照allTaskList的isChecked属性状态进行删除，再次点击清除的时候会再清一遍。所以必须remove掉。
		adapter.notifyDataSetChanged();   //更新ui的时候，直接进adapter，所以需要更新userTaskList和systemTaskList
		
		Toast.makeText(this, "杀死了"+count+"个进程，释放了"+Formatter.formatFileSize(this, savedMemo), Toast.LENGTH_SHORT).show();
		
		taskCount -= count;
		availMemo += savedMemo;
		
		tv_task_count.setText("运行中的进程:"+taskCount+"个");
		tv_mem_info.setText("剩余/总内存："+Formatter.formatFileSize(this, availMemo)+"/"+Formatter.formatFileSize(this, totalMemo));
	}

}
