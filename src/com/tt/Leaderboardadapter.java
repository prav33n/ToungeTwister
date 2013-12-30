/**
 * 
 */
package com.tt;

import com.tongue.twister.R;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * @author Praveen Jelish
 * 
 */
public class Leaderboardadapter extends CursorAdapter {

	/**
	 * @param context
	 * @param c
	 * @param autoRequery
	 */
	Context con;

	public Leaderboardadapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
		// TODO Auto-generated constructor stub
		this.con = context;
	}

	/**
	 * @param context
	 * @param c
	 * @param flags
	 */
	public Leaderboardadapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.widget.CursorAdapter#bindView(android.view.View,
	 * android.content.Context, android.database.Cursor)
	 */
	@Override
	public void bindView(View v, Context con, Cursor cur) {
		// TODO Auto-generated method stub
		TextView text = (TextView) v.findViewById(R.id.scoredetails);
		TextView previouslevel = (TextView) v.findViewById(R.id.previouslevel);
		TextView nextlevel = (TextView) v.findViewById(R.id.nextlevel);
		ProgressBar levelprogress = (ProgressBar) v
				.findViewById(R.id.levelprogress);
		ImageView img = (ImageView) v.findViewById(R.id.avatar);

		if (cur.getPosition() == 0) {
			img.setImageResource(R.drawable.img1);
		} else if (cur.getPosition() == 1) {
			img.setImageResource(R.drawable.img2);
		} else if (cur.getPosition() == 2) {
			img.setImageResource(R.drawable.img3);
		} else {
			img.setImageResource(R.drawable.img4);
		}

		int score, level, tt, userid;
		String name;
		try {
			userid = cur.getInt(cur.getColumnIndex("UserID"));
			name = cur.getString(cur.getColumnIndex("Name"));
			score = cur.getInt(cur.getColumnIndex("Score"));
			level = score / 1000;
			tt = cur.getInt(cur.getColumnIndex("Level"));
			text.setText(name + "\nDistinct TTs :" + tt + "\nScore :" + score);
			previouslevel.setText("Level " + level);
			nextlevel.setText("Level " + (level + 1));
			levelprogress.setProgress(score % 1000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			userid = 0;
		}

		if (userid == Baseclass.userid)
			v.setBackgroundResource(R.color.mild_blue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.widget.CursorAdapter#newView(android.content.Context,
	 * android.database.Cursor, android.view.ViewGroup)
	 */
	@Override
	public View newView(Context con, Cursor cur, ViewGroup parent) {
		// TODO Auto-generated method stub
		LayoutInflater inflater = LayoutInflater.from(con);
		View v = inflater.inflate(R.layout.scoreview, parent, false);
		bindView(v, con, cur);
		return v;
	}

}
