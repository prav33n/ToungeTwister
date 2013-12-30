package com.tt;

import com.tongue.twister.R;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

public class Tracklist extends Baseclass {

	CheckBox completed;
	TextView tv;
	int nodeid, keycode;
	Cursor cur;
	ListView list;
	ContentResolver cr;
	Parcelable state;
	String nodename;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// final boolean customTitleSupported =
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tracklist);
		list = (ListView) findViewById(R.id.tttracklist);
		Bundle extra = getIntent().getExtras();
		nodeid = extra.getInt("nodeid");
		nodename = extra.getString("nodename");
		query = "select  phrase.[_id],phrase.[Phrase],phrase.[Completed],attempt.[msecs],attempt.[isgreen] from phrase left join attempt on phrase.[_id] = attempt.[NodeID]  where phrase.[_id] in (select nodeid from tracknode where trackid = "
				+ nodeid + ")";// order by phrase.[Completed] DESC
		cr = getContentResolver();
		/*
		 * set header and footer for list
		 */
		TextView headertext = (TextView) findViewById(R.id.headertext);
		headertext.setText(nodename);
		cur = cr.query(CONTENT_URI, projection, query, null, null);
		Tracklistadpater tracklist = new Tracklistadpater(
				getApplicationContext(), R.layout.scoreview, cur, new String[] {
						"Phrase", "_id" }, new int[] { R.id.trackphrase,
						R.id.trackstatus }, 0);
		/*
		 * Note : set layout items to clikable = false to use on item listener
		 * or the clickable item takes focus
		 */
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.d("clicked", "clicked on item: " + position);
				TextView tv = (TextView) view.findViewById(R.id.trackphrase);
				Intent i = new Intent(getApplicationContext(),
						Detailedtrack.class);
				i.putExtra("phraseid", Integer.parseInt(tv.getTag().toString()));
				i.putExtra("nodeid", nodeid);
				i.putExtra("position", position);
				i.putExtra("nodename", nodename);
				startActivity(i);
			}
		});
		list.setAdapter(tracklist);
	}

	public void showdetailedtrack(View v) {

		Intent i = new Intent(getApplicationContext(), Detailedtrack.class);
		i.putExtra("phraseid", Integer.parseInt("" + v.getTag().toString()));
		i.putExtra("nodeid", nodeid);
		i.putExtra("nodename", nodename);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
		startActivity(i);
	}

	@Override
	public void onPause() {
		super.onPause();
		state = list.onSaveInstanceState();
	}

	@Override
	public void onResume() {
		super.onResume();
		list.setAdapter(null);
		cur = cr.query(CONTENT_URI, projection, query, null, null);
		// update the list data after the user attempts a phrase.
		Tracklistadpater tracklist = new Tracklistadpater(
				getApplicationContext(), R.layout.tracklistview, cur,
				new String[] { "Phrase", "_id" }, new int[] { R.id.trackphrase,
						R.id.trackstatus }, 0);
		list.setAdapter(tracklist);
		tracklist.notifyDataSetChanged();
		// restore the list to previous state when the user press back button
		if (state != null)
			list.onRestoreInstanceState(state);
	}

}
