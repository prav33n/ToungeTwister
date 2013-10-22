package com.tt;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;



public class Home extends Baseclass   { 
	static ContentResolver cr;
	static AlertDialog.Builder alertbox;
	static AlertDialog dlg;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		/* run the following command to back up data rom device 
		 * adb backup -f ~/data.ab -noapk com.tt
		 * dd if=data.ab bs=1 skip=24 | openssl zlib -d | tar -xvf - 
		 * to extract data.ab file in linux evironment
		 * */
		final boolean customTitleSupported = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.home);
		   if (customTitleSupported) {
	            getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
	                    R.layout.ttheader);
	        }
		ListView list = (ListView)findViewById(R.id.TTlist);
		cr = this.getContentResolver();
		 Databasesync sync = new Databasesync(this);
		 sync.execute(new String[]{"stats"});
		query = "select _id, tracktype, tracklevel from track";
		final ContentResolver cr = getContentResolver();
		Cursor cur = cr.query(Baseclass.CONTENT_URI, null, "select * from user where _id=1", null, null);
		LayoutInflater factory = LayoutInflater.from(this);
		final View textEntryView = factory.inflate(R.layout.introdialog,null);
		
		if(cur.getCount() == 0){
			alertbox = new AlertDialog.Builder(this);
			alertbox.setTitle("Please enter your Name");
			alertbox.setView(textEntryView);
			alertbox.setPositiveButton("Save", new DialogInterface.OnClickListener() {
				// do something when the button is clicked
				public void onClick(DialogInterface arg0, int arg1) {
					ContentValues cv = new ContentValues();
					EditText et = (EditText)textEntryView.findViewById(R.id.username);
					String username = et.getText().toString();
					Integer hashcode = username.hashCode();
					Log.e("hashcode","//"+username+"//"+hashcode);
						new Usercheck().execute(username,hashcode.toString());
				}
			});
			alertbox.setCancelable(false);
			dlg = alertbox.create();
			dlg.setCancelable(false);
			dlg.show();
		}
		else{
			cur.moveToFirst();
			Baseclass.userid = cur.getInt(cur.getColumnIndex("UserID"));
			Baseclass.name = cur.getString(cur.getColumnIndex("Name"));
			Log.e("TT TAG",""+Baseclass.userid);
			cur.close();
		}
		
		Cursor nodes = cr.query(CONTENT_URI, projection, query, null, null);
		TTlistadapter listadapter = new TTlistadapter (getApplicationContext(),R.layout.trackname, nodes,
                new String[] {"TrackType","_id" },
                new int[] { R.id.ttname,R.id.ttname }, 0);//jArray is your json array 
		list.setAdapter(listadapter); 
		list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("clicked", "clicked on item: " + position);
                Button bt = (Button) view.findViewById(R.id.ttname);
        		Intent i = new Intent(getApplicationContext(),Tracklist.class);
        		i.putExtra("nodeid", bt.getTag().toString());
        		i.putExtra("nodename", bt.getText().toString());
        		startActivity(i);
            }
        });
		
	}
	
	public void showtracklist(View v){
		// TODO Auto-generated method stub
		Button bt = (Button) v.findViewById(R.id.ttname);
		Intent i = new Intent(getApplicationContext(),Tracklist.class);
		i.putExtra("nodeid", bt.getTag().toString());
		i.putExtra("nodename", bt.getText().toString());
		startActivity(i);
	}

	public void showscore(View v){
		Intent i = new Intent(getApplicationContext(),Leaderboard.class);
		i.putExtra("mode", "leaderboard");
		startActivity(i);
	}
}

class Usercheck extends AsyncTask<String,Integer,String>
{
	String username, status;
	Integer hashcode;
	
	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		username = params[0];
		hashcode = Integer.parseInt(params[1]);
		JSONObject json = new JSONObject();
		try {
			json.put("UserID",hashcode);
			json.put("Name", username);
			json.put("Level",0);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		status = Baseclass.streamtostring(Baseclass.httpclient("http://107.21.123.15/ttservices/updateuser.php",json));
		if(status.equals("false") || username.isEmpty()){
			publishProgress(1);}
		else{
			ContentValues cv = new ContentValues();
			cv.put("_id", 1);
			cv.put("UserID",hashcode);
			cv.put("Name", username);
			cv.put("Created", System.currentTimeMillis());
			Home.cr.insert(Baseclass.CONTENT_URIUser, cv);
			Baseclass.userid =hashcode;
			Baseclass.name= username;
		}
		return null;
	}
	
	 protected void onProgressUpdate(Integer... progress) {
	        //This method runs on the UI thread, it receives progress updates
	        //from the background thread and publishes them to the status bar
		 //Home.alertbox.setTitle("User Name exist !");
		 if(username.isEmpty())
			 Home.dlg.setTitle("User Name Cannot be empty ! \nPlease enter you NicKName");
		 else 
		 Home.dlg.setTitle("User Name exist ! \nPlease enter you NicKName");
		 Home.dlg.show();
	    }
}


