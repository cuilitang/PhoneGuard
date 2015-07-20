package cui.litang.phoneguard.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import cui.litang.phoneguard.db.BlackNumberDBOpenHelper;
import cui.litang.phoneguard.entity.RefuseEntity;

/**
 * 黑名单号码Dao
 * @author Cuilitang
 * @Date 2015年7月18日
 */
public class BlackNumberDao {
	
	private static final String TAG = "BlackNumberDao";
	private BlackNumberDBOpenHelper helper;
	
	/**
	 * 构造
	 * @param context 上下文
	 */
	public BlackNumberDao(Context context) {
		
		helper = new BlackNumberDBOpenHelper(context);
	}
	
	/**
	 * 查询黑名单号码是否存在
	 * @param number  号码
	 * @return
	 */
	public Boolean getNumber(String number) {
		
		SQLiteDatabase database = helper.getReadableDatabase();
		Cursor cursor = database.rawQuery("select * from refuselist where number = ?", new String[]{number});
		boolean result = cursor.moveToNext();
		cursor.close();
		database.close();
		return result;
	}
	
	 /**
	  * 查询号码的拦截模式
	  * @param number 号码
	  * @return 拦截模式
	  */
	public String getMode(String number) {
		
		String mode = null;
		SQLiteDatabase database = helper.getReadableDatabase();
		Cursor cursor = database.rawQuery("select mode from refuselist where number = ?", new String[]{number});
		while(cursor.moveToNext()){
			mode = cursor.getString(0);
		}
		cursor.close();
		database.close();
		return mode;
	}
	
	/**
	 * 查询所有黑名单号码
	 * @return 黑名单号码List
	 */
	public List<RefuseEntity> loadAll(){
		
		ArrayList<RefuseEntity> refuseList = new ArrayList<RefuseEntity>();
		SQLiteDatabase database = helper.getReadableDatabase();
		Cursor cursor = database.rawQuery("select number,mode from refuselist order by id ",null);
		
		while(cursor.moveToNext()){
			RefuseEntity refuseEntity = new RefuseEntity();
			refuseEntity.setNumber(cursor.getString(0));
			refuseEntity.setMode(cursor.getString(1));
			refuseList.add(refuseEntity);
		}
		
		cursor.close();
		database.close();
		return refuseList;
	}
	
	/**
	 * 根据传入的条件查询数据
	 * @param limit  加载多少条
	 * @param  offset 起始位置
	 * @return
	 */
	public List<RefuseEntity> loadByCondition(int limit,int offset){
		
		ArrayList<RefuseEntity> refuseList = new ArrayList<RefuseEntity>();
		SQLiteDatabase database = helper.getReadableDatabase();
		Cursor cursor = database.rawQuery("select number,mode from refuselist order by id  limit ? offset ?",new String[]{String.valueOf(limit),String.valueOf(offset)});
		
		while(cursor.moveToNext()){
			RefuseEntity refuseEntity = new RefuseEntity();
			refuseEntity.setNumber(cursor.getString(0));
			refuseEntity.setMode(cursor.getString(1));
			refuseList.add(refuseEntity);
		}
		
		cursor.close();
		database.close();
		return refuseList;
	}
	
	/**
	 * 保存新的黑名单号码
	 * @param refuseEntity  黑名单号码实体
	 */
	public void save(RefuseEntity refuseEntity) {
		
		SQLiteDatabase database = helper.getReadableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put("number", refuseEntity.getNumber());
		contentValues.put("mode", refuseEntity.getMode());
		database.insert("refuselist", null, contentValues);
		Log.i(TAG, refuseEntity.getNumber()+"\n"+refuseEntity.getMode());
		database.close();
	}
	
	/**
	 * 更新黑名单号码的拦截状态
	 * @param refuseEntity 黑名单号码实体
	 */
	public void update(RefuseEntity refuseEntity) {
		
		SQLiteDatabase database = helper.getReadableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put("mode", refuseEntity.getMode());
		database.update("refuselist", contentValues, "number = ?", new String[]{refuseEntity.getNumber()});
		database.close();
	}
	
	
	
	
	/**
	 * 删除黑名单号码
	 * @param number 要删除的号码
	 */
	public void delete(String number) {
		
		SQLiteDatabase database = helper.getReadableDatabase();
		database.delete("refuselist", "number=?", new String[]{number});
		database.close();
	}
	
	
	
	

}
