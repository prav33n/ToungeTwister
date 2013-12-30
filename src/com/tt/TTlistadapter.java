/**
 * 
 */
package com.tt;

import com.tongue.twister.R;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;

/**
 * @author Raven
 * 
 */
public class TTlistadapter extends SimpleCursorAdapter implements ListAdapter {
	Context con;

	public TTlistadapter(Context context, int layout, Cursor c, String[] from,
			int[] to, int flags) {
		super(context, layout, c, from, to, flags);
		this.con = context;
		// TODO Auto-generated constructor stub
	}

	/**
	 * adapter for the TT list in home screen
	 */

	@Override
	public void bindView(View v, Context context, Cursor cur) {
		// TODO set values to the list view element using cursor data
		Button bt = (Button) v.findViewById(R.id.ttname);
		int nodeid = cur.getInt(cur.getColumnIndex("_id"));
		bt.setText(cur.getString(cur.getColumnIndex("TrackType")));
		bt.setTag(nodeid);

		bt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Button bt = (Button) v.findViewById(R.id.ttname);
				Intent i = new Intent(con, Tracklist.class);
				Baseclass.nodeid = Integer.parseInt(bt.getTag().toString());
				Baseclass.nodename = bt.getText().toString();
				i.putExtra("nodeid", Integer.parseInt(bt.getTag().toString()));
				i.putExtra("nodename", bt.getText().toString());
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				con.startActivity(i);
			}
		});

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.widget.CursorAdapter#newView(android.content.Context,
	 * android.database.Cursor, android.view.ViewGroup)
	 */
	@Override
	public View newView(Context context, Cursor cur, ViewGroup parent) {
		// TODO return the view to inflate
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.trackname, parent, false);
		bindView(v, context, cur);
		return v;
	}

}
