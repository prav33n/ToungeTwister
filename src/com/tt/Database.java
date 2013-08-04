/**
 * 
 */
package com.tt;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author Raven
 *
 */
public class Database extends SQLiteOpenHelper {
SQLiteDatabase tt;
	public Database(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
		try {
			tt = SQLiteDatabase.openDatabase(context.getDatabasePath(name).toString(),factory,SQLiteDatabase.OPEN_READWRITE);
			Log.e("database constructor","Open"+tt.isOpen());
		    }
			catch(Exception e) {Log.e("constructor",""+e.getMessage());}
		
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}
	
	public Cursor queryall(String DB_TABLE) {
		try {
			Cursor Cur = tt.rawQuery("Select * from "+DB_TABLE, null);
			return Cur;}
			catch(Exception e) {e.printStackTrace(); return null;}
		
		}
	public int update (String DB_TABLE,ContentValues values,String where){
		tt.beginTransaction();
		int update = tt.update(DB_TABLE, values, where,null);
		tt.setTransactionSuccessful();
		tt.endTransaction();
		Log.e("update", ""+update);
		//checkDB.close();
		return update;
	}
	public long insert(String DB_TABLE, ContentValues values) {
//	   	ContentValues initialValues = new ContentValues();
		tt.beginTransaction();
		long insert = tt.insertOrThrow(DB_TABLE, null, values);
		tt.setTransactionSuccessful();
		tt.endTransaction();
		Log.e("insert", ""+insert);
		//checkDB.close();
		return insert;
	 }

	

	public Cursor query(String query) {
		try {
		Cursor Cur = tt.rawQuery(query, null);
		Log.e("sql query",""+Cur.getColumnCount());
		return Cur;}
		catch(Exception e) {Log.e("sql query","db error"); e.printStackTrace(); return null;}
		
	}
	
	

}
