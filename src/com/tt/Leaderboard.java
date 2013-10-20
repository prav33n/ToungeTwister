/**
 * 
 */
package com.tt;

import org.json.JSONArray;
import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author Raven
 *
 */
public class Leaderboard extends Baseclass {

	ListView list;
	//Array Adapter that will hold our ArrayList and display the items on the ListView
	Activity act;
	JSONArray jarray;
	String result = new String();
	int nodeid;
	ContentResolver cr;
	Cursor cur;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scores);
		//Initialize ListView
		list= (ListView)findViewById(R.id.scorelist);
		cr = this.getContentResolver();
		LayoutInflater inflater = getLayoutInflater();
        ViewGroup listheader = (ViewGroup)inflater.inflate(R.layout.listheader, list, false);
        list.addHeaderView(listheader, null, false);
		act= this;
		Bundle extras = getIntent().getExtras();
		if(extras.getString("mode").equals("leaderboard")){
			//new httpresult().execute("leaderboard");
			cur = cr.query(CONTENT_URI, null, "Select * from leaderboard order by Score ASC", null, null);
			Leaderboardadapter leaderboard= new Leaderboardadapter(getApplicationContext(),cur,false); 
			list.setAdapter(leaderboard); 
			
		}

		else if(extras.getString("mode").equals("scoreboard")){
			TextView tv = (TextView)listheader.findViewById(R.id.headertext);
			tv.setText("Leaderboard");
			ViewGroup listfooter = (ViewGroup)inflater.inflate(R.layout.listheader, list, false);
			tv = (TextView)listfooter.findViewById(R.id.headertext);
			tv.setText("Score : "+extras.getInt("currenttime"));
		    //list.addFooterView(listfooter,null,true);
			nodeid = extras.getInt("NodeID");
			
			Cursor stats= cr.query(CONTENT_URI, null, "Select UserID,NodeID,msecs,isgreen,Name,_id from stat where NodeID="+nodeid+" and isgreen = 1 group by UserID order by msecs ASC", null, null);
			Log.e("stats count",""+stats.getCount());
			Statsadapter statsboard= new Statsadapter(getApplicationContext(),stats,false); 
			list.setAdapter(statsboard); 
			//new phrasestats().execute("leaderboard");
			/*nodeid = extras.getInt("NodeID");
			Thread updatescore = new Thread(new Runnable() {
				public void run() { 
					JSONObject json = new JSONObject();
					try {
						json.put("nodeid",nodeid);
						result = streamtostring(Baseclass.httpclient("http://107.21.123.15/asrresult/getphrasestats.php",json));
						jarray = new JSONArray(result);			
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			updatescore.start();
			while(result==null)
				continue;
			Log.e("mode","scoreboard"+jarray.length());
			Statsadapter statsAdapter = new Statsadapter (this,jarray);//jArray is your json array 
			//Set the above adapter as the adapter of choice for our list
			lstTest.setAdapter(statsAdapter); */
		}


	}

/*	private class httpresult extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			JSONObject json = new JSONObject();
			try {
				json.put("score","topscore");
				result = streamtostring(Baseclass.httpclient("http://107.21.123.15/asrresult/totalscore.php",json));	
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			try {
				JSONArray jarray = new JSONArray(result);
				Log.e("mode","leaderboard"+jarray.length());
				Scorecalc jSONAdapter = new Scorecalc (act,jarray);//jArray is your json array 
				list.setAdapter(jSONAdapter); //Set the above adapter as the adapter of choice for our list 
			} catch (JSONException e) {
				e.printStackTrace();
			}	
		}
	}
	
	private class phrasestats extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			JSONObject json = new JSONObject();
			String result = new String();
			try {
				json.put("nodeid",nodeid);
				result = streamtostring(Baseclass.httpclient("http://107.21.123.15/asrresult/getphrasestats.php",json));	
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			try {
				JSONArray jarray = new JSONArray(result);
				Log.e("mode","leaderboard"+jarray.length());
				Statsadapter statsAdapter = new Statsadapter (act,jarray);//jArray is your json array 
				//Set the above adapter as the adapter of choice for our list
				list.setAdapter(statsAdapter);  
			} catch (JSONException e) {
				e.printStackTrace();
			}	
		}
	}*/
	
}
