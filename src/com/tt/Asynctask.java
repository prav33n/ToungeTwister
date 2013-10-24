/**
 * 
 */
package com.tt;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author Raven
 * @param <Params>
 * 
 */

public class Asynctask extends AsyncTask<List<String>, Integer, String> {
	TextView tv, besttime;
	Button leaderboard;
	Bundle results = new Bundle();
	ContentResolver cr;
	String result, res, attempt;
	Context con;
	Activity act;
	Cursor user;
	int attemptno, phraseid, attemptid, userID, score, greentime, yellowtime;
	long duration;
	JSONObject jobj, json;
	List<String> textMatchList;

	public Asynctask(Activity act, int nodeid, long difference) {
		this.con = act.getApplicationContext();
		this.act = act;
		this.phraseid = nodeid;
		this.duration = difference;
		userID = Baseclass.userid;
		cr = act.getContentResolver();
	}

	@Override
	protected String doInBackground(List<String>... params) {
		// TODO Auto-generated method stub
		Log.e("ASYNCTAG", params[0].toString());
		this.textMatchList = params[0];
		tv = (TextView) act.findViewById(R.id.phrase);
		json = new JSONObject();
		try {
			json.put("phrase", tv.getText());
			json.put("asrresult", textMatchList);
			json.put("attemptid", phraseid);
			json.put("attemptno", attemptno);
			json.put("userid", userID);
			json.put("timediff", duration);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		result = Baseclass.streamtostring(Baseclass.httpclient(
				"http://107.21.123.15/ttservices/asrresult.php", json));
		return result;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		long mintime = 0;
		int isgreen = -1;
		RelativeLayout movieContainer;
		movieContainer = (RelativeLayout) act.findViewById(R.id.resultwindow); // get scroll View
		Animation anim = AnimationUtils.loadAnimation(act, R.anim.fadein); 
		movieContainer.startAnimation(anim);
		
		if (!result.equals("null")) {
			try {
				jobj = new JSONObject(result);
				json = json.put("todb", jobj);
				tv = (TextView) act.findViewById(R.id.resulttext);
				tv.setVisibility(View.VISIBLE);
				besttime = (TextView) act.findViewById(R.id.besttime);
				besttime.setVisibility(View.VISIBLE);
				leaderboard = (Button) act.findViewById(R.id.viewscore);
				act.findViewById(R.id.resultwindow).setVisibility(View.VISIBLE);
				res = jobj.getString("asrphrase");
				attempt = jobj.getString("attemptid");
				score = (int) jobj.getDouble("score");

				if (res.isEmpty())
					tv.setText("Please try again");
				else {
					tv.setText("Your Time: "
							+ String.format("%.2f", (float) duration / 1000)
							+ " Sec ");

					if (score > 30 && score < 70) {
						tv.setText(tv.getText() + "\n Needs Some Work");
						tv.setTextColor(Color.YELLOW);
						isgreen = 0;
					} else if (score >= 70) {
						tv.setText(tv.getText() + "\n GOOD !");
						tv.setTextColor(Color.GREEN);
						isgreen = 1;
					} else if (score <= 30) {
						tv.setText(tv.getText() + "\n Please Try Again");
						tv.setTextColor(Color.RED);
						isgreen = -1;
					}
				}

				// Get mintime for the current phrase
				Cursor cur = cr.query(Baseclass.CONTENT_URI, null,
						"select * from stat where nodeid =" + phraseid
								+ " and UserID =" + Baseclass.userid
								+ " order by msecs ASC", null, null);
				if (cur.getCount() > 0) {
					cur.moveToFirst();
					if (cur.getInt(cur.getColumnIndex("isgreen")) == isgreen) {
						isgreen = cur.getInt(cur.getColumnIndex("isgreen"));
						mintime = cur.getInt(cur.getColumnIndex("msecs")) < duration ? cur
								.getInt(cur.getColumnIndex("msecs")) : duration;
					} else if (cur.getInt(cur.getColumnIndex("isgreen")) > isgreen) {
						isgreen = cur.getInt(cur.getColumnIndex("isgreen"));
						mintime = cur.getInt(cur.getColumnIndex("msecs"));
					} else
						mintime = duration;
					besttime.setText("Your Best time: "
							+ String.format("%.2f", (float) mintime / 1000)
							+ " Sec");
					cur.close();
					Log.e("result present", "" + mintime + "//" + isgreen);
				}

				else {
					mintime = (int) duration;
					if (score > 30 && score < 70)
						isgreen = 0;
					else if (score >= 70)
						isgreen = 1;
					else if (score <= 30)
						isgreen = -1;
					besttime.setText("Your Best Time: "
							+ String.format("%.2f", (float) duration / 1000)
							+ " Sec");
					Log.e("new result", "" + mintime + "//" + isgreen);
				}

				if (isgreen == 1) {
					besttime.setTextColor(Color.GREEN);
					leaderboard.setVisibility(View.VISIBLE);
				} else if (isgreen == -1) {
					besttime.setTextColor(Color.RED);
					leaderboard.setVisibility(View.VISIBLE);
				} else {
					besttime.setTextColor(Color.YELLOW);
					leaderboard.setVisibility(View.VISIBLE);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if (mintime != 0) {
			String query = "select * from attempt where NodeID =" + phraseid;
			Cursor cur = cr.query(Baseclass.CONTENT_URI, null, query, null,
					null);
			ContentValues resultinsert = new ContentValues();
			if (cur.getCount() > 0) {
				String where = "_id = " + phraseid;
				Log.e(Baseclass.TAG, "" + cur.getCount() + "//" + where);
				resultinsert.put("msecs", mintime);
				resultinsert.put("isgreen", isgreen);
				resultinsert.put("AttemptNumber", attemptno);
				resultinsert.put("Feedback", res);
				resultinsert.put("Score", score);
				resultinsert.put("Timestamp", System.currentTimeMillis());
				Log.e("rows updated",
						""
								+ cr.update(Baseclass.CONTENT_URIAttempt,
										resultinsert, where, null));
			}

			else if (cur.getCount() == 0) {

				resultinsert.put("_id", phraseid);
				resultinsert.put("UserId", Baseclass.userid);
				resultinsert.put("NodeId", phraseid);
				resultinsert.put("AttemptNumber", attemptno);
				resultinsert.put("Passed", 1);
				resultinsert.put("Feedback", res);
				if (score > 30 && score <= 70) {
					resultinsert.put("Score", 50);
				} else if (score > 70) {
					resultinsert.put("Score", 100);
				} else if (score <= 30) {
					resultinsert.put("Score", 0);
				}
				resultinsert.put("msecs", mintime);
				resultinsert.put("isgreen", isgreen);
				cr.insert(Baseclass.CONTENT_URIAttempt, resultinsert);
			}

			ContentValues cv = new ContentValues();
			cv.put("Completed", true);
			String where = "_id=" + phraseid;
			cr.update(Baseclass.CONTENT_URIPhrase, cv, where, null);

			cur = cr.query(Baseclass.CONTENT_URI, null,
					"select * from stat where UserID = " + Baseclass.userid
							+ " AND NodeID = " + phraseid, null, null);

			cv = new ContentValues();
			if (cur.getCount() > 0) {
				cv.put("msecs", mintime);
				cv.put("isgreen", isgreen);
				cr.update(Baseclass.CONTENT_URIStats, cv, "UserID ="
						+ Baseclass.userid + " AND NodeID=" + phraseid, null);
			} else {
				cv.put("_id", Math.random());
				cv.put("UserID", Baseclass.userid);
				cv.put("NodeID", phraseid);
				cv.put("msecs", mintime);
				cv.put("isgreen", isgreen);
				cv.put("Name", Baseclass.name);
				cr.insert(Baseclass.CONTENT_URIStats, cv);
			}

			Thread t = new Thread(new Runnable() {
				public void run() {
					Baseclass.streamtostring(Baseclass.httpclient(
							"http://107.21.123.15/ttservices/storeresult.php",
							json));
				}
			});
			t.start();
		} else {
			tv = (TextView) act.findViewById(R.id.resulttext);
			act.findViewById(R.id.resultwindow).setVisibility(View.VISIBLE);
			besttime = (TextView) act.findViewById(R.id.besttime);
			leaderboard = (Button) act.findViewById(R.id.viewscore);
			tv.setVisibility(View.VISIBLE);
			besttime.setVisibility(View.GONE);
			leaderboard.setVisibility(View.GONE);
			tv.setText("processing failed");
		}

	}
}
