package cui.litang.phoneguard;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public abstract class BaseSetupActivity extends Activity {
	private GestureDetector gd;
	
	protected SharedPreferences sp;   //声明出来给子类用

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
	
	/**
	 * 将子类中的滑动翻页抽过来
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", MODE_PRIVATE);  //得到一个实例
		
		gd = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener(){
			
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				if(Math.abs(velocityX)<200){
					Toast.makeText(getApplicationContext(), "滑的太慢了", Toast.LENGTH_SHORT).show();
					 
				}
				
				else if(Math.abs(e2.getRawY()-e1.getRawY())>100){
					Toast.makeText(getApplicationContext(), "请左右水平滑动", Toast.LENGTH_SHORT).show();
					
				}
				
				else if(e2.getRawX()-e1.getRawX()>200){
					showPre();
					
				}
				
				else if(e1.getRawX()-e2.getRawX()>200){
					
					showNext();
					
				}
				
				return super.onFling(e1, e2, velocityX, velocityY);
			}
			
		}); 
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		gd.onTouchEvent(event);
		return super.onTouchEvent(event);
		
	}

}
