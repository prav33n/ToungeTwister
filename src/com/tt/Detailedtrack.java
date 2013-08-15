/**
 * 
 */
package com.tt;

import java.util.HashMap;
import java.util.Locale;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Raven
 *
 */
public class Detailedtrack extends Baseclass implements OnInitListener {
	TextView phrase;
	Cursor node;
	TextToSpeech mTts;
	int phraseid,attemptnumber = 1;
	protected static final int RESULT_TEXT = 2;
	String currentphrase;
	ContentResolver cr;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);  
		setContentView(R.layout.detailedtrack);
		Bundle extras = getIntent().getExtras();
		phraseid = extras.getInt("phraseid");
		int nodeid = extras.getInt("nodeid");
		phrase = (TextView)findViewById(R.id.phrase);
        query =  "select  _id,phrase,length,updated from phrase  where _id = "+phraseid;
        cr = getContentResolver();
        node = cr.query(CONTENT_URI, projection, query, null, null);
        node.moveToFirst();
        currentphrase = node.getString(node.getColumnIndex("Phrase"));
        phrase.setText(currentphrase);
        node.close();
        Cursor cur = getscores(phraseid);
        if(cur.getCount()>0){
        	cur.moveToFirst();
        	TextView tv = (TextView)findViewById(R.id.resulttext);
			findViewById(R.id.resultwindow).setVisibility(View.VISIBLE);
			String txt = "Total Attempts : "+(cur.getInt(cur.getColumnIndex("redattempts"))+cur.getInt(cur.getColumnIndex("yellowattempts"))+cur.getInt(cur.getColumnIndex("greenattempts")))+"\n Passed Attempts : "+cur.getInt(cur.getColumnIndex("greenattempts"))+"\nPhrase Duration :"+(cur.getInt(cur.getColumnIndex("mingreentime"))/1000)+"S";
			tv.setText(txt);
        }
        	
        query =   "select  _id,phrase,length,updated from phrase  where _id in (select nodeid from tracknode where trackid = "+nodeid+");";
        node = getContentResolver().query(CONTENT_URI, projection, query, null, null);
        node.moveToFirst();
	}
	
	public void startspeech(View v){
		new Asynctask((Activity)this).execute(phraseid,attemptnumber++);
	}
	
	public void startplay(View v){
	//	Intent textIntent = new Intent();
		//textIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		//startActivityForResult(textIntent, RESULT_TEXT);
		
		//playback test
		try{
            AudioTrack audioTrack = new  AudioTrack(AudioManager.STREAM_MUSIC, 8000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, Baseclass.bytearray.length, AudioTrack.MODE_STATIC);
            audioTrack.flush();
            audioTrack.write(Baseclass.bytearray, 0, Baseclass.bytearray.length);
            audioTrack.play();
            

    } catch(Exception e){
    	e.printStackTrace();
    	Intent textIntent = new Intent();
		textIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(textIntent, RESULT_TEXT);
		
      Log.d("Audio","Playback Failed");
  }
		
		
		
	}
	public void nexttrack(View v){
		if(!node.isLast())
		node.moveToNext();
		changetrack();
	}
	public void previoustrack(View v){
		
		if(!node.isFirst())
		node.moveToPrevious();
		changetrack();
	/*	attemptnumber=0;
		phraseid = node.getInt(node.getColumnIndex("_id"));
		currentphrase = node.getString(node.getColumnIndex("Phrase"));
	    phrase.setText(currentphrase);
	     Cursor cur = getscores(phraseid);
	        if(cur.getCount()>0){
	        	cur.moveToFirst();
	        	TextView tv = (TextView)findViewById(R.id.resulttext);
				findViewById(R.id.resultwindow).setVisibility(View.VISIBLE);
				String txt = "Total Attempts : "+(cur.getInt(cur.getColumnIndex("redattempts"))+cur.getInt(cur.getColumnIndex("yellowattempts"))+cur.getInt(cur.getColumnIndex("greenattempts")))+"\n Passed Attempts : "+cur.getInt(cur.getColumnIndex("greenattempts"))+"\nPhrase Duration :"+(cur.getInt(cur.getColumnIndex("mingreentime"))/1000)+"S";
				tv.setText(txt);
	        } */
	    
	    
	}
	
	public void viewscore(View V){
		Intent i = new Intent(this, Score.class);
		i.putExtra("mode", "detailscore");
		i.putExtra("NodeID", phraseid);
		startActivity(i);
	}

	 @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	        super.onActivityResult(requestCode, resultCode, data);
	        Log.e("conf",""+requestCode);
	        switch (requestCode) {
			     case RESULT_TEXT :{
			    	 
			        	 if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
			                 // success, create the TTS instance
			                 mTts = new TextToSpeech(this, this);
				        	 
			             } else {
			                 // missing data, install it
			                 Intent installIntent = new Intent();
			                 installIntent.setAction(
			                     TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
			                 startActivity(installIntent);
			             }
   	
			        	 
			        }
	 	        }
	        super.onActivityResult(requestCode, resultCode, data);
	    }

	@Override
	public void onInit(int status) {
		// TODO Auto-generated method stub
		 if (status == TextToSpeech.SUCCESS) {
	            if (mTts.isLanguageAvailable(Locale.US) == TextToSpeech.LANG_AVAILABLE)
	                mTts.setLanguage(Locale.US);
	         HashMap<String, String> myHashAlarm = new HashMap<String, String>();
	       	 myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_STREAM,
	       	         String.valueOf(AudioManager.STREAM_ALARM));
	       	 mTts.speak(currentphrase, TextToSpeech.QUEUE_FLUSH, myHashAlarm);
	        } else if (status == TextToSpeech.ERROR) {
	            Toast.makeText(this, "Sorry! Text To Speech failed...",
	                    Toast.LENGTH_LONG).show();
	        }	
	}
	
	public Cursor getscores(int phraseid){
		String qry = "select * from attempt where NodeId="+phraseid;
		Cursor cur= cr.query(CONTENT_URI, projection, qry, null, null);
		return cur;
	}
	
	public boolean changetrack(){
		attemptnumber= 0;
		phraseid = node.getInt(node.getColumnIndex("_id"));
		currentphrase = node.getString(node.getColumnIndex("Phrase"));
	    phrase.setText(currentphrase);
	     Cursor cur = getscores(phraseid);
	        if(cur.getCount()>0){
	        	cur.moveToFirst();
	        	TextView tv = (TextView)findViewById(R.id.resulttext);
				findViewById(R.id.resultwindow).setVisibility(View.VISIBLE);
				String txt = "Points : "+cur.getInt(cur.getColumnIndex("Score"))+"\nTotal Attempts : "+(cur.getInt(cur.getColumnIndex("redattempts"))+cur.getInt(cur.getColumnIndex("yellowattempts"))+cur.getInt(cur.getColumnIndex("greenattempts")))+"\n Passed Attempts : "+cur.getInt(cur.getColumnIndex("greenattempts"))+"\nPhrase Duration :"+(cur.getInt(cur.getColumnIndex("mingreentime"))/1000)+"S";
				tv.setText(txt);
				cur.close();
	        }
	        else 
	        	findViewById(R.id.resultwindow).setVisibility(View.GONE);
	   return true;
	}
	
	
	
}
