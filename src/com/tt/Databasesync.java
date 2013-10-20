/**
 * 
 */
package com.tt;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.util.Log;

/**
 * @author Raven
 *
 */
public class Databasesync extends AsyncTask<String, String, String> {
	Activity act;
	JSONArray jarray  = new JSONArray();
	JSONObject jobj = new JSONObject();
	int seq = 0;
	String url = "http://107.21.123.15/ttservices/dbsync.php";
	String tablename, result;
	Database db;
	int norecords = 0;
	SharedPreferences settings;
	boolean sync = false;
	
	public Databasesync(Activity act)
	{
		this.act = act;
		db = Database.getInstance(act,"TT.sqlite");  /*intiate the database connection */
		settings = act.getSharedPreferences("synctime",  act.MODE_PRIVATE);
		Log.e("last sync",""+settings.getLong("lastsync", 0));
		if((System.currentTimeMillis() - settings.getLong("lastsync", 0)) > 3.6e+7) //3.6e+7
			sync = true;

		Log.e("update database",""+isOnline());
//start suýnc in new thread
		Thread  t = new Thread(new Runnable() {
	           public void run() { 
	        		try {
	        			SharedPreferences.Editor editor =settings.edit();
	        			do{
	        			jobj = new JSONObject();
	        			jobj.put("tablename","stats");
	        			jobj.put("seq",seq);
	        			result = Baseclass.streamtostring(Baseclass.httpclient(url, jobj));
	        			//jobj = new JSONObject(result);
	        			jarray = new JSONArray(result);
	        			Log.e("update length",""+jarray.getJSONObject(jarray.length()-1).getInt("totalrecords"));
	        			norecords = jarray.getJSONObject(jarray.length()-1).getInt("totalrecords");
	        			//Log.e("update length",norecords+"//"+jarray.length());
	        			db.insertstats(jarray); 
	        			seq++;
	        			editor.putLong("lastsync", System.currentTimeMillis());
	        			}while(seq < norecords);
	        			editor.commit();
	        		} catch (Exception e) {
	        			// TODO Auto-generated catch block
	        			e.printStackTrace();
	        		}	        	   
      }});
		
		
		if(isOnline() && sync == true){
		// TODO Auto-generated method stub
			t.start();
		}
	}
	
	@Override
	protected String doInBackground(String... tablename) {/*
		Log.e("update database",""+isOnline());
		if(isOnline() && sync == true){
		// TODO Auto-generated method stub
		try {
			SharedPreferences.Editor editor =settings.edit();
			do{
			jobj = new JSONObject();
			jobj.put("tablename",tablename[0]);
			this.tablename = tablename[0];
			jobj.put("seq",seq);
			result = Baseclass.streamtostring(Baseclass.httpclient(url, jobj));
			//jobj = new JSONObject(result);
			jarray = new JSONArray(result);
			Log.e("update length",""+jarray.getJSONObject(jarray.length()-1).getInt("totalrecords"));
			norecords = jarray.getJSONObject(jarray.length()-1).getInt("totalrecords");
			//Log.e("update length",norecords+"//"+jarray.length());
			db.insertstats(jarray); 
			seq++;
			editor.putLong("lastsync", System.currentTimeMillis());
			}while(seq < norecords);
			editor.commit();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		return result; }
		else 
			return null;
	*/
		return null;}

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) act
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		return cm.getActiveNetworkInfo() != null
				&& cm.getActiveNetworkInfo().isConnectedOrConnecting();
	}
	
}
