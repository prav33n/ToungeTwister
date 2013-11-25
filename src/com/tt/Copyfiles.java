package com.tt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

@SuppressLint("SdCardPath")
public class Copyfiles {
	Context con;

	// copy the database to the sdcard or data folder of the application
	public boolean Copydatabase(Context con) {
		try {
			final String DB_NAME = "TT.sqlite";
			final String DB_PATH = "/data/data/"
					+ con.getApplicationContext().getPackageName()
					+ "/databases/";
			// Log.e("Database directory",""+con.getFilesDir().getPath()+"//"+DB_PATH+DB_NAME);
			File f = new File(DB_PATH + DB_NAME);
			if (f.exists()) {
				Log.e("DB exist", "tt db");
			} else {
				InputStream myinput = con.getAssets().open(DB_NAME);
				File dir = new File(DB_PATH);
				dir.mkdirs();
				OutputStream myoutput = new FileOutputStream(DB_PATH + DB_NAME);
				byte[] buffer = new byte[1024];
				int length;
				while ((length = myinput.read(buffer)) > 0) {
					myoutput.write(buffer, 0, length);
				}
				// Close the streams
				myoutput.flush();
				myoutput.close();
				myinput.close();
			}
			Log.e("DBcopy", "completed");
			return true;
		} catch (IOException e) {
			Log.e("DBcopy", "failed");
			return false;
		}
	}

	// can be used in future versions, not implemented completely
	public boolean copyaudio(String filename) {

		try {

			String filepath = "";
			Log.e("Database directory", filepath + filename);
			File f = new File(filepath + filename);
			if (f.exists()) {
				Log.e("file exists", "exists");
			} else {
				InputStream myinput = con.getAssets().open(filename);
				File dir = new File(filepath);
				dir.mkdirs();
				OutputStream myoutput = new FileOutputStream(filepath
						+ filename);
				byte[] buffer = new byte[1024];
				int length;
				while ((length = myinput.read(buffer)) > 0) {
					myoutput.write(buffer, 0, length);
				}
				// Close the streams
				myoutput.flush();
				myoutput.close();
				myinput.close();
			}
			Log.e("filecopy", "completed");
			return true;
		} catch (IOException e) {
			Log.e("DBcopy", "failed");
			return false;
		}

	}
}
