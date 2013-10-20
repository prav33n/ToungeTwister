package com.tt;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class Userverify extends AsyncTask<String, Integer, String> {
	Activity act;
	ContentResolver cr;
	View textEntryView;
	JSONObject json;
	
	public Userverify(Activity act) {
		this.act = act;
		cr = act.getContentResolver();
		LayoutInflater factory = LayoutInflater.from(act.getApplicationContext());
		textEntryView = factory.inflate(R.layout.introdialog,null);
		
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		Log.e(Baseclass.TAG, "" +Baseclass.userid);
		}

	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub

		AlertDialog.Builder alertbox = new AlertDialog.Builder(act);
		alertbox.setTitle("Please enter your Name");
		alertbox.setView(textEntryView);
		alertbox.setPositiveButton("Save", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				ContentValues cv = new ContentValues();
				EditText et = (EditText)textEntryView.findViewById(R.id.username);
				String username = et.getText().toString();
				int hashcode = username.hashCode();
				try{
					json = new JSONObject();
					json.put("UserID",hashcode);
					json.put("Name", username);
				}
				catch(JSONException e){e.printStackTrace();}
				Log.e("serverdata",""+Baseclass.streamtostring(Baseclass.httpclient("http://107.21.123.15/ttservices/updateuser.php",json)));
				
				/*cv.put("_id", 1);
				cv.put("UserID",hashcode);
				cv.put("Name", username);
				cv.put("Level",0);
				cv.put("Created", System.currentTimeMillis());
				cr.insert(Baseclass.CONTENT_URIUser, cv);
				Baseclass.userid =hashcode;
				Baseclass.name= username;*/
			}
		});
		alertbox.setCancelable(false);
		alertbox.show();
		return "success";
	}

}
