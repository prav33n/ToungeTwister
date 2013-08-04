package com.tt;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

public class Tracklist extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor>  {

	Baseclass bs;
	CheckBox completed;
	TextView tv;
	int nodeid; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		 bs = new Baseclass();
		setContentView(R.layout.tracklist);
		ListView list = (ListView)findViewById(R.id.tttracklist);
		Bundle extra = getIntent().getExtras();
		nodeid = Integer.parseInt(extra.getString("nodeid"));
		bs.query = "select  _id,phrase,length,updated from phrase  where _id in (select nodeid from tracknode where trackid = "+nodeid+");";
		bs.projection = new String[] {"_id", "phrase","updated"};  
	    ContentResolver cr = getContentResolver();
		getSupportLoaderManager().initLoader(bs.LOADER_ID, null,this);
		  bs.mAdapter = new SimpleCursorAdapter(this,
	              R.layout.tracklist, null,
	              new String[] {"Phrase","Updated" },
	              new int[] { R.id.trackphrase,R.id.completed }, 0);
		  list.setAdapter(bs.mAdapter);
		  //ContentResolver cr = getContentResolver();
		  findViewById(R.id.trackphrase).setVisibility(View.GONE);
		  findViewById(R.id.completed).setVisibility(View.GONE);
		  list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> array, View view, int position,
					long arg3) {
				// TODO Auto-generated method stub
			}
		});

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
	    		   if(columnIndex==cur.getColumnIndex("Phrase")){
	    		        TextView tv  = (TextView)view.findViewById(R.id.trackphrase);
		    	        int nodeid = cur.getInt(cur.getColumnIndex("_id"));
		    	        /*if(count.get(personid.indexOf(person)) <=1)
		       	       	tv.setText(""+count.get(personid.indexOf(person))+" Quote");
		    	        else*/ 
		    	        tv.setText(cur.getString(cur.getColumnIndex("Phrase")));
		    	        tv.setTag(nodeid); }
	    		   else if(columnIndex==cur.getColumnIndex("Updated")){
		    	        	CheckBox ch = (CheckBox)view.findViewById(R.id.completed);
		    	        	ch.setChecked(true);
		    	        	ch.setClickable(false);}
	    	   	return true;
	    	    }
		 });

	}
	
	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		bs.mAdapter.swapCursor(null);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		Log.e("loader","created");
		  CursorLoader cursorLoader = new CursorLoader(this,Baseclass.CONTENT_URI, bs.projection, bs.query, null, null);
				  return cursorLoader;
	
	}
	
	
	public void showdetailedtrack(View v){
		
		Intent i = new Intent(getApplicationContext(),Detailedtrack.class);
		i.putExtra("phraseid", Integer.parseInt(""+v.getTag().toString()));
		i.putExtra("nodeid", nodeid);
		startActivity(i);
	}

}
