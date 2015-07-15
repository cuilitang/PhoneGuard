package cui.litang.phoneguard.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;

public class TelAreaQueryUtils {
	
	private static String path = "data/data/cui.litang.phoneguard/files/address.db";
	/**
	 * 传一个号码进去返回一个归属地
	 * @param 电话号码字符串
	 * @return 归属地字符串
	 */
	public static String queryNumber(String tel) {
		String area = tel;   //如果匹配不到就返回电话号码
		SQLiteDatabase database = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		if(tel.matches("^1[34568]\\d{9}$")){
			Cursor cursor = database.rawQuery("select location from data2 where id = (select outkey from data1 where id = ?)", new String[]{tel.substring(0, 7)});
			while(cursor.moveToNext()){
				area = cursor.getString(0);
				area = area.substring(0, area.length()-2); //去掉运营商字样 比如山东济南联通 保留山东济南
			}
			cursor.close();
			
		}else {
			switch (tel.length()) {
			case 1:
				area = "无结果";
				break;
			case 2:
				area = "无结果";
				break;
			case 3:
				area = "公共服务";
				break;
			case 4:
				area = "模拟器";
				break;
			case 5:
				area = "客服电话";
				break;
			case 7:
				area = "本地电话";
				break;
			case 8:
				area = "本地号码";
				break;
			default:
				if(tel.length()>10&&tel.startsWith("0")){
					//010-XXXXXXXX 截取 10，若是查出结果就赋值给area
					Cursor cursor = database.rawQuery("select location from data2 where area = ?", new String[]{tel.substring(1, 3)});
					while(cursor.moveToNext()){
						area = cursor.getString(0);
						
					}
					cursor.close();
					
					//0531-XXXXXXXX 截取 531，若是查出结果就赋值给area
					cursor = database.rawQuery("select location from data2 where area = ?", new String[]{tel.substring(1, 4)} );
					while(cursor.moveToNext()){
						area = cursor.getString(0);
					}
					cursor.close();
					area = area.substring(0, area.length()-2); //去掉运营商字样 比如山东济南联通 保留山东济南
				}
				break;
			}
		}
		
		
		return area;
	}
	
}
