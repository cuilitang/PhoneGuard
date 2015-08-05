package cui.litang.phoneguard.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import cui.litang.phoneguard.db.AppLockDBOpenHelper;
import cui.litang.phoneguard.db.BlackNumberDBOpenHelper;

/**
 * 上锁应用号码Dao
 * @author Cuilitang
 * @Date 2015-8-4 19:32:15
 */
public class AppLockDAO {
	
	private static final String TAG = "ApplockDao";
	private AppLockDBOpenHelper helper;
	private Context context;
	
	/**
	 * 构造
	 * @param context 上下文
	 */
	public AppLockDAO(Context context) {
		
		helper = new AppLockDBOpenHelper(context);
		this.context = context;
	}
	


	
	/**
	 * 保存新的受保护程序的包名
	 * @param packageName  包名
	 */
	public void save(String packageName) {
		
		SQLiteDatabase database = helper.getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put("package_name", packageName);
		database.insert("applock", null, contentValues);
		database.close();
		Intent intent = new Intent();
		intent.setAction("cui.litang.phonegrund.APPLOCK_CHANGE");
		context.sendBroadcast(intent);
	}
	
	
	
	/**
	 * 删除包名
	 * @param number 要删除的包名
	 */
	public void delete(String packageName) {
		
		SQLiteDatabase database = helper.getWritableDatabase();
		database.delete("applock", "package_name=?", new String[]{packageName});
		database.close();
		Intent intent = new Intent();
		intent.setAction("cui.litang.phonegrund.APPLOCK_CHANGE");
		context.sendBroadcast(intent);
	}
	
	/**
	 * 查询一条程序锁包名记录是否存在
	 * @param packname
	 * @return
	 */
	public boolean find(String packName){
		boolean result = false;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query("applock", null, "package_name=?", new String[]{packName}, null, null, null);
		if(cursor.moveToNext()){
			result = true;
		}
		cursor.close();
		db.close();
		return result;
	}
	/**
	 * 查询全部的包名
	 * @param packname
	 * @return
	 */
	public List<String> findAll(){
		List<String> protectPacknames = new ArrayList<String>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.query("applock", new String[]{"package_name"}, null,null, null, null, null);
		while(cursor.moveToNext()){
			protectPacknames.add(cursor.getString(0));
		}
		cursor.close();
		db.close();
		return protectPacknames;
	}
	
	
	
	

}
