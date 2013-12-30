/**
 * 
 */
package com.tt;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Prediction;
import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.tongue.twister.R;

/**
 * @author Praveen Jelish View code to handle the individual track view and
 *         handling the recoding, getting the result from google server.
 */
public class Detailedtrack extends Baseclass implements OnInitListener,
		RecognitionListener, OnGesturePerformedListener {
	TextView phrase, tv;
	Cursor node, leader;
	TextToSpeech mTts;
	int mintime, isgreen, phraseid, attemptnumber = 1, position, nodeid,
			keycode;
	protected static final int RESULT_TEXT = 2;
	boolean issoundavailable = false, processing = false;
	float currenttime;
	AlertDialog.Builder alertbox;
	AlertDialog dlg;
	Dialog dialog;
	String currentphrase, nodename;
	ContentResolver cr;
	Activity act;
	ListView list;
	ImageButton btnprevious, btnnext;
	SpeechRecognizer sr;
	Long start_time, end_time, difference;
	GestureLibrary gestureLib;
	Button leaderboard, replay;
	CountDownTimer timer;
	ByteArrayOutputStream soundstream;
	AudioTrack audioTrack = null;
	AudioRecord audiorecord;
	AudioManager mAudioManager;
	ImageButton share;
	ScrollView container;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.detailedtrack);
		Bundle extras = getIntent().getExtras();
		nodename = extras.getString("nodename");
		TextView headertext = (TextView) findViewById(R.id.headertext);
		headertext.setText(nodename);
		phraseid = extras.getInt("phraseid");
		position = extras.getInt("position");
		Baseclass.position = position;
		nodeid = extras.getInt("nodeid");
		// get the view for setting the ads and set the ads using the gogole
		// admob sdk code
		LinearLayout rel = (LinearLayout) findViewById(R.id.ads_layout);
		container = (ScrollView) findViewById(R.id.detailedtrack); // get scroll
																	// View
		new Ad(rel, this);
		cr = getContentResolver();
		// Log.e("TT phrase id",""+phraseid+"//"+position+"//"+nodeid);
		GestureOverlayView gesture = (GestureOverlayView) findViewById(R.id.ttgesture);
		gestureLib = GestureLibraries.fromRawResource(this, R.raw.gestures);
		if (!gestureLib.load())
			finish();
		gesture.addOnGesturePerformedListener(this);
		container.requestDisallowInterceptTouchEvent(true);
		act = this;
		btnprevious = (ImageButton) findViewById(R.id.buttonprevious);
		btnnext = (ImageButton) findViewById(R.id.buttonnext);
		share = (ImageButton) findViewById(R.id.sharescore);
		leaderboard = (Button) findViewById(R.id.viewscore);
		replay = (Button) findViewById(R.id.replay);
		replay.setText("Hint");
		start_time = (long) 0;
		end_time = (long) 0;
		difference = (long) 0;
		LayoutInflater inflater = getLayoutInflater();
		inflater.inflate(R.layout.listheader, list, false);
		phrase = (TextView) findViewById(R.id.phrase);
		query = "select  _id,phrase,length,updated from phrase  where _id in (select nodeid from tracknode where trackid = "
				+ nodeid + ")";
		node = cr.query(CONTENT_URI, projection, query, null, null);
		// move the cursor to the last position in the Track list view
		node.moveToPosition(position);
		changetrack();
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
		intents.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US.toString());
		intents.putExtra(
				RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE,
				Locale.US.toString());
		intents.putExtra(
				RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,
				2500);
		intents.putExtra(
				RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS,
				phrase.length() * 500);
		sr.startListening(intents);
		// close timer if the user starts a new speech or moves to a new track
		closetimer();
		// create a dialog using a custom view
		LayoutInflater factory = LayoutInflater.from(this);
		final View textEntryView = factory.inflate(R.layout.asrdialog, null);
		tv = (TextView) textEntryView.findViewById(R.id.asrdlgtext);
		// set the custom theme for the dialog
		dialog = new Dialog(this, R.style.ThemeDialogCustom);
		// remove the tile for the dialog
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		// you can move the dialog, so that is not centered
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int height = displaymetrics.heightPixels;
		// int width = displaymetrics.widthPixels;
		// Log.e("dialog display",""+width+"//"+height);

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialog.getWindow().getAttributes());
		lp.y = (height - 200);
		dialog.getWindow().setAttributes(lp);
		dialog.setContentView(textEntryView);
		dialog.setCancelable(true);
		issoundavailable = true;
		soundstream = new ByteArrayOutputStream();
	}

	public void startplay(View v) {
		// code to play back the recorded sound or start TTS if the recording is
		// not available
		Log.d("Audio", issoundavailable + "//" + soundstream.size());
		if (issoundavailable && soundstream.size() > 0) {
			setVolumeControlStream(AudioManager.STREAM_ALARM);
			mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			// Request audio focus for playback
			int result = mAudioManager.requestAudioFocus(null,
														// Use the music stream.
														AudioManager.STREAM_ALARM,
														// Request permanent focus.
														AudioManager.AUDIOFOCUS_GAIN);

			if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
				Log.d("Audio", "Playback Started" + issoundavailable);
				// Start playback.
				try {
					int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
					mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM);
					if (mAudioManager.isBluetoothA2dpOn()) {
						// Adjust output for Bluetooth.
						maxVolume = maxVolume / 2;
					} 
					else if (mAudioManager.isWiredHeadsetOn()) 
					{
						// Adjust output for headsets
						maxVolume = maxVolume / 2;
					}
					mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, maxVolume, AudioManager.FLAG_PLAY_SOUND);
					audioTrack = new AudioTrack(AudioManager.STREAM_ALARM,8000,AudioFormat.CHANNEL_OUT_MONO,AudioFormat.ENCODING_PCM_16BIT, soundstream.size(),AudioTrack.MODE_STATIC);
					audioTrack.flush();
					audioTrack.write(soundstream.toByteArray(), 0,soundstream.size());
					Log.e("Audio status", "" + audioTrack.getPlayState() + "//"+ mAudioManager.isMusicActive());
					if (audioTrack.getPlayState() == 1)
						audioTrack.play();
					Log.e("Audio status after", "" + audioTrack.getPlayState()+ "//" + mAudioManager.isMusicActive());
				} catch (Exception e) {
					e.printStackTrace();
					Toast toast = Toast.makeText(this, "Audio Error",Toast.LENGTH_SHORT);
					toast.show();
				}
			}
		} else {
			Intent textIntent = new Intent();
			textIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
			startActivityForResult(textIntent, RESULT_TEXT);
		}

	}

	public void nexttrack(View v) {
		if (!node.isLast()) {
			node.moveToNext();
			// move the position of the tracklist page by 1, used while
			// returning to previous page.
			Baseclass.position++;
		} else if (node.isLast()) {
			Toast toast = Toast.makeText(this, "Last Phrase Reached",Toast.LENGTH_SHORT);
			toast.setDuration(500);
			toast.show();
		}
		// animate the scroll view when the user changes the tracks
		Animation anim = AnimationUtils
				.loadAnimation(this, R.anim.push_left_in);
		container.startAnimation(anim);
		changetrack();

	}

	public void previoustrack(View v) {
		if (!node.isFirst()) {
			node.moveToPrevious();
			// move the position of the tracklist page by 1, used while
			// returning to previous page.
			Baseclass.position--;
		} else if (node.isFirst()) {
			Toast toast = Toast.makeText(this, "First Phrase Reached",Toast.LENGTH_SHORT);
			toast.setDuration(500);
			toast.show();
		}
		// animate the scroll view when the user changes the tracks
		Animation anim = AnimationUtils.loadAnimation(this,
				R.anim.push_right_in);
		container.startAnimation(anim);
		changetrack();

	}

	@Override
	public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
		// TODO Auto-generated method stub
		// code to get the gestures from the view and identify the correct
		// gestures, get the predictions array and iterate through the array.
		ArrayList<Prediction> predictions = gestureLib.recognize(gesture);
		// Log.e("gesture", "Logged" + predictions.toString());
		for (Prediction prediction : predictions) {
			if (prediction.score > 1.0) {
				if (prediction.name.equalsIgnoreCase("baml")
						|| prediction.name.equalsIgnoreCase("left")) {
					// Log.e("gesture", "right");
					if (!node.isFirst()) {
						Baseclass.position--;
						node.moveToPrevious();
					} 
					else if (node.isFirst()) {
						Toast toast = Toast.makeText(this,
								"First Phrase Reached", Toast.LENGTH_SHORT);
						toast.setDuration(500);
						toast.show();
					}
					changetrack();
					Animation anim = AnimationUtils.loadAnimation(this,
							R.anim.push_right_in);
					container.startAnimation(anim);
				}

				else if (prediction.name.equalsIgnoreCase("bamr")
						|| prediction.name.equalsIgnoreCase("right")) {
					// Log.e("gesture", "left");
					if (!node.isLast()) {
						Baseclass.position++;
						node.moveToNext();
					} else if (node.isLast()) {
						Toast toast = Toast.makeText(this,
								"Last Phrase Reached", Toast.LENGTH_SHORT);
						toast.setDuration(500);
						toast.show();
					}
					changetrack();
					Animation anim = AnimationUtils.loadAnimation(this,
							R.anim.push_left_in);
					container.startAnimation(anim);
				}
			}
		}

	}

	public void viewscore(View V) {
		// Switch to stats view
		Intent i = new Intent(this, Leaderboard.class);
		i.putExtra("mode", "scoreboard");
		i.putExtra("NodeID", phraseid);
		i.putExtra("currentime", currenttime);
		i.putExtra("phrase", currentphrase);
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

	@SuppressLint("InlinedApi")
	@Override
	public void onInit(int status) {
		// TODO Auto-generated method stub
		// code to initislise the text to speech feature
		if (status == TextToSpeech.SUCCESS) {
			// set the speech language to English
			if (mTts.isLanguageAvailable(Locale.US) == TextToSpeech.LANG_AVAILABLE)
				mTts.setLanguage(Locale.US);
			HashMap<String, String> myHashAlarm = new HashMap<String, String>();
			myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_STREAM,String.valueOf(AudioManager.STREAM_MUSIC));
			myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_VOLUME,String.valueOf(0.5));
			// start the speech and flush the previous speech queue if already
			// playing the sound
			mTts.speak(currentphrase, TextToSpeech.QUEUE_FLUSH, myHashAlarm);
		} 
		else if (status == TextToSpeech.ERROR) {
			Toast.makeText(this, "Sorry! Text To Speech failed...",	Toast.LENGTH_LONG).show();
		}
	}

	public boolean getscores(int id) {
		// set the result view for track usng the stored data in attempts table
		TextView besttime = (TextView) findViewById(R.id.besttime);
		Cursor cur = cr.query(CONTENT_URI, projection,
				"select * from attempt where NodeID = " + id
						+ " ORDER BY msecs ASC", null, null);
		if (cur.getCount() > 0) {
			cur.moveToFirst();
			if (cur.getInt(cur.getColumnIndex("isgreen")) == 1) {
				besttime.setTextColor(Color.GREEN);
			} else if (cur.getInt(cur.getColumnIndex("isgreen")) == 0) {
				besttime.setTextColor(Color.YELLOW);
			} else {
				besttime.setTextColor(Color.RED);
			}
			besttime.setText("Your Best Time: "
					+ String.format(
							"%.2f",
							(float) cur.getInt(cur.getColumnIndex("msecs")) / 1000)
					+ " Sec");
			findViewById(R.id.viewscore).setVisibility(View.VISIBLE);
			findViewById(R.id.resultwindow).setVisibility(View.VISIBLE);
			besttime.setVisibility(View.VISIBLE);
			findViewById(R.id.resulttext).setVisibility(View.GONE);
			cur.close();
		} else {
			findViewById(R.id.viewscore).setVisibility(View.VISIBLE);
			findViewById(R.id.resultwindow).setVisibility(View.VISIBLE);
			findViewById(R.id.resulttext).setVisibility(View.GONE);
			besttime.setVisibility(View.GONE);
			if (!cur.isClosed())
				cur.close();
		}
		return true;

	}

	public boolean changetrack() {
		// attemptnumber= 0;
		issoundavailable = false;
		soundstream = new ByteArrayOutputStream();
		phraseid = node.getInt(node.getColumnIndex("_id"));
		currentphrase = node.getString(node.getColumnIndex("Phrase"));
		Baseclass.nodeid = nodeid;
		Baseclass.phraseid = phraseid;
		Baseclass.nodename = nodename;
		// Log.e("detailed phraseid",""+Baseclass.phraseid);
		// trim the extra space between the next line charecter and the phrase
		phrase.setText(currentphrase.trim());
		share.setTag(currentphrase.trim());
		// getspinner((Spinner)findViewById(R.id.topspinner),currentphrase,2);
		// Log.e("track data",""+node.getString(node.getColumnIndex("Phrase")));
		getscores(node.getInt(node.getColumnIndex("_id")));
		if (audioTrack != null
				&& audioTrack.getState() == AudioTrack.STATE_INITIALIZED) {
			audioTrack.stop();
			audioTrack.release();
		}
		// close the previous timer if the user navigates to a new track
		closetimer();
		replay.setText("Hint");
		return true;
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (sr != null)
			sr.destroy();
		if (mTts != null)
			mTts.shutdown();
		if (audioTrack != null
				&& audioTrack.getState() == AudioTrack.STATE_INITIALIZED) {
			audioTrack.stop();
			audioTrack.release();
		}
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);

	}

	@Override
	public void onResume() {
		super.onResume();
		// Log.e("transition",""+Baseclass.transition);
		if (!Baseclass.transition)
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
		else
			Baseclass.transition = false;
	}

	@Override
	public void onBeginningOfSpeech() {
		// TODO Auto-generated method stub
		// store the start time once the user starts recording
		Log.e("sound track", "speech start");
		start_time = System.currentTimeMillis();
	}

	@Override
	public void onBufferReceived(byte[] buffer) {
		// TODO Auto-generated method stub
		Log.e("sound track", "buffer received");
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
		Log.e("sound track", "speech end");
		end_time = System.currentTimeMillis();
		difference = end_time - start_time;
		Log.e("diff", "" + difference);
		tv.setText("Processing");
		dialog.show();
		processing = true;
		/** CountDownTimer */
		timer = new CountDownTimer(30000, 30000) {
			@Override
			public void onFinish() {
				try {
					if (dialog.isShowing()) {
						dialog.dismiss();
						findViewById(R.id.resultwindow).setVisibility(View.VISIBLE);
						TextView resulttext = (TextView) findViewById(R.id.resulttext);
						resulttext.setVisibility(View.VISIBLE);
						resulttext.setText("Processing time exceeded \nPlease try Again");
						findViewById(R.id.besttime).setVisibility(View.GONE);
						findViewById(R.id.viewscore).setVisibility(View.GONE);
						// close the speech recognizer if the timer run out
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
		// Log.e("Speech event",""+eventType + params.toString());
	}

	@Override
	public void onPartialResults(Bundle partialResults) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onReadyForSpeech(Bundle params) {
		// TODO Auto-generated method stub
		// show the listening dailog when the recognizer is ready for listening
		dialog.show();

	}

	@SuppressWarnings("unchecked")
	@Override
	public void onResults(Bundle results) {
		// TODO Auto-generated method stub
		// get the result from the speech recognizer
		// Log.d(TAG, "onResults " + results);
		if (results.isEmpty()&& results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).size() == 0) 
		{
			// Log.e("ASR" + TAG, "onResults empty" + results.size());
			dialog.dismiss();
		} else {
			// Log.e("ASR" + TAG, "results received"+
			// results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)+phraseid+"//"+difference);
			// send the data to the async class for further processing
			new Asynctask(this, phraseid, difference).execute(results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION));
			// close the listening dialog on receving the server results
			dialog.dismiss();
		}
		timer.cancel();
		replay.setText("Playback");

	}

	@Override
	public void onRmsChanged(float rmsdB) {
		// TODO Auto-generated method stub

	}

	public boolean isOnline() {
		// check if the device is connected to a network, cannot detect if the
		// device is connected to internet, only detect connection to a wireless
		// or 3G connection
		ConnectivityManager cm = (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);
		return cm.getActiveNetworkInfo() != null&& cm.getActiveNetworkInfo().isConnectedOrConnecting();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent e) {
		// dispatch the touch events for the getureview to other layers
		return super.dispatchTouchEvent(e);
	}

	public boolean closetimer() {
		// close timer if already inititalised
		if (timer != null) {
			timer.cancel();
			return true;
		}
		return false;

	}

	void recordsound(int action) {
		switch(action){
		case 0:
			break;
		case 1:
			
			break;
		}

	}
}
