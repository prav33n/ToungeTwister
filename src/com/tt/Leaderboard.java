/**
 * 
 */
package com.tt;

import org.json.JSONArray;

import com.tongue.twister.R;
import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author Praveen Jelish View code to display the leaderboard data for the
 *         individual tracks
 */
public class Leaderboard extends Baseclass {

	ListView list;
	// Array Adapter that will hold our ArrayList and display the items on the
	// ListView
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
		// GET THE ListView
		list = (ListView) findViewById(R.id.scorelist);
		cr = this.getContentResolver();
		LinearLayout rel = (LinearLayout) this.findViewById(R.id.ads_layout);
		new Ad(rel, this);
		act = this;
		Bundle extras = getIntent().getExtras();
		if (extras.getString("mode").equals("leaderboard")) {
			cur = cr.query(CONTENT_URI, null,
					"Select * from leaderboard order by Score ASC", null, null);
			// create a leaderboard adapter and set it to list view
			Leaderboardadapter leaderboard = new Leaderboardadapter(
					getApplicationContext(), cur, false);
			list.setAdapter(leaderboard);
		}

		else if (extras.getString("mode").equals("scoreboard")) {
			TextView tv = (TextView) findViewById(R.id.headertext);
			tv.setText(extras.getString("phrase"));
			nodeid = extras.getInt("NodeID");
			ImageButton share = (ImageButton) findViewById(R.id.sharescore);
			share.setTag(extras.getString("phrase"));
			Cursor stats = cr
					.query(CONTENT_URI,
							null,
							"Select UserID,NodeID,msecs,isgreen,Name,_id from stat where NodeID="
									+ nodeid
									+ " and isgreen = 1 group by UserID order by msecs ASC",
							null, null);
			Log.e("stats count", "" + stats.getCount());
			// create a Stats adapter adapter and set it to list view
			Statsadapter statsboard = new Statsadapter(getApplicationContext(),
					R.layout.tracklistview, stats, new String[] { "UserID",
							"_id", "msecs" }, new int[] { R.id.avatar,
							R.id.scoredetails, R.id.scoredetails }, 0);
			list.setAdapter(statsboard);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}

	@Override
	public void onPause() {
		super.onPause();
		Baseclass.transition = true;
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}
}
