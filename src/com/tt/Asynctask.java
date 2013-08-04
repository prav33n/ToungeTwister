/**
 * 
 */
package com.tt;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * @author Raven
 * @param <Params>
 *
 */

public class Asynctask extends AsyncTask<Integer,Integer,String> implements RecognitionListener {
	TextView tv;
	Bundle results = new Bundle();
	Baseclass bs;
	byte[] sound;
	ContentResolver cr;
   private static final String TAG = "MyStt3Activity";
   long end_time, start_time,difference;
   String result;
   Context con;
   Activity act;
   Cursor user;
   SpeechRecognizer sr;
   AlertDialog.Builder alertbox;
   AlertDialog dlg;
   int attemptno,phraseid,attemptid,userID;
   JSONObject jobj,json;
   public Asynctask(Activity act){
	   this.con = act.getApplicationContext();
		this.act = act;
		cr = act.getContentResolver();
   }
   
   
	   @Override
	   protected void onPreExecute() {
	      super.onPreExecute();
	      user = cr.query(Baseclass.CONTENT_URI, null,"select UserID from user", null, null);
	      user.moveToFirst();
	      userID = user.getInt(user.getColumnIndex("UserID"));
	      Log.e(TAG,""+userID);
	        sr = SpeechRecognizer.createSpeechRecognizer(this.con);       
			sr.setRecognitionListener(this); 
			Intent intents = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);        
	        intents.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
	        intents.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass()
					.getPackage().getName());
	        intents.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,5); 
	        intents.putExtra(RecognizerIntent.ACTION_RECOGNIZE_SPEECH, RecognizerIntent.EXTRA_CONFIDENCE_SCORES);
	       // intents.putExtra(RecognizerIntent.ACTION_RECOGNIZE_SPEECH, RecognizerIntent.EXTRA_RESULTS);
	        sr.startListening(intents);
	        tv = (TextView) act.findViewById(R.id.phrase);
	        alertbox = new AlertDialog.Builder(act);
	        alertbox.setTitle("Listening");
            alertbox.setIcon(R.drawable.speech);
            dlg = alertbox.create();
            dlg.show();
	   }
	
	

	@Override
	protected String doInBackground(Integer... params) {
		// TODO Auto-generated method stub
		attemptno = params[1];
		phraseid = params[0];
		for(int i=0;;i++){
			if(!results.isEmpty()){
				Log.e(TAG,"completed");
			break;	
			}
		} 
		   ArrayList<String> textMatchList = results
			.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
		   publishProgress(1);
			//Log.e("http string",URL+"//"+send.toString());
			HttpParams httpParameters = new BasicHttpParams();   //set connection parameters
			HttpConnectionParams.setConnectionTimeout(httpParameters, 10000);
			HttpConnectionParams.setSoTimeout(httpParameters, 10000);
			HttpClient httpclient = new DefaultHttpClient(httpParameters);
			HttpResponse response= null;
			HttpPost httppost = new HttpPost("http://107.21.123.15/asrresult/asrresult.php");	
			//httppost.setHeader("Content-type", "application/json");
			//create a json object to post attemptid, phrase and asr result
			try{
			json = new JSONObject();
			json.put("phrase", tv.getText());
			json.put("asrresult",textMatchList);
			json.put("attemptid",phraseid);
			json.put("attemptno",attemptno);
			json.put("userid",userID);
			json.put("timediff",difference);
			// Post the data:
			JSONArray postjson=new JSONArray();
	        postjson.put(json);
            httppost.setHeader("json",json.toString());
            httppost.getParams().setParameter("jsonpost",json);
 
            // Execute HTTP Post Request
            //System.out.print(json);
			//se = new StringEntity(textMatchList.toString());
			//se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/text"));
			//httppost.setEntity(se);
			response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			InputStream is = entity.getContent();
			this.result = streamtostring(is);
			}
			catch(Exception e){
				e.printStackTrace();
			}
		Log.e("tag",textMatchList.toString());
		return  result;
	}
	

    protected void onProgressUpdate(Integer... progress) {
        dlg.setTitle("Processing");
    }
	
	
	   @Override
	   protected void onPostExecute(String result) {
	      super.onPostExecute(result);
	      ArrayList<String> textMatchList = results
			.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
	      //Log.e("tagtest",result);
	      float rating = 0;
	      String res = new String();
	      String attempt = new String();
	      try {
	jobj = new JSONObject(result);
	tv = (TextView) act.findViewById(R.id.resulttext);
	act.findViewById(R.id.resultwindow).setVisibility(View.VISIBLE);
	res = jobj.getString("asrphrase");
	attempt = jobj.getString("attemptid");
	rating  = (float) jobj.getDouble("score");
	if(res.isEmpty())
		tv.setText("Please try again");
	else 
	 tv.setText("Result :"+res+"\n Your time :"+((float)difference/1000)+" S\n Points : "+(int)rating);
/*	 RatingBar rb = (RatingBar)act.findViewById(R.id.resultrating);
	 rb.setProgress((int)rating);*/
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	    dlg.dismiss();
		 
	     /*	ArrayList<Float>	confidence = new ArrayList<Float>();
		for (float s : results.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES))
		{
		   // do stuff with "s"
			confidence.add(s);
		}
		
		Log.e(TAG,""+confidence.toString());*/
	  // ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION); 
//	   act.findViewById(R.id.resultwindow).setVisibility(View.VISIBLE);
//	   tv = (TextView) act.findViewById(R.id.resulttext);
//	   tv.setText("Result :"+textMatchList.get(0)+"\n"+textMatchList.get(1)+"\n Your time :"+((float)difference/1000)+" S");
//	   RatingBar rb = (RatingBar)act.findViewById(R.id.resultrating);
//     //  int rating  = (int)Math.floor(confidence.get(0)*10);
//      // rb.setProgress(rating);
//       dlg.dismiss();
//       
//       
//       //attempt table       
        Cursor cur = cr.query(Baseclass.CONTENT_URI, null,"select count(_id) as number from attempt", null, null);
        Log.e(TAG,""+attemptno+"//"+phraseid);
        cur.moveToFirst();
  //       Log.e(TAG,""+cur.getInt(0));
         attemptid=cur.getInt(0)+1;
         String query = "select * from attempt where NodeID ="+phraseid;
        cur = act.getContentResolver().query(Baseclass.CONTENT_URI, null,query, null, null);
        ContentValues resultinsert = new ContentValues();
   
        /*if(cur.getCount() > 0){
            String where = "_id = "+phraseid;
            Log.e(TAG,""+cur.getCount()+"//"+where);
            resultinsert.put("AttemptNumber", attemptno);
            resultinsert.put("Passed", 1);
            resultinsert.put("Feedback", res);
            resultinsert.put("Score", (int)rating);
        	Log.e("rowsupdated",""+cr.update(Baseclass.CONTENT_URIAttempt, resultinsert, where, null));
        	
        	 resultinsert = new ContentValues();
        	 where = "AttemptID = "+phraseid;
             resultinsert.put("Result", res);
             resultinsert.put("ASRResultID",attempt);
             resultinsert.put("Position", 1);
             resultinsert.put("Score",(int)rating);
             resultinsert.put("ASR", textMatchList.toString());
             Log.e("rowsupdated",""+cr.update(Baseclass.CONTENT_URIASR, resultinsert, where, null));
        	}
        else if(cur.getCount()==0){
            resultinsert.put("_id",phraseid);
            resultinsert.put("UserId",1);
            resultinsert.put("NodeId",phraseid);
            resultinsert.put("AttemptNumber", attemptno);
            resultinsert.put("Passed", 1);
            resultinsert.put("Feedback", res);
            resultinsert.put("Score", (int)rating);
            cr.insert(Baseclass.CONTENT_URIAttempt, resultinsert);
            
            
            resultinsert = new ContentValues();
            resultinsert.put("_id",Math.random());
            resultinsert.put("UserId",userID);
            resultinsert.put("ASRResultID",attempt);
            resultinsert.put("AttemptID", phraseid);
            resultinsert.put("Result", res);
            resultinsert.put("Position", 1);
            resultinsert.put("Score",(int)rating);
            resultinsert.put("ASR", textMatchList.toString());
            cr.insert(Baseclass.CONTENT_URIASR, resultinsert);    
          }*/
      
        resultinsert.put("_id",attemptid);
        resultinsert.put("UserId",1);
        resultinsert.put("NodeId",phraseid);
        resultinsert.put("AttemptNumber", attemptno);
        resultinsert.put("Passed", 1);
        resultinsert.put("Feedback", res);
        resultinsert.put("Score", (int)rating);
        resultinsert.put("Comment", ""+difference);
        cr.insert(Baseclass.CONTENT_URIAttempt, resultinsert);
        
        
        resultinsert = new ContentValues();
        resultinsert.put("_id",attemptid);
        resultinsert.put("UserId",userID);
        resultinsert.put("ASRResultID",attempt);
        resultinsert.put("AttemptID", phraseid);
        resultinsert.put("Result", res);
        resultinsert.put("Position", 1);
        resultinsert.put("Score",(int)rating);
        resultinsert.put("ASR", textMatchList.toString());
        cr.insert(Baseclass.CONTENT_URIASR, resultinsert);   
       
        
	   }
	   
	  
	   
	   
	@Override
	public void onBeginningOfSpeech() {
		// TODO Auto-generated method stub
		start_time = System.currentTimeMillis();
		
	}

	@Override
	public void onBufferReceived(byte[] buffer) {
		// TODO Auto-generated method stub
		
		
	}

	@Override
	public void onEndOfSpeech() {
		// TODO Auto-generated method stub
		end_time = System.currentTimeMillis();
		difference = end_time-start_time;
		Log.e("diff",""+difference);
	}

	@Override
	public void onError(int error) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEvent(int eventType, Bundle params) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPartialResults(Bundle partialResults) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReadyForSpeech(Bundle params) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onResults(Bundle results) {
		// TODO Auto-generated method stub
		 String str = new String();
         Log.d(TAG, "onResults " + results);
         this.results = results;
   
  	}

	@Override
	public void onRmsChanged(float rmsdB) {
		// TODO Auto-generated method stub
		
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
