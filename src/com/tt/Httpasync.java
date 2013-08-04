package com.tt;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

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
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class Httpasync extends AsyncTask<String, Integer,String>{
	String result;
	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		try{
		StringEntity se;
		//Log.e("http string",URL+"//"+send.toString());
		HttpParams httpParameters = new BasicHttpParams();   //set connection parameters
		HttpConnectionParams.setConnectionTimeout(httpParameters, 60000);
		HttpConnectionParams.setSoTimeout(httpParameters, 60000);
		HttpClient httpclient = new DefaultHttpClient(httpParameters);
		HttpResponse response= null;
		HttpPost httppost = new HttpPost(params[0]);	
		httppost.setHeader("Content-type", "application/json");
		se = new StringEntity(params[1]);
		se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
		httppost.setEntity(se);
		response = httpclient.execute(httppost);
		HttpEntity entity = response.getEntity();
		InputStream is = entity.getContent();
		streamtostring(is);}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
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
			this.result = result;
			return result;
		}catch(Exception e){
			Log.e("log_tag", "Error converting result "+e.toString());
			e.printStackTrace();
			return null;
		}
    	
    }
}
