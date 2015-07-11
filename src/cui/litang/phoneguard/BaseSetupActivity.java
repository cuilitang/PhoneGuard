package cui.litang.phoneguard;

import android.app.Activity;
import android.view.View;

public abstract class BaseSetupActivity extends Activity {
	public abstract void showNext();
	public abstract void showPre();
	/**
	 * 下一步的点击事件
	 * @param view
	 */
	public void next(View view){
		showNext();
		
	}
	
	/**
	 *   上一步
	 * @param view
	 */
	public void pre(View view){
		showPre();
		
	}

}
