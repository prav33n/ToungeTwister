/**
 * 
 */
package com.tt;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;

/**
 * @author Raven
 *
 */
public class TTlistadapter extends BaseAdapter implements ListAdapter {

	/**
	 * adapter for the TT list in home screen
	 */
	
	private final Activity activity;
    private final Cursor cur;
	public TTlistadapter(Cursor cur, Activity act) {
		this.cur = cur;
        this.activity = act;
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return cur.getCount();
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Cursor getItem(int position) {
		// TODO Auto-generated method stub
		if(cur.getCount()==0) return null;
        else{
        	if(position==0)
        		cur.moveToFirst();
        	else if(!cur.isLast())
        		cur.moveToNext();
        }
       return cur;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		// TODO Auto-generated method stub
		 LayoutInflater inflator = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         view = inflator.inflate(R.layout.trackname, parent,false);
         Button bt =(Button)view.findViewById(R.id.ttname);
         getItem(position);
        int nodeid = cur.getInt(cur.getColumnIndex("_id"));
		bt.setText(cur.getString(cur.getColumnIndex("TrackType")));
		bt.setTag(nodeid);
		return view;
	}
	
	
	

}
