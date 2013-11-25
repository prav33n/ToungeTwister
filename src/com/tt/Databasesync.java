/**
 * 
 */
package com.tt;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;

/**
 * @author Praveen Jelish Sync the server data with the local device database
 */
public class Databasesync extends AsyncTask<String, String, String> {
	Activity act;
	JSONArray jarray = new JSONArray();
	JSONObject jobj = new JSONObject();
	int seq = 0;
	String url = "http://107.21.123.15/ttservices/dbsync.php";
	String tablename, result;
	Database db;
	int norecords = 0;
	SharedPreferences settings;
	boolean sync = false;

	public Databasesync(Activity act) {
		this.act = act;
		db = Database.getInstance(act, "TT.sqlite"); /*
													 * intiate the database
													 * connection
													 */
		// get the preference data of the application for last sync time
		settings = act.getSharedPreferences("synctime", Context.MODE_PRIVATE);
		// Log.e("last sync",""+settings.getLong("lastsync", 0));
		if ((System.currentTimeMillis() - settings.getLong("lastsync", 0)) > 3.6e+7) // 3.6e+7
			sync = true;
		// Log.e("update database",""+isOnline());

		// start suýnc in new thread
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					SharedPreferences.Editor editor = settings.edit();
					do {
						jobj = new JSONObject();
						jobj.put("tablename", "stats");
						jobj.put("seq", seq);
						result = Baseclass.streamtostring(Baseclass.httpclient(
								url, jobj));
						// jobj = new JSONObject(result);
						jarray = new JSONArray(result);
						// Log.e("update length",""+jarray.getJSONObject(jarray.length()-1).getInt("totalrecords"));
						norecords = jarray.getJSONObject(jarray.length() - 1)
								.getInt("totalrecords");
						// Log.e("update length",norecords+"//"+jarray.length());
						db.insertstats(jarray);
						seq++;
						editor.putLong("lastsync", System.currentTimeMillis());
					} while (seq < norecords);
					editor.commit();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		if (isOnline() && sync == true) {
			// TODO Auto-generated method stub
			t.start();
		}
	}

	@Override
	protected String doInBackground(String... tablename) {
		return null;
	}

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) act
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		return cm.getActiveNetworkInfo() != null
				&& cm.getActiveNetworkInfo().isConnectedOrConnecting();
	}

}
