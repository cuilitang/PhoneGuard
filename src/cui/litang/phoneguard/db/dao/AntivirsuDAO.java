package cui.litang.phoneguard.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AntivirsuDAO {

	/**
	 * 查询一个md5是否在病毒数据库里面存在
	 * @param md5 
	 * @return
	 */
	public static boolean isVirus(String md5){
		String path = "/data/data/cui.litang.phoneguard/files/antivirus.db";
		boolean result = false;
		//打开病毒数据库文件
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select * from datable where md5=?", new String[]{md5});
		if(cursor.moveToNext()){
			result = true;
		}
		cursor.close();
		db.close();
		return result;
	}

}
