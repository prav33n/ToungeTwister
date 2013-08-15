package com.tt;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

@SuppressLint("SetJavaScriptEnabled")
public class Score extends Baseclass {
	ContentResolver cr;
	String html = new String();
	WebView scoreview;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);  
		setContentView(R.layout.scores);

		scoreview = (WebView)findViewById(R.id.scoreview);	
		Bundle extras = getIntent().getExtras();
		if(extras.getString("mode").equals("detailscore")){
			int nodeid = extras.getInt("NodeID");
			cr = this.getContentResolver();
			query = "select * from attempt where NodeID="+nodeid;
			Cursor cur = cr.query(CONTENT_URI, projection, query, null, null);
			cur.moveToFirst();
			html ="<!DOCTYPE HTML> <html> <head> <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"> <title>Highcharts Example</title> <script type=\"text/javascript\" src=\"file:///android_asset/jquery.min.js\"></script> <script type=\"text/javascript\"> $(function () { $('#container').highcharts({ chart: { type: 'column' }, title: { text:'Score view' }, subtitle: { text: 'Phrase' }, xAxis: { categories:['Attempts'], }, yAxis: { min: 0, title: { text: 'No attempts' } }, tooltip: { headerFormat: '<span style=\"font-size:10px\">{point.key}</span><table>', pointFormat: '<tr><td style=\"padding:0\">{series.name}: </td>' + '<td style=\"padding:0\"><b>{point.y:.1f}</b></td></tr>', footerFormat: '</table>', shared: false, useHTML: true }, plotOptions: { column: { pointPadding: 0.2, borderWidth: 0 } }, series: [{ name: 'Red Attempts', color: 'Red', data: ["+cur.getInt(cur.getColumnIndex("redattempts"))+"] }, { name: 'Yellow Attempts', color:'yellow', data: ["+cur.getInt(cur.getColumnIndex("yellowattempts"))+"] }, { name: 'Green Attempts', color:'green', data: ["+cur.getInt(cur.getColumnIndex("greenattempts"))+"] }] }); }); </script> </head> <body> <script src=\"file:///android_asset/highcharts.js\"></script> <div id=\"container\" style=\"min-width: 310px; height: 400px; margin: 0 auto\"></div> </body> </html>";
			scoreview.getSettings().setJavaScriptEnabled(true);
			String mime = "text/html";
			String encoding = "utf-8";
			//myWebView.getSettings().setJavaScriptEnabled(true);
			scoreview.loadDataWithBaseURL("file:///android_asset/", html, mime, encoding, null);}
		else if(extras.getString("mode").equals("leaderboard")){

			Thread updatescore = new Thread(new Runnable() {
				public void run() { 
					JSONObject json = new JSONObject();
					try {
						json.put("score","topscore");
						String result = streamtostring(Baseclass.httpclient("http://107.21.123.15/asrresult/totalscore.php",json));
						JSONArray jarray = new JSONArray(result);
						html = "<html><body><table border='1px' style='width:100%;'><tr><th>User</th><th>Score</th><th>Level</th></tr>";
						for(int i = 0; i<jarray.length();i++){
							JSONObject jobj = new JSONObject();
							jobj = jarray.getJSONObject(i);
							if(jobj.getInt("UserID")==Baseclass.userid){
								html+= "<tr bgcolor=\"#0099CC\"><td>"+jobj.getString("Name")+"</td><td>"+
								jobj.getInt("Score")+"</td><td>"+
								jobj.getInt("Level")+"</td></tr>";
							}
							else{
								html+= "<tr><td>"+jobj.getString("Name")+"</td><td>"+
								jobj.getInt("Score")+"</td><td>"+
								jobj.getInt("Level")+"</td></tr>";}

						}
						html +="</table></body></html>";
						scoreview.getSettings().setJavaScriptEnabled(true);
						String mime = "text/html";
						String encoding = "utf-8";
						//myWebView.getSettings().setJavaScriptEnabled(true);
						scoreview.loadDataWithBaseURL(null, html, mime, encoding, null);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			updatescore.start();




			/*	query = "select attemptnumber,passed,score,feedback,phrase.[Phrase] from attempt INNER JOIN  phrase on attempt.[NodeID] = phrase.[_id]";
		Cursor cur = this.getContentResolver().query(CONTENT_URI, projection, query, null, null);
		String html = "<html><body><table border='1px' style='width:100%;'><tr><th>Phrase</th><th>no attempts</th><th>Score</th><th>Your result</th><th>Passed</th></tr>";
		if(cur.getCount() > 0)
			cur.moveToFirst();
		for(int i=0;i < cur.getCount(); i++)
		{
			html+= "<tr><td>"+ cur.getString(cur.getColumnIndex("Phrase"))+"</td><td>"+
			cur.getInt(cur.getColumnIndex("AttemptNumber"))+"</td><td>"+
			cur.getInt(cur.getColumnIndex("Score"))+"</td><td>"+
			cur.getString(cur.getColumnIndex("Feedback"))+"</td><td>"+
			cur.getInt(cur.getColumnIndex("Passed"))+"</td></tr>";
			if(!cur.isLast())
			cur.moveToNext();
		}	
		//		"<tr><td>123330</td><td>3 hours 24 mins</td><td>213</td><td>213</td></tr>" ;

				html +="</table></body></html>";
		 cur.close();  */


		}

		//String html ="<!DOCTYPE HTML> <html> <head> <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"> <title>Highcharts Example</title> <script type=\"text/javascript\" src=\"file:///android_asset/jquery.min.js\"></script> <script type=\"text/javascript\"> $(function () { $('#container').highcharts({ chart: { type: 'column' }, title: { text:'Score view' }, subtitle: { text: 'Phrase' }, xAxis: { categories:['Attempts'], }, yAxis: { min: 0, title: { text: 'No attempts' } }, tooltip: { headerFormat: '<span style=\"font-size:10px\">{point.key}</span><table>', pointFormat: '<tr><td style=\"padding:0\">{series.name}: </td>' + '<td style=\"padding:0\"><b>{point.y:.1f}</b></td></tr>', footerFormat: '</table>', shared: false, useHTML: true }, plotOptions: { column: { pointPadding: 0.2, borderWidth: 0 } }, series: [{ name: 'Red Attempts', color: 'Red', data: [3] }, { name: 'Yellow Attempts', color:'yellow', data: [4] }, { name: 'Green Attempts', color:'green', data: [1] }] }); }); </script> </head> <body> <script src=\"file:///android_asset/highcharts.js\"></script> <div id=\"container\" style=\"min-width: 310px; height: 400px; margin: 0 auto\"></div> </body> </html>";

	}

	public String streamtostring(InputStream is){
		//convert response to string
		try{
			int length;
			if(is.available()>0)
				length = is.available();
			else 
				length = 1;
			BufferedReader reader = new BufferedReader(new InputStreamReader(is), length);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) 
			{
				sb.append(line);
			}
			String result=sb.toString();
			is.close();
			reader = null;
			sb = null;
			Log.e("Received data",""+result);
			return result;
		}catch(Exception e){
			Log.e("log_tag", "Error converting result "+e.toString());
			e.printStackTrace();
			return null;
		}

	}

}
