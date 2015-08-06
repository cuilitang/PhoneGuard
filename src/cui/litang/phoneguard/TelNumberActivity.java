package cui.litang.phoneguard;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cui.litang.phoneguard.db.dao.TelNumberDAO;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

public class TelNumberActivity extends Activity {
	
	protected static final String TAG = "CommonNumActivity";
	private ExpandableListView elv_tel_num;//可扩展的ListView
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_tel_number);
		elv_tel_num = (ExpandableListView) findViewById(R.id.elv_tel_num);
		elv_tel_num.setAdapter(new TelNumberAdapter());
		
		elv_tel_num.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				
				TextView tv = (TextView) v;
				String number = tv.getText().toString().split("\n")[1];
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_DIAL);
				intent.setData(Uri.parse("tel:"+number));
				startActivity(intent);
				return false;
			}
		});
	}

	private class TelNumberAdapter extends BaseExpandableListAdapter{
		
		//存储对应组中的子孩子的详细信息
		private List<String> groupNames;
		//将子孩子的所有信息一次性从数据库中获取出来，这样可以避免重复查询数据库内存缓存集合。key：分组的位置  value：分组里面所有子孩子的信息
		private Map<Integer, List<String>> childrenCache; 
		
		public TelNumberAdapter() {
			childrenCache = new HashMap<Integer, List<String>>();
		}

		/**
		 *一级列表数量
		 */
		@Override
		public int getGroupCount() {
			
			return TelNumberDAO.getGroupCount();
		}

		/**
		 * 二级列表数量
		 */
		@Override
		public int getChildrenCount(int groupPosition) {

			return TelNumberDAO.getChildrenCount(groupPosition);
		}

		/**
		 * 返回分组（二级列表）所对应的对象。这里我们用不到，所以返回null。
		 */
		@Override
		public Object getGroup(int groupPosition) {
			// Auto-generated method stub
			return null;
		}
		
		/**
		 * 获取分组中的条目对象。这里我们用不到，所以返回null。
		 */
		@Override
		public Object getChild(int groupPosition, int childPosition) {
			//  Auto-generated method stub
			return null;
		}

		/**
		 * 获取分组所对应的id
		 */
		@Override
		public long getGroupId(int groupPosition) {

			return groupPosition;
		}

		/**
		 * 获取分组中的条目所对应的id
		 */
		@Override
		public long getChildId(int groupPosition, int childPosition) {
			
			return childPosition;
		}

		/**
		 * 是否要为分组中的条目设置一下id。false代表不用设置。   stable ['steɪb(ə)l]  n. 马厩；牛棚
		 */
		@Override
		public boolean hasStableIds() {
			
			return false;
		}

		
		/**
		 * 返回每一个分组的view对象.
		 * 参数一：当前分组的id
		 * 参数二：当前分组的View是否可扩展
		 * 参数三：缓存的View对象
		 * 参数四：当前分组的父View对象
		 */
		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			
			TextView tv;
			if(convertView == null){
				tv = new TextView(TelNumberActivity.this);
			}else {
				tv = (TextView) convertView;
			}
			tv.setTextSize(28);
			
			if(groupNames != null){
				tv.setText("　　"+groupNames.get(groupPosition));
			}else {
				groupNames = TelNumberDAO.getGroupNames();
				tv.setText("　　"+groupNames.get(groupPosition));
			}
			return tv;
		}

		
		/**
		 * 返回每一个分组 某一个位置对应的孩子的view对象
		 * 参数一：当前分组的id
		 * 参数二：分组中的子孩子的id
		 * 参数三：分组中的子孩子是否是最后一个
		 * 参数四：子孩子View的缓存对象
		 * 参数五：分组中的子孩子所在的父View对象
		 */
		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			
			TextView tv;
			if (convertView == null) {
				
				tv = new TextView(TelNumberActivity.this);
			}else {
				
				tv = (TextView) convertView;
			}
			tv.setTextSize(20);
			
			String result = null;
			if(childrenCache.containsKey(groupPosition)){
				
				result = childrenCache.get(groupPosition).get(childPosition);
			}else {
				
				List<String> children = TelNumberDAO.getChildrenByPosition(groupPosition);
				result = children.get(childPosition);
				
				childrenCache.put(groupPosition, children); //将数据存缓存
			}
			tv.setText(result);
			return tv;
		}

		/**
		 * 返回值如果为true，则表示每个分组的子孩子都可以响应到点击事件，否则，不可以响应
		 */
		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {

			return true;
		}
		
	}
}
