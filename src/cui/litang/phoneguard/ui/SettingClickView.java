package cui.litang.phoneguard.ui;

import cui.litang.phoneguard.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 自定义控件
 * @author Cuilitang
 * @Date 2015年7月16日
 */
public class SettingClickView extends RelativeLayout {
	
	private String namespace;
	private TextView tv_title;
	private TextView tv_desc;
	
	/**
	 * 构造，布局文件和样式使用的时候调用
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public SettingClickView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	/**
	 * 构造，布局文件使用的时候调用
	 * @param context
	 * @param attrs
	 */
	public SettingClickView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
		namespace = "http://schemas.android.com/apk/res/cui.litang.phoneguard";
		String title = attrs.getAttributeValue(namespace, "title");
		tv_title.setText(title);
		
		
		
	}
	
	/**
	 * 初始化控件
	 * @param context
	 */
	private void initView(Context context){
		
		View.inflate(context, R.layout.setting_click_view, this);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_desc = (TextView) findViewById(R.id.tv_desc);
	}
	
	/**
	 * 设置控件的描述信息
	 * @param desc 描述信息
	 */
	public void setDesc(String desc){
		tv_desc.setText(desc);
	}

	
}
