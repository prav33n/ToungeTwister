package com.tt;

import android.database.Cursor;
import android.os.Bundle;
import android.webkit.WebView;

public class Score extends Baseclass {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);  
		setContentView(R.layout.scores);
	WebView scoreview = (WebView)findViewById(R.id.scoreview);	
	query = "select attemptnumber,passed,score,feedback,phrase.[Phrase] from attempt INNER JOIN  phrase on attempt.[NodeID] = phrase.[_id]";
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
	 cur.close();  
	 String mime = "text/html";
	 String encoding = "utf-8";
	    //myWebView.getSettings().setJavaScriptEnabled(true);
	    scoreview.loadDataWithBaseURL(null, html, mime, encoding, null);
	
	}
}
