package cui.litang.phoneguard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.color;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class SelectContactAcctivity extends Activity {
	
	private ListView lv ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_contact);
		
		lv = (ListView) findViewById(R.id.list_select_contact);
		final List<Map<String, String>> list  = getContactInfo();//取出联系人信息
		
		lv.setAdapter(new SimpleAdapter(this, list, R.layout.activity_select_contact, new String[]{"name","telnum"}, new int[]{R.id.tv_name,R.id.tv_phone}));
		
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				
				String phone = list.get(position).get("phone");
				Intent intent = new Intent();
				intent.putExtra("phone", phone);
				setResult(0, intent);
				finish();
				
				
			}
		});
		
		
		
		
		
	}

	private List<Map<String,String>>getContactInfo(){
		//联系人列表存储容器
		List<Map<String, String>> list  = new ArrayList<Map<String,String>>();
		//内容解析器
		ContentResolver resolver = getContentResolver();
		//系统存储的联系人的内容存放地址
		Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
		Uri uriData = Uri.parse("content://com.android.contacts/data");
		
		Cursor cursor = resolver.query(uri, new String[]{"contact_id"}, null, null, null);
		
		while(cursor.moveToNext()){
			String id = cursor.getString(0);
			if(id!=null){
				HashMap<String, String> contact = new HashMap<String,String>();
				Cursor dataCursor = resolver.query(uriData, new String[]{"data1","mimetype"}, "contact_id=?", new String[]{id}, null);
				while(dataCursor.moveToNext()){
					String data1 = dataCursor.getString(0);
					String mimeType = dataCursor.getString(1);
					if ("vnd.android.cursor.item/name".equals(mimeType)) {
						contact.put("name", data1);
					}else if ("vnd.android.cursor.item/phone_v2".equals(mimeType)) {
						contact.put("telnum", data1);
					}
				}
				list.add(contact);
				dataCursor.close();
			}
			
		}
		
		cursor.close();
		
		
		return list;
		
	}
}
