package cui.litang.phoneguard;

import java.util.List;

import cui.litang.phoneguard.db.dao.BlackNumberDao;
import cui.litang.phoneguard.entity.RefuseEntity;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class BlackListActivity extends Activity {

	protected static final String TAG = "BlackListActivity";
	private AlertDialog dialog;
	private Button cancel;
	private Button ok;
	private CheckBox cb_refuse_call;
	private CheckBox cb_refuse_sms;
	private EditText et_black_number;
	private BlackNumberDao dao;
	private List<RefuseEntity> refuseEntities;
	private BlackListAdapter blackListAdapter;
	private ListView lv_black_list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_black_list);
		dao = new BlackNumberDao(BlackListActivity.this);
		refuseEntities = dao.loadAll();
		blackListAdapter = new BlackListAdapter();
		lv_black_list = (ListView) findViewById(R.id.lv_black_list);
		lv_black_list.setAdapter(blackListAdapter);
		lv_black_list.setOnItemLongClickListener(new OnItemLongClickListener() {

			/**
			 * Parameters:
				parent The AbsListView where the click happened
				view The view within the AbsListView that was clicked
				position The position of the view in the list
				id The row id of the item that was clicked
				Returns:
				true if the callback consumed the long click, false otherwise
			 */
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				RefuseEntity refuseEntity = refuseEntities.get(position);
				Toast.makeText(BlackListActivity.this, refuseEntity.getNumber()+"本来准备做长按修改的，但是进度迟滞，需要赶进度，所以就这样吧。", Toast.LENGTH_SHORT).show();
				return true;
			}
		});
		

	}

	/**
	 * ListView适配器
	 * @author Cuilitang
	 * @Date 2015年7月18日
	 */
	class BlackListAdapter extends BaseAdapter{

		@Override
		public int getCount() {

			return refuseEntities.size();
		}
		
		/**
		 * Get a View that displays the data at the specified position in the data set. You can either create a View manually or inflate it from an XML layout file. When the View is inflated, the parent View (GridView, ListView...) will apply default layout parameters unless you use 
		 */
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder viewHolder;
			if(convertView == null){
				view = View.inflate(BlackListActivity.this, R.layout.listview_item_black_number, null);
				viewHolder = new ViewHolder();
				viewHolder.tv_number = (TextView) view.findViewById(R.id.tv_black_number);
				viewHolder.tv_mode = (TextView) view.findViewById(R.id.tv_mode);
				viewHolder.iv_delete = (ImageView) view.findViewById(R.id.iv_delete);
				view.setTag(viewHolder);
			}
			
			else {
				view = convertView;
				viewHolder = (ViewHolder) view.getTag();
			}
			
			viewHolder.tv_number.setText(refuseEntities.get(position).getNumber());
			String mode = refuseEntities.get(position).getMode();
			
			Log.i(TAG, mode);
			
			if ("3".equals(mode)) {
				viewHolder.tv_mode.setText("全部拦截");
			} else if("2".equals(mode)){
				viewHolder.tv_mode.setText("电话拦截");
			}else {
				viewHolder.tv_mode.setText("短信拦截");
			}
			
			//为删除图像增加点击监听
			viewHolder.iv_delete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					AlertDialog.Builder  builder = new Builder(BlackListActivity.this);
					builder.setTitle("警告");
					builder.setMessage("你确定要删除这条数据么？");
					builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dao.delete(refuseEntities.get(position).getNumber());
							refuseEntities.remove(position);
							blackListAdapter.notifyDataSetChanged();
							
						}
					});
					
					builder.setNegativeButton("取消",null);
					builder.show();
				}
			});
					
			
			return view;
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
	}
	
	/**
	 * 缓存view的成员元素对象在内存中
	 * @author Cuilitang
	 * @Date 2015年7月18日
	 */
	static class ViewHolder{
		TextView tv_number;
		TextView tv_mode;
		ImageView iv_delete;
	}
	
	/**
	 * 添加按钮相应时间，弹出添加号码Dialog
	 * @param view
	 */
	public void addBlackNumber(View view) {
		
		AlertDialog.Builder builder = new Builder(this);
		dialog = builder.create();
		View dialogView = View.inflate(this, R.layout.dialog_add_black_number, null);
		dialog.setView(dialogView);
		dialog.show();
		addDialoglistener(dialogView);
		
	}
	/**
	 * 为弹出的添加黑名单dialog添加监听事件
	 */
	private void addDialoglistener(View dialogView) {
		cancel = (Button) dialogView.findViewById(R.id.cancel);
		ok = (Button) dialogView.findViewById(R.id.ok);
		cb_refuse_call = (CheckBox) dialogView.findViewById(R.id.cb_refuse_call);
		cb_refuse_sms = (CheckBox) dialogView.findViewById(R.id.cb_refuse_sms);
		et_black_number = (EditText) dialog.findViewById(R.id.et_black_number);
		
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				dialog.dismiss();
			}
		});
		
		ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//1.获取EditText号码，判空;获取checkBox状态，判空
				//2.将数据存储到SQlite中,SQLite工具类
				//3.更新ListView
				//4.关闭对话框				
//1
				String blackNumber = et_black_number.getText().toString().trim();
				boolean isRefuseCall = cb_refuse_call.isChecked();
				boolean isRefuseSms = cb_refuse_sms.isChecked();
				if(TextUtils.isEmpty(blackNumber)){
					Toast.makeText(BlackListActivity.this, "号码不能为空", 0).show();
					return;
				}
				if(!(isRefuseCall||isRefuseSms)){
					Toast.makeText(BlackListActivity.this, "请至少勾选一种拦截类型", 0).show();
					return;
				}
				
				String mode;
				if(isRefuseCall&&isRefuseSms){
					mode = "3";   //全部拦截
					Log.i(TAG, "全部拦截");
				}else if (isRefuseCall) {
					mode = "2";   //拦截电话
					Log.i(TAG, "拦截电话");
				}else {
					mode = "1"; //拦截短信
					Log.i(TAG, "拦截短信");
				}
				
//2				
				RefuseEntity refuseEntity = new RefuseEntity();
				refuseEntity.setNumber(blackNumber);
				refuseEntity.setMode(mode);
				dao.save(refuseEntity);
//3
				refuseEntities.add(refuseEntity);
				blackListAdapter.notifyDataSetChanged();
//4
				dialog.dismiss();
			}
		});
		
		
		
	}

}
