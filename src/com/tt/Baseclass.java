package com.tt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;


public class Baseclass extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {
	SimpleCursorAdapter mAdapter;
	static Activity act;
	protected static final int LOADER_ID = 1;
	public static int userid;
	public static boolean connectionstatus;
	public static byte[] bytearray;
	public static final Uri CONTENT_URI =  Uri.parse("content://com.TT.provider/");
	public static final Uri CONTENT_URIASR =  Uri.parse("content://com.TT.provider/ASRResult");
	public static final Uri CONTENT_URIAttempt =  Uri.parse("content://com.TT.provider/attempt");
	public static final Uri CONTENT_URIPhrase =  Uri.parse("content://com.TT.provider/phrase");
	public static final Uri CONTENT_URIUser =  Uri.parse("content://com.TT.provider/user");
	public static final Uri CONTENT_URIStats =  Uri.parse("content://com.TT.provider/stat");
	public static final String TAG = "TT Logs";
	
	static boolean transition = false;
	String[] projection;
	String query;
	static String name;

	public Baseclass(){
		act = this;
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
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

	public static InputStream httpclient(String URL,JSONObject json){
		InputStream is= null;
		HttpParams httpParameters = new BasicHttpParams();   //set connection parameters
		HttpConnectionParams.setConnectionTimeout(httpParameters, 10000);
		HttpConnectionParams.setSoTimeout(httpParameters, 10000);
		HttpClient httpclient = new DefaultHttpClient(httpParameters);
		HttpResponse response= null;
		HttpPost httppost = new HttpPost(URL);	
		//httppost.setHeader("json",json.toString());
		//httppost.getParams().setParameter("jsonpost",json);
		try {
			 httppost.setHeader("Content-type", "application/json");
			StringEntity se = new StringEntity(json.toString());
			se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			httppost.setEntity(se);
			response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	return is;
	}
	
	public static String streamtostring(InputStream is){
		//convert response to string
		try{
			int length;
			if(is.available()>0)
				length = is.available();
			else 
				length = 1;
			BufferedReader reader = new BufferedReader(new InputStreamReader(is), length);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) 
			{
				sb.append(line);
			}
			String result=sb.toString();
			is.close();
			reader = null;
			sb = null;
			Log.e("Received data",""+result);
			return result;
		}catch(Exception e){
			Log.e("log_tag", "Error converting result "+e.toString());
			e.printStackTrace();
			return null;
		}
	}
	

}
