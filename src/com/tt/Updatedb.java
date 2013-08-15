/**
 * 
 */
package com.tt;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.R.integer;
import android.R.string;
import android.os.AsyncTask;
import android.util.Log;

/**
 * @author Raven
 *
 */
public class Updatedb extends AsyncTask<string, integer, string> {

	@Override
	protected string doInBackground(string... params) {
		// TODO Auto-generated method stub
		string result = params[0];
		try{
			Baseclass.httpclient("result", null);
		}
		catch (Exception e){
			
		}
		return result;
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
