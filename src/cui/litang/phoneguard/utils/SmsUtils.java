package cui.litang.phoneguard.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.xmlpull.v1.XmlSerializer;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;
import android.widget.ProgressBar;

public class SmsUtils {

	/**
	 * 备份短信的回调接口
	 * @author Cuilitang
	 * @Date 2015年7月20日
	 */
	public interface BackupCallBack{
		
		/**
		 * 开始之前count进度的总量
		 * @param count
		 */
		void beforeBackup(int count);
		
		/**
		 * 当前进度
		 * @param process 进度
		 */
		void onBackup(int process);
		
		
	}
	
	/**
	 * 备份短信将内容写进xml里
	 * @param context
	 * @param callBack
	 * @throws Exception
	 */
	public static void backupSms(Context context, BackupCallBack callBack) throws Exception {
		
		ContentResolver resolver = context.getContentResolver();
		File file = new File(Environment.getExternalStorageDirectory(),"backup.xml");
		FileOutputStream fos = new FileOutputStream(file);
		XmlSerializer xs = Xml.newSerializer();
		xs.setOutput(fos,"utf-8");
		xs.startDocument("utf-8", true);
		xs.startTag(null, "smss");
		Uri uri = Uri.parse("content://sms/");
		Cursor cursor = resolver.query(uri, new String[]{"body","address","type","date"}, null, null, null);
		
		int count = cursor.getCount();
		callBack.beforeBackup(count);
		xs.attribute(null, "count", count+"");
		int process = 0;
		while(cursor.moveToNext()){
			String body = cursor.getString(0);
			String address = cursor.getString(1);
			String type = cursor.getString(2);
			String date = cursor.getString(3);
		
			xs.startTag(null, "sms");
			
			xs.startTag(null, "body");
			xs.text(body);
			xs.endTag(null, "body");
			
			xs.startTag(null, "address");
			xs.text(address);
			xs.endTag(null, "address");
			
			xs.startTag(null, "type");
			xs.text(type);
			xs.endTag(null, "type");
			
			xs.startTag(null, "date");
			xs.text(date);
			xs.endTag(null, "date");
			
			xs.endTag(null, "sms");
			
			process++;
			callBack.onBackup(process);
		}
		
		cursor.close();
		xs.endTag(null, "smss");
		xs.endDocument();
		fos.close();
		
		
	}
	
	
	/**
	 *  还原短信
	 * @param context 上下文
	 * @param flag 是否删除之前
	 */
	public static void restoreSms(Context context, boolean flag) {
		
		Uri uri = Uri.parse("content://sms/");
		if(flag){
			context.getContentResolver().delete(uri, null, null);
		}
		
		//模式数据，真实的数据应该使用xs对象去xml中去取得
		ContentValues values = new ContentValues();
		values.put("body", "i'm a sms");
		values.put("date", "1395045035573");
		values.put("type", "1");
		values.put("address", "5558");
		
		context.getContentResolver().insert(uri, values);
		
		
		
	}
}
