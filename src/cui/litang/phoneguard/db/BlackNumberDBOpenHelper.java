package cui.litang.phoneguard.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**黑名单功能 SQLite工具类
 * @author Cuilitang
 * @Date 2015年7月18日
 */
public class BlackNumberDBOpenHelper extends SQLiteOpenHelper {

	/**
	 * 构造
	 * @param context
	 */
	public BlackNumberDBOpenHelper(Context context) {
		
		super(context, "refuselist.db",null,1);
	}

	/**
	 * 初始化
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		db.execSQL("create table refuselist (id integer primary key autoincrement, number varchar(20), mode varchar(2))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Auto-generated method stub
		
	}

}
