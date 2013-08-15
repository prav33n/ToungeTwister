package com.tt;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;



public class Home extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor>   { 
	Baseclass bs;
	JSONObject  json;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		 bs = new Baseclass();
		//final boolean customTitleSupported = requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.home);
	     /*   if (customTitleSupported) {
	            getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
	                    R.layout.titlebar);
	        }*/
		ListView list = (ListView)findViewById(R.id.TTlist);
		bs.query = "select _id, tracktype, tracklevel from track";
		bs.projection = new String[] {"_id", "tracktype","tracklevel"}; 
		
	    final ContentResolver cr = getContentResolver();
	    Cursor cur = cr.query(Baseclass.CONTENT_URI, null, "select * from user where _id=1", null, null);
		   LayoutInflater factory = LayoutInflater.from(this);
           final View textEntryView = factory.inflate(R.layout.introdialog,null);
	    if(cur.getCount() == 0){
	        AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
	        alertbox.setTitle("Please enter your Name");
            alertbox.setView(textEntryView);
            alertbox.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            	// do something when the button is clicked
                public void onClick(DialogInterface arg0, int arg1) {
                			ContentValues cv = new ContentValues();
                			EditText et = (EditText)textEntryView.findViewById(R.id.username);
                			String username = et.getText().toString();
                			int hashcode = username.hashCode();
                			Log.e("hashcode","//"+username);
                			cv.put("_id", 1);
                			cv.put("UserID",hashcode);
                			cv.put("Name", username);
                			cv.put("Level",0);
                			cv.put("Updated", System.currentTimeMillis());
                			cv.put("Synced",true);
                			cr.insert(Baseclass.CONTENT_URIUser, cv);
                			Baseclass.userid =hashcode;
                			try{
                				json = new JSONObject();
                				json.put("UserID",hashcode);
                				json.put("Name", username);
                				json.put("Level",0);
                				Thread t = new Thread(new Runnable() {
                					public void run() { 
                						Baseclass.httpclient("http://107.21.123.15/asrresult/updateuser.php",json);
                					}
                				});
                				t.start();	
                			}
                			catch(JSONException e){
                				
                			}
                }
            });
        	alertbox.setCancelable(false);
			alertbox.show();
	        }
	    else{
	    	cur.moveToFirst();
	    	Baseclass.userid = cur.getInt(cur.getColumnIndex("UserID"));
	    	Log.e("TT TAG",""+cur.getInt(cur.getColumnIndex("UserID")));
	    }
		getSupportLoaderManager().initLoader(Baseclass.LOADER_ID, null,this);
		  bs.mAdapter = new SimpleCursorAdapter(this,
	              R.layout.home, null,
	              new String[] {"TrackType","TrackLevel" },
	              new int[] { R.id.TTtrackname,R.id.dummy_button }, 0);
		  list.setAdapter(bs.mAdapter);
		  findViewById(R.id.TTtrackname).setVisibility(View.GONE);
		  list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> array, View view, int position,
					long arg3) {
				// TODO Auto-generated method stub
				Button bt = (Button) view.findViewById(R.id.TTtrackname);
				Intent i = new Intent(getApplicationContext(),Tracklist.class);
				i.putExtra("nodeid", bt.getTag().toString());
				startActivity(i);
			}
		});

		}
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		Log.e("loader","created");
		  CursorLoader cursorLoader = new CursorLoader(this,Baseclass.CONTENT_URI, bs.projection, bs.query, null, null);
				  return cursorLoader;
		//return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		// TODO Auto-generated method stub
		switch (loader.getId()) {
	      case Baseclass.LOADER_ID:
	    	  Log.e("loader","swaped"+cursor.getCount());
	        // The asynchronous load is complete and the data
	        // is now available for use. Only now can we associate
	        // the queried Cursor with the SimpleCursorAdapter.
	        bs.mAdapter.swapCursor(cursor);
	        break;
	    }
		

		 bs.mAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
			 @Override  
			 public boolean setViewValue(View view, Cursor cur, int columnIndex) {
	    		   Button rl;
	    		   
	    		   if(columnIndex==cur.getColumnIndex("TrackType")){
	    		        Button bt  = (Button)view.findViewById(R.id.TTtrackname);
		    	        int nodeid = cur.getInt(cur.getColumnIndex("_id"));
		    	        /*if(count.get(personid.indexOf(person)) <=1)
		       	       	tv.setText(""+count.get(personid.indexOf(person))+" Quote");
		    	        else*/ 
		    	        bt.setText(cur.getString(cur.getColumnIndex("TrackType")));
		    	        bt.setTag(nodeid); }
	    		   else if(columnIndex==cur.getColumnIndex("TrackLevel")){
		    	        	rl = (Button)view.findViewById(R.id.dummy_button);
		    	        	rl.setVisibility(View.GONE);
		    	        	}
	    	   	return true;
	    	    }
		 });

	}
	
	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		bs.mAdapter.swapCursor(null);
	}

	public void showtracklist(View v){
		// TODO Auto-generated method stub
		Button bt = (Button) v.findViewById(R.id.TTtrackname);
		Intent i = new Intent(getApplicationContext(),Tracklist.class);
		i.putExtra("nodeid", bt.getTag().toString());
		startActivity(i);
	}
	
	public void showscore(View v){
		Intent i = new Intent(getApplicationContext(),Score.class);
		i.putExtra("mode", "leaderboard");
		startActivity(i);
	}
	
}


/*public class Home extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

}*/
