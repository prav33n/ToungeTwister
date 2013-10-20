/**
 * 
 */
package com.tt;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Raven
 * 
 */
public class Detailedtrack extends Baseclass implements OnInitListener,
	RecognitionListener, OnGesturePerformedListener {
	TextView phrase, tv;
	Cursor node, leader;
	TextToSpeech mTts;
	int mintime, isgreen, phraseid, attemptnumber = 1,position,nodeid;
	protected static final int RESULT_TEXT = 2;
	boolean issoundavailable = false, processing = false;
	float currenttime;
	AlertDialog.Builder alertbox;
	AlertDialog dlg;
	String currentphrase;
	ContentResolver cr;
	Activity act;
	ListView list;
	ImageButton btnprevious, btnnext;
	SpeechRecognizer sr;
	Long start_time, end_time, difference;
	GestureLibrary gestureLib;
	Button leaderboard;
	CountDownTimer timer;
	ByteArrayOutputStream soundstream;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detailedtrack);
		Bundle extras = getIntent().getExtras();
		phraseid = extras.getInt("phraseid");
		position = extras.getInt("position");
		nodeid = extras.getInt("nodeid");
		cr = getContentResolver();
		Log.e("TT phrase id",""+phraseid+"//"+position+"//"+nodeid);
		ScrollView sv = (ScrollView) findViewById(R.id.detailedtrack);
		GestureOverlayView gesture = (GestureOverlayView) findViewById(R.id.ttgesture);
		gestureLib = GestureLibraries.fromRawResource(this, R.raw.gestures);
		if (!gestureLib.load())
			finish();
		gesture.addOnGesturePerformedListener(this);
		sv.requestDisallowInterceptTouchEvent(true);
		act = this;
		btnprevious = (ImageButton) findViewById(R.id.buttonprevious);
		btnnext = (ImageButton) findViewById(R.id.buttonnext);
		leaderboard = (Button) findViewById(R.id.viewscore);
		start_time = (long) 0;
		end_time = (long) 0;
		difference = (long) 0;
		LayoutInflater inflater = getLayoutInflater();
		ViewGroup listheader = (ViewGroup) inflater.inflate(
				R.layout.listheader, list, false);		
		phrase = (TextView) findViewById(R.id.phrase);
		query = "select  _id,phrase,length,updated from phrase  where _id in (select nodeid from tracknode where trackid = "+ nodeid + ")";
		node = cr.query(CONTENT_URI, projection, query, null, null);
		node.moveToPosition(position);
		changetrack();
	/*	query = "select  _id,phrase,length,updated from phrase  where _id = "
				+ phraseid;
		cr = getContentResolver();
		node = cr.query(CONTENT_URI, projection, query, null, null);
		node.moveToFirst();
		currentphrase = node.getString(node.getColumnIndex("Phrase"));
		phrase.setText(currentphrase);
		node.close();
		getscores(phraseid);
		query = "select  _id,phrase,length,updated from phrase  where _id in (select nodeid from tracknode where trackid = "+ nodeid + ")";
		node = cr.query(CONTENT_URI, projection, query, null, null);
		node.moveToFirst();*/
		
	}

	public void startspeech(View v) {
		sr = SpeechRecognizer.createSpeechRecognizer(this
				.getApplicationContext());
		sr.setRecognitionListener(this);
		Intent intents = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intents.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intents.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "com.tt");
		intents.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
		//intents.putExtra(RecognizerIntent.ACTION_RECOGNIZE_SPEECH,RecognizerIntent.EXTRA_CONFIDENCE_SCORES);
		intents.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
		intents.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,"en-US");
		intents.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,2500);
		intents.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS,phrase.length()*500);
		// intents.putExtra(RecognizerIntent.ACTION_RECOGNIZE_SPEECH,
		// RecognizerIntent.EXTRA_RESULTS);
		sr.startListening(intents);
		LayoutInflater factory = LayoutInflater.from(this);
		final View textEntryView = factory.inflate(R.layout.asrdialog, null);
		alertbox = new AlertDialog.Builder(act);
		alertbox.setView(textEntryView);
		tv = (TextView) textEntryView.findViewById(R.id.asrdlgtext);
		if (!isOnline())
			tv.setText("No Internet Access");
		else
			tv.setText("Listening");
		alertbox.setIcon(R.drawable.speech);
		dlg = alertbox.create();
		dlg.setCancelable(false);
		issoundavailable = true;
		soundstream = new ByteArrayOutputStream();
	}

	public void startplay(View v) {
		// playback test
		// Log.d("Audio","Playback Started"+issoundavailable);
		if (issoundavailable) {
			try {
				AudioManager mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
	        	int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	        	mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, AudioManager.FLAG_PLAY_SOUND);
				AudioTrack audioTrack = new AudioTrack(
						AudioManager.STREAM_MUSIC, 8000,
						AudioFormat.CHANNEL_OUT_MONO,
						AudioFormat.ENCODING_PCM_16BIT,
						soundstream.size(), AudioTrack.MODE_STATIC);
				audioTrack.flush();
				audioTrack.write(soundstream.toByteArray(),0,soundstream.size());
				audioTrack.play();

			} catch (Exception e) {
				e.printStackTrace();
				Log.d("Audio", "Playback Failed");
			}
		} else {
			Intent textIntent = new Intent();
			textIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
			startActivityForResult(textIntent, RESULT_TEXT);
		}

	}

	public void nexttrack(View v) {
		if (!node.isLast())
			node.moveToNext();
		else if (node.isLast())
			Toast.makeText(this, "Last Track selected",
					Toast.LENGTH_SHORT).show();
			
		changetrack();
	}

	public void previoustrack(View v) {
		if (!node.isFirst())
			node.moveToPrevious();
		else if (node.isFirst())
			Toast.makeText(this, "First Track selected",
					Toast.LENGTH_SHORT).show();
			
		changetrack();
	}

	public void viewscore(View V) {
		Intent i = new Intent(this, Leaderboard.class);
		i.putExtra("mode", "scoreboard");
		i.putExtra("NodeID", phraseid);
		i.putExtra("currentime", currenttime);
		startActivity(i);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.e("conf", "" + requestCode);
		switch (requestCode) {
		case RESULT_TEXT: {
			if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
				// success, create the TTS instance
				mTts = new TextToSpeech(this, this);

			} else {
				// missing data, install it
				Intent installIntent = new Intent();
				installIntent
						.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
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

	public boolean getscores(int id) {
		TextView besttime = (TextView) findViewById(R.id.besttime);
		Cursor cur = cr.query(CONTENT_URI, projection,
				"select * from attempt where NodeID = " + id
						+ " ORDER BY msecs ASC", null, null);
		if (cur.getCount() > 0) {
			cur.moveToFirst();
			if (cur.getInt(cur.getColumnIndex("isgreen")) == 1) {
				besttime.setTextColor(Color.GREEN);
				findViewById(R.id.viewscore).setVisibility(View.VISIBLE);
			} else if (cur.getInt(cur.getColumnIndex("isgreen")) == 0) {
				besttime.setTextColor(Color.YELLOW);
				findViewById(R.id.viewscore).setVisibility(View.VISIBLE);
			} else {
				besttime.setTextColor(Color.RED);
				findViewById(R.id.viewscore).setVisibility(View.VISIBLE);
			}
			besttime.setText("Best Time :"
					+ String.format(
							"%.2f",
							(float) cur.getInt(cur.getColumnIndex("msecs")) / 1000)
					+ " Sec");
			cur.close();
			findViewById(R.id.resulttext).setVisibility(View.GONE);
			findViewById(R.id.resultwindow).setVisibility(View.VISIBLE);
			besttime.setVisibility(View.VISIBLE);
		} else
			findViewById(R.id.resultwindow).setVisibility(View.GONE);
		return true;
	}

	public boolean changetrack() {
		// attemptnumber= 0;
		issoundavailable = false;
		soundstream = new ByteArrayOutputStream();
		phraseid = node.getInt(node.getColumnIndex("_id"));
		currentphrase = node.getString(node.getColumnIndex("Phrase"));
		phrase.setText(currentphrase);
		getscores(node.getInt(node.getColumnIndex("_id")));
		return true;
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (sr != null)
			sr.destroy();

	}

	@Override
	public void onBeginningOfSpeech() {
		// TODO Auto-generated method stub
		start_time = System.currentTimeMillis();

	}

	@Override
	public void onBufferReceived(byte[] buffer) {
		// TODO Auto-generated method stub
		// get the byte array of the sound and write it to sound stream
		try {
			soundstream.write(buffer, 0, buffer.length);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("sound track", "index outof bound");
		}
	}

	@Override
	public void onEndOfSpeech() {
		// TODO Auto-generated method stub
		end_time = System.currentTimeMillis();
		difference = end_time - start_time;
		Log.e("diff", "" + difference);
		tv.setText("Processing");
		dlg.show();
		processing = true;
		/** CountDownTimer */
		timer = new CountDownTimer(30000, 30000) {
			public void onFinish() {
				try {
					if(dlg.isShowing()){
						dlg.dismiss();
						findViewById(R.id.resultwindow).setVisibility(View.VISIBLE);
						TextView test = (TextView) findViewById(R.id.resulttext);
						test.setVisibility(View.VISIBLE);
						test.setText("Processing time exceeded \nPlease try Again");
						findViewById(R.id.besttime).setVisibility(View.GONE);
						findViewById(R.id.viewscore).setVisibility(View.GONE);
					//	Button leaderboard = (Button)act.findViewById(R.id.viewscore);
						sr.destroy();
						}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			@Override
			public void onTick(long arg0) {
				// TODO Auto-generated method stub

			}
		};
		timer.start();

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
		dlg.show();

	}

	@SuppressWarnings("unchecked")
	@Override
	public void onResults(Bundle results) {
		// TODO Auto-generated method stub
		// String str = new String();
		// Log.d(TAG, "onResults " + results);
		if (results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
				.isEmpty()) {
			Log.e("ASR" + TAG, "onResults empty" + results.size());
			dlg.dismiss();
				} else {
			Log.e("ASR" + TAG,	"results received"+ results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)+phraseid+"//"+difference);
			new Asynctask(this, phraseid, difference).execute(results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION));
			dlg.dismiss();
		}
		timer.cancel();

	}

	@Override
	public void onRmsChanged(float rmsdB) {
		// TODO Auto-generated method stub

	}

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) act
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		return cm.getActiveNetworkInfo() != null
				&& cm.getActiveNetworkInfo().isConnectedOrConnecting();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent e) {
		return super.dispatchTouchEvent(e);
	}

	@Override
	public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
		// TODO Auto-generated method stub

		ArrayList<Prediction> predictions = gestureLib.recognize(gesture);
		Log.e("gesture", "Logged" + predictions.toString());
		for (Prediction prediction : predictions) {
			if (prediction.score > 1.0) {
				if (prediction.name.equalsIgnoreCase("bamr")
						|| prediction.name.equalsIgnoreCase("right")) {
					Log.e("gesture", "right");
					if (!node.isFirst())
						node.moveToPrevious();
					changetrack();
				}

				else if (prediction.name.equalsIgnoreCase("baml")
						|| prediction.name.equalsIgnoreCase("left")) {
					Log.e("gesture", "left");
					if (!node.isLast())
						node.moveToNext();
					changetrack();
				}
			}
		}

	}

}
