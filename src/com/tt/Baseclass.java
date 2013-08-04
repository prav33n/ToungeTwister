package com.tt;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;


public class Baseclass extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {
	SimpleCursorAdapter mAdapter;
	 protected static final int LOADER_ID = 1;
	 String[] projection;
	 String query;
	public static final Uri CONTENT_URI =  Uri.parse("content://com.TT.provider/");
	 public static final Uri CONTENT_URIASR =  Uri.parse("content://com.TT.provider/ASRResult");
	 public static final Uri CONTENT_URIAttempt =  Uri.parse("content://com.TT.provider/attempt");
	 public static final Uri CONTENT_URIPhrase =  Uri.parse("content://com.TT.provider/phrase");
	 public static final Uri CONTENT_URIUser =  Uri.parse("content://com.TT.provider/user");
	
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		Log.e("loader","created");
		  CursorLoader cursorLoader = new CursorLoader(this,CONTENT_URI, projection, query, null, null);
				  return cursorLoader;
		//return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		// TODO Auto-generated method stub
		switch (loader.getId()) {
	      case LOADER_ID:
	    	  Log.e("loader","swaped"+cursor.getCount());
	        // The asynchronous load is complete and the data
	        // is now available for use. Only now can we associate
	        // the queried Cursor with the SimpleCursorAdapter.
	        mAdapter.swapCursor(cursor);
	        break;
	    }

		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		mAdapter.swapCursor(null);
	}

		

}
