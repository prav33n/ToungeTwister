/**
 * 
 */
package com.tt;

import com.tt.R;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

/**
 * @author Raven
 * 
 */
public class Tracklistadpater extends SimpleCursorAdapter implements
		ListAdapter {

	public Tracklistadpater(Context context, int layout, Cursor c,
			String[] from, int[] to, int flags) {
		super(context, layout, c, from, to, flags);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.widget.CursorAdapter#bindView(android.view.View,
	 * android.content.Context, android.database.Cursor)
	 */
	@Override
	public void bindView(View v, Context context, Cursor cur) {
		// TODO set values to the list view element using cursor data
		TextView tracklist = (TextView) v.findViewById(R.id.trackphrase);
		int nodeid = cur.getInt(cur.getColumnIndex("_id"));
		tracklist.setText(cur.getString(cur.getColumnIndex("Phrase")));
		// Log.e("track data",""+cur.getString(cur.getColumnIndex("Phrase")));
		tracklist.setTag(nodeid);
		ImageView trackstatus = (ImageView) v.findViewById(R.id.trackstatus);
		// Log.e("msecs",""+cur.getInt(cur.getColumnIndex("msecs")));
		if (cur.getInt(cur.getColumnIndex("msecs")) != 0) {
			if (cur.getInt(cur.getColumnIndex("isgreen")) == 1) {
				trackstatus.setImageResource(R.drawable.green_status);
			} else if (cur.getInt(cur.getColumnIndex("isgreen")) == 0) {
				trackstatus.setImageResource(R.drawable.yellow_status);
			} else {
				trackstatus.setImageResource(R.drawable.red_status);
			}
		} else
			trackstatus.setImageResource(R.drawable.btn_check_off_holo_dark);
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
		View v = inflater.inflate(R.layout.tracklistview, parent, false);
		bindView(v, context, cur);
		return v;
	}

}
