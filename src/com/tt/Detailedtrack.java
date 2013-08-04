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
import android.media.AudioManager;
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
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);  
		setContentView(R.layout.detailedtrack);
		Bundle extras = getIntent().getExtras();
		phraseid = extras.getInt("phraseid");
		int nodeid = extras.getInt("nodeid");
		phrase = (TextView)findViewById(R.id.phrase);
        query =  "select  _id,phrase,length,updated from phrase  where _id = "+phraseid;
        ContentResolver cr = getContentResolver();
        node = getContentResolver().query(CONTENT_URI, projection, query, null, null);
        node.moveToFirst();
        currentphrase = node.getString(node.getColumnIndex("Phrase"));
        phrase.setText(currentphrase);
        node.close();
        query =   "select  _id,phrase,length,updated from phrase  where _id in (select nodeid from tracknode where trackid = "+nodeid+");";
        node = getContentResolver().query(CONTENT_URI, projection, query, null, null);
        node.moveToFirst();
	}
	
	public void startspeech(View v){
		new Asynctask((Activity)this).execute(phraseid,attemptnumber++);
	}
	
	public void startplay(View v){
		Intent textIntent = new Intent();
		textIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(textIntent, RESULT_TEXT);
	}
	public void nexttrack(View v){
		if(!node.isLast())
		node.moveToNext();
		attemptnumber= 0;
		phraseid = node.getInt(node.getColumnIndex("_id"));
		currentphrase = node.getString(node.getColumnIndex("Phrase"));
	    phrase.setText(currentphrase);
	}
	public void previoustrack(View v){
		
		if(!node.isFirst())
		node.moveToPrevious();
		attemptnumber=0;
		phraseid = node.getInt(node.getColumnIndex("_id"));
		currentphrase = node.getString(node.getColumnIndex("Phrase"));
	    phrase.setText(currentphrase);
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
	
	
	
	
}
