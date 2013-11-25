/**
 * 
 */
package com.tt;

import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

/**
 * @author Praveen Jelish
 * 
 */
public class Database extends SQLiteOpenHelper {

	private static Database db;
	SQLiteDatabase tt;
	static Context con;
	static CursorFactory factory;
	static String name;

	private Database(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
		try {
			tt = SQLiteDatabase.openDatabase(context.getDatabasePath(name)
					.toString(), factory, SQLiteDatabase.OPEN_READWRITE);
			// Log.e("database constructor","Open"+tt.isOpen());
		} catch (Exception e) {
			Log.e("constructor", "" + e.getMessage());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite
	 * .SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite
	 * .SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	// Create a singleton instance of the database so it is opened only once
	public static synchronized Database getInstance(Context con, String name) {
		if (db == null)
			db = new Database(con, name, factory, 1);
		return db;
	}

	public Cursor queryall(String DB_TABLE) {
		try {
			Cursor Cur = tt.rawQuery("Select * from " + DB_TABLE, null);
			return Cur;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public int update(String DB_TABLE, ContentValues values, String where) {
		tt.beginTransaction();
		int update = tt.update(DB_TABLE, values, where, null);
		tt.setTransactionSuccessful();
		tt.endTransaction();
		// Log.e("update", ""+update);
		// checkDB.close();
		return update;
	}

	public long insert(String DB_TABLE, ContentValues values) {
		// ContentValues initialValues = new ContentValues();
		tt.beginTransaction();
		long insert = tt.insertOrThrow(DB_TABLE, null, values);
		tt.setTransactionSuccessful();
		tt.endTransaction();
		// Log.e("insert", ""+insert);
		// checkDB.close();
		return insert;
	}

	public Cursor query(String query) {
		try {
			Cursor Cur = tt.rawQuery(query, null);
			// Log.e("sql query",""+Cur.getColumnCount());
			return Cur;
		} catch (Exception e) {
			Log.e("sql query", "db error");
			e.printStackTrace();
			return null;
		}

	}

	public boolean insertstats(JSONArray jArray) {

		tt.beginTransaction();
		try {
			SQLiteStatement insert = null;
			insert = tt
					.compileStatement("INSERT OR REPLACE INTO \"stat\" (\"_id\",\"UserID\",\"NodeID\",\"msecs\",\"isgreen\",\"updated\",\"Name\") VALUES (?,?,?,?,?,?,?)");
			// Parse the staring to json foramt
			// JSONArray jArray = new JSONArray(result);
			JSONObject json_data = null;
			// Log.e("insert stats",""+(jArray.length()-1));
			Calendar cal = Calendar.getInstance();
			for (int i = 0; i < jArray.length() - 1; i++) {
				json_data = jArray.getJSONObject(i);
				insert.bindLong(1, Integer.parseInt(json_data.getString("_id")));
				insert.bindLong(2,
						Integer.parseInt(json_data.getString("userid")));
				insert.bindLong(3,
						Integer.parseInt(json_data.getString("tracknodeid")));
				insert.bindLong(4,
						Integer.parseInt(json_data.getString("msec")));
				insert.bindLong(5,
						Integer.parseInt(json_data.getString("isgreen")));
				insert.bindLong(6, cal.getTimeInMillis());
				insert.bindString(7, json_data.getString("name"));
				insert.execute();
				insert.clearBindings();
			}
			tt.setTransactionSuccessful();
			return true;
		} catch (Exception e) {
			String errMsg = (e.getMessage() == null) ? "bulkInsert failed" : e
					.getMessage();
			Log.e("bulkInsert stats:", errMsg);
			return false;
		} finally {
			tt.endTransaction();
		}
	}

	public boolean insertuser(JSONArray jArray) {

		tt.beginTransaction();
		try {
			SQLiteStatement insert = null;
			insert = tt
					.compileStatement("INSERT OR REPLACE INTO \"leaderboard\" (\"_id\",\"UserID\",\"Level\",\"Name\",\"Score\") VALUES (?,?,?,?,?)");
			// Parse the staring to json foramt
			// JSONArray jArray = new JSONArray(result);
			JSONObject json_data = null;
			// Log.e("insert leaderboard",""+jArray.length());
			for (int i = 0; i < jArray.length(); i++) {
				json_data = jArray.getJSONObject(i);
				insert.bindLong(1, json_data.getString("ID") == null ? 0
						: Integer.parseInt(json_data.getString("ID")));
				insert.bindLong(2,
						Integer.parseInt(json_data.getString("UserID")));
				insert.bindLong(3,
						Integer.parseInt(json_data.getString("Level")));
				insert.bindString(4, (json_data.getString("Name")));
				insert.bindLong(5,
						Integer.parseInt(json_data.getString("Score")));
				insert.execute();
				insert.clearBindings();
			}
			tt.setTransactionSuccessful();
			return true;
		} catch (Exception e) {
			String errMsg = (e.getMessage() == null) ? "bulkInsert failed" : e
					.getMessage();
			Log.e("bulkInsert user:", errMsg);
			return false;
		} finally {
			tt.endTransaction();
		}
	}

}
