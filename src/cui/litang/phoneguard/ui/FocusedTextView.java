package cui.litang.phoneguard.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewDebug.ExportedProperty;
import android.widget.TextView;

/**
 * 自定义一个TextView，使其有焦点。
 * @author Cuilitang
 * @Date 2015年7月3日
 */
public class FocusedTextView extends TextView{

	public FocusedTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	

	public FocusedTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}



	public FocusedTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}



	/**
	 * 判断是否有焦点，直接返回true
	 */
	@Override
	@ExportedProperty(category="focus")
	public boolean isFocused() {
		return true;
	}
	
	

}
