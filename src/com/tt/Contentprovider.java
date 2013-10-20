/**
 * 
 */
package com.tt;

import java.util.List;

import org.json.JSONArray;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.net.Uri;
import android.util.Log;

/**
 * @author Raven
 *
 */
public class Contentprovider extends ContentProvider {

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#delete(android.net.Uri, java.lang.String, java.lang.String[])
	 */
	Database db;
    Cursor Cur;
	SQLiteDatabase checkDB;
	CursorFactory factory;
	
	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#getType(android.net.Uri)
	 */
	@Override
	public String getType(Uri arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#insert(android.net.Uri, android.content.ContentValues)
	 */
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
	List<String> database = null;
	database = uri.getPathSegments();
	Log.e("table",""+database.toString());
	db.insert(database.get(0), values);
		return uri;
	}
	/* (non-Javadoc)
	 * @see android.content.ContentProvider#onCreate()
	 */
	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		Copyfiles fp = new Copyfiles(); /* intiate the file copy class and call the copydatabse function*/
		fp.Copydatabase(getContext());
		db = Database.getInstance(getContext(),"TT.sqlite");  /*intiate the database connection */
		return true;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String)
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		
		return db.query(selection);
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[])
	 */
	@Override
	public int update(Uri uri, ContentValues values, String where, String[] whereargs) {
		// TODO Auto-generated method stub
		List<String> database = null;
		database = uri.getPathSegments();
		Log.e("table",""+database.toString());
		return db.update(database.get(0), values, where);
	}
	
}
