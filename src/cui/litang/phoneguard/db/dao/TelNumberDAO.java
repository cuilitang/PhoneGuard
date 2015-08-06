package cui.litang.phoneguard.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class TelNumberDAO {
	
	private static String path;
	private static SQLiteDatabase db;
	public static void openDatabase(){
		path = "/data/data/cui.litang.phoneguard/files/commonnum.db";
		db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
	}

	/**
	 * 一级列表数量
	 * @return
	 */
	public static int getGroupCount() {
		
		openDatabase();
		
		int count = 0;
		if(db.isOpen()){
			Cursor cursor = db.rawQuery("select * from classlist", null);
			count = cursor.getCount();
			cursor.close();
			db.close();
		}
		return count;
	}

	/**
	 * 二级列表数量
	 * @return
	 */
	public static int getChildrenCount(int groupPosition) {
		
		openDatabase();
		
		int count = 0;
		int newposition = groupPosition + 1;
		String sql = "select * from table" + newposition;
		if(db.isOpen()){
			Cursor cursor = db.rawQuery(sql, null);
			count = cursor.getCount();
			cursor.close();
			db.close();
		}
		
		
		
		
		return count;
	}
	
	/**
	 * 获取一级列表的名称集合
	 * @return
	 */
	public static List<String> getGroupNames() {
		
		openDatabase();

		ArrayList<String> groupNames = new ArrayList<String>();
		if(db.isOpen()){
			
			Cursor cursor = db.rawQuery("select name from classlist", null);
			while(cursor.moveToNext()){
				
				String name = cursor.getString(0);
				groupNames.add(name);
			}
			cursor.close();
			db.close();
		}
		
		return groupNames;
	}
	
	/**
	 * 获取当前一级列表下的二级数据list
	 * @param groupPosition 当前一级列表Position
	 * @return
	 */
	public static List<String> getChildrenByPosition(int groupPosition) {

		openDatabase();
		
		String child = null;
		ArrayList<String> children = new ArrayList<String>();
		int newPosition = groupPosition + 1;
		String sql = "select name,number from table"+newPosition;
		if(db.isOpen()){
			
			Cursor cursor = db.rawQuery(sql, null);
			while(cursor.moveToNext()){
				
				String name = cursor.getString(0);
				String number = cursor.getString(1);
				child = name+"\n"+number;
				children.add(child);
			}
			cursor.close();
			db.close();
		}
		return children;
	}

}
