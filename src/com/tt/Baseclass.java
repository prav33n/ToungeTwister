package com.tt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
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
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;

@SuppressLint("Registered")
public class Baseclass extends Activity implements
		LoaderManager.LoaderCallbacks<Cursor> {
	SimpleCursorAdapter mAdapter;
	static Activity act;
	protected static final int LOADER_ID = 1;
	public static int userid;
	public static boolean connectionstatus;
	public static byte[] bytearray;
	public static final Uri CONTENT_URI = Uri
			.parse("content://com.TT.provider/");
	public static final Uri CONTENT_URIASR = Uri
			.parse("content://com.TT.provider/ASRResult");
	public static final Uri CONTENT_URIAttempt = Uri
			.parse("content://com.TT.provider/attempt");
	public static final Uri CONTENT_URIPhrase = Uri
			.parse("content://com.TT.provider/phrase");
	public static final Uri CONTENT_URIUser = Uri
			.parse("content://com.TT.provider/user");
	public static final Uri CONTENT_URIStats = Uri
			.parse("content://com.TT.provider/stat");
	public static final String TAG = "TT Logs";
	static int nodeid, phraseid, position;
	static String currrentpage, nodename;
	static boolean transition = false;
	String[] projection;
	String query;
	static ArrayList<String> content = new ArrayList<String>();
	static String name;

	public Baseclass() {
		act = this;

	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		CursorLoader cursorLoader = new CursorLoader(this, CONTENT_URI,
				projection, query, null, null);
		return cursorLoader;

		// return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		// TODO Auto-generated method stub
		switch (loader.getId()) {
		case LOADER_ID:
			Log.e("loader", "swaped" + cursor.getCount());
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

	public static InputStream httpclient(String URL, JSONObject json) {
		InputStream is = null;
		HttpParams httpParameters = new BasicHttpParams(); // set connection
															// parameters
		HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
		HttpConnectionParams.setSoTimeout(httpParameters, 5000);
		HttpClient httpclient = new DefaultHttpClient(httpParameters);
		HttpResponse response = null;
		HttpPost httppost = new HttpPost(URL);
		// httppost.setHeader("json",json.toString());
		// httppost.getParams().setParameter("jsonpost",json);
		try {
			httppost.setHeader("Content-type", "application/json");
			StringEntity se = new StringEntity(json.toString());
			se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
					"application/json"));
			httppost.setEntity(se);
			httpclient.getConnectionManager().closeExpiredConnections();
			response = httpclient.execute(httppost);
			StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				is = entity.getContent();
			} else {
				// Closes the connection.
				response.getEntity().getContent().close();
				throw new IOException(statusLine.getReasonPhrase());
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return is;
	}

	public static String streamtostring(InputStream is) {
		// convert response to string
		try {
			int length;
			if (is.available() > 0)
				length = is.available();
			else
				length = 1;
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is), length);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			String result = sb.toString();
			is.close();
			reader = null;
			sb = null;
			// Log.e("Received data",""+result);
			return result;
		} catch (Exception e) {
			Log.e("log_tag", "Error converting result " + e.toString());
			e.printStackTrace();
			return null;
		}
	}

	public void sharedata(View v) {
		Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
		// set the type
		shareIntent.setType("text/plain");
		// add a subject
		String shareMessage = null;
		if (v.getTag() != null)
			shareMessage = "\"" + v.getTag() + "\"\n\n";
		shareMessage += "Check out Tongue Twisters Tournament: http://bitly.com/tonguetwisterstournament ";
		shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
				"Check out Tongue Twisters Tournament");
		// build the body of the message to be shared
		// add the message
		shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMessage);
		// Log.e("button selected",shareMessage);
		// start the chooser for sharing
		startActivity(Intent.createChooser(shareIntent, "Application Share"));

	}

	public void goback(View v) {
		finish();
	}
}
