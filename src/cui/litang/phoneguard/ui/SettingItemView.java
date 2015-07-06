package cui.litang.phoneguard.ui;

import cui.litang.phoneguard.R;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 自定义控件
 * @author Cuilitang
 * @Date 2015年7月2日
 */
public class SettingItemView extends RelativeLayout {

	private static final String TAG = "SettingItemView";
	private CheckBox cb_status;
	private TextView tv_desc;
	private TextView tv_title;
	private String namespace;
	private String on;
	private String off;

	public SettingItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	public SettingItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
		namespace = "http://schemas.android.com/apk/res/cui.litang.phoneguard";
		String title = attrs.getAttributeValue(namespace, "title");
		on = attrs.getAttributeValue(namespace, "on");
		off = attrs.getAttributeValue(namespace, "off");
		tv_title.setText(title);
		Log.i(TAG, title);
		Log.i(TAG, on);
		Log.i(TAG, off);
	}

	public SettingItemView(Context context) {
		super(context);
		initView(context);
	}
	
	private void initView(Context context){
		View.inflate(context, R.layout.setting_item_view, this);
		cb_status = (CheckBox) findViewById(R.id.cb_status);
		tv_desc = (TextView) findViewById(R.id.tv_desc);
		tv_title = (TextView) findViewById(R.id.tv_title);
		
		
		
	}
	
	
	/**
	 * 取得控件状态
	 * @return 是否被勾选
	 */
	public boolean isChecked(){
		return cb_status.isChecked();
	}
	
	/**
	 * 设置控件状态
	 * @param checked 是否勾选的布尔值
	 */
	public void setCheck(boolean checked){
		cb_status.setChecked(checked);
		if(checked){
			setDesc(on);
		}else {
			setDesc(off);
		}
	}
	
	/**
	 * 设置控件的描述信息
	 * @param desc 描述信息
	 */
	public void setDesc(String desc){
		tv_desc.setText(desc);
	}
	

	
}
