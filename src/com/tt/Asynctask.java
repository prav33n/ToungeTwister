/**
 * 
 */
package com.tt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
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

		result = streamtostring(httpclient(
				"http://107.21.123.15/ttservices/asrresult.php", json));
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		Log.e("server result", "" + result);
		long mintime = 0;
		int isgreen = -1, besttimecolor = -1;
		RelativeLayout container;
		container = (RelativeLayout) act.findViewById(R.id.resultwindow); // get
																			// scroll
																			// View
		Animation anim = AnimationUtils.loadAnimation(act, R.anim.fadein);
		container.startAnimation(anim);

		if (!result.equals("null") || !result.isEmpty()) {
			try {
				jobj = new JSONObject(result);
				json = json.put("todb", jobj);
				tv = (TextView) act.findViewById(R.id.resulttext);
				besttime = (TextView) act.findViewById(R.id.besttime);
				leaderboard = (Button) act.findViewById(R.id.viewscore);
				tv.setVisibility(View.VISIBLE);
				besttime.setVisibility(View.VISIBLE);
				leaderboard.setVisibility(View.VISIBLE);
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
					// set color for the current time based on result received
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
					// set color for the best time based on result in the
					// database
					if (cur.getInt(cur.getColumnIndex("isgreen")) == isgreen) {
						isgreen = cur.getInt(cur.getColumnIndex("isgreen"));
						mintime = cur.getInt(cur.getColumnIndex("msecs")) < duration ? cur
								.getInt(cur.getColumnIndex("msecs")) : duration;
					} else if (cur.getInt(cur.getColumnIndex("isgreen")) > isgreen) {
						isgreen = cur.getInt(cur.getColumnIndex("isgreen"));
						mintime = cur.getInt(cur.getColumnIndex("msecs"));
					} else {
						mintime = duration;
					}
					cur.close();
					// Log.e("result present", "" + mintime + "//" + isgreen);
				} else {
					mintime = (int) duration;
				}
				besttime.setText("Your Best time: "
						+ String.format("%.2f", (float) mintime / 1000)
						+ " Sec");
				Log.e("result data", "" + mintime + "//" + isgreen + "//"
						+ besttimecolor);
				if (isgreen == 1) {
					besttime.setTextColor(Color.GREEN);
				} else if (isgreen == -1) {
					besttime.setTextColor(Color.RED);
				} else {
					besttime.setTextColor(Color.YELLOW);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			// update the result to the database
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
				cur = cr.query(Baseclass.CONTENT_URI, null,
						"select * from stat", null, null);
				cv.put("_id", cur.getCount() + 1);
				cv.put("UserID", Baseclass.userid);
				cv.put("NodeID", phraseid);
				cv.put("msecs", mintime);
				cv.put("isgreen", isgreen);
				cv.put("Name", Baseclass.name);
				cr.insert(Baseclass.CONTENT_URIStats, cv);
			}
			// send the upadate score back to the server
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					Baseclass.streamtostring(Baseclass.httpclient(
							"http://107.21.123.15/ttservices/storeresult.php",
							json));
				}
			});
			t.start();
		} else {
			new Asynctask(act, phraseid, duration)
					.execute(textMatchList);
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

	public InputStream httpclient(String URL, JSONObject json) {
		InputStream is = null;
		HttpParams httpParameters = new BasicHttpParams(); // set connection
															// parameters
		HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
		HttpConnectionParams.setSoTimeout(httpParameters, 5000);
		HttpClient httpclient = new DefaultHttpClient(httpParameters);
		HttpResponse response = null;
		HttpPost httppost = new HttpPost(URL);
		// httppost.setHeader("json",json.toString());
		// httppost.getParams().setParameter("jsonpost",json);
		try {
			httppost.setHeader("Content-type", "application/json");
			StringEntity se = new StringEntity(json.toString());
			se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
					"application/json"));
			httppost.setEntity(se);
			response = httpclient.execute(httppost);
			StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				is = entity.getContent();
			} else {
				// Closes the connection.
				response.getEntity().getContent().close();
				throw new IOException(statusLine.getReasonPhrase());
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return is;
	}

	public String streamtostring(InputStream is) {
		// convert response to string
		try {
			int length;
			if (is.available() > 0)
				length = is.available();
			else
				length = 1;
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is), length);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			String result = sb.toString();
			is.close();
			reader = null;
			sb = null;
			Log.e("Received data", "" + result);
			return result;
		} catch (Exception e) {
			Log.e("log_tag", "Error converting result " + e.toString());
			e.printStackTrace();
			return null;
		}
	}

}
