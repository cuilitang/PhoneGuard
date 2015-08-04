package cui.litang.phoneguard.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**程序锁功能 SQLite工具类
 * @author Cuilitang
 * @Date 2015-8-4 19:32:06
 */
public class AppLockDBOpenHelper extends SQLiteOpenHelper {

	/**
	 * 构造
	 * @param context
	 */
	public AppLockDBOpenHelper(Context context) {
		
		super(context, "applock.db",null,1);
	}

	/**
	 * 初始化
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		db.execSQL("create table applock (_id integer primary key autoincrement,package_name varchar(20))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Auto-generated method stub
		
	}

}
