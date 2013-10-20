package com.tt;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.string;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

class Scorecalc extends BaseAdapter implements ListAdapter {

    private final Activity activity;
    private final JSONArray jsonArray;
    public Scorecalc (Activity activity, JSONArray jsonArray) {
       // assert activity != null;
     //   assert jsonArray != null;
        this.jsonArray = jsonArray;
        this.activity = activity;
    }


    @Override 
    public int getCount() {
        if(jsonArray==null) 
         return 0;
        else
        return jsonArray.length();
    }

    @Override 
    public JSONObject getItem(int position) {
    	Log.e("json array position",""+position);
         if(jsonArray==null) return null;
         else
           return jsonArray.optJSONObject(position);
    }

    @Override 
    public long getItemId(int position) {
        //JSONObject jsonObject = getItem(position);
        return position;
    }

    @Override 
    public View getView(int position, View convertView, ViewGroup parent) {
    	
    	  LayoutInflater inflator = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
          convertView = inflator.inflate(R.layout.scoreview, parent,false);
        if (convertView == null){
      //  LayoutInflater inflator = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //convertView = inflator.inflate(R.layout.scoreview, parent,false);
        }
        TextView text =(TextView)convertView.findViewById(R.id.scoredetails);
        TextView previouslevel = (TextView)convertView.findViewById(R.id.previouslevel);
        TextView nextlevel = (TextView)convertView.findViewById(R.id.nextlevel);
        ProgressBar levelprogress = (ProgressBar)convertView.findViewById(R.id.levelprogress);
        ImageView img = (ImageView)convertView.findViewById(R.id.avatar);
        if(position ==0){
        	img.setImageResource(R.drawable.img1);
        }
        else if(position ==1){
        	img.setImageResource(R.drawable.img2);
        }
        else if(position == 2){
        	img.setImageResource(R.drawable.img3);
        }
        else {
        	img.setImageResource(R.drawable.img4);
        }
                    JSONObject json_data = getItem(position);  
                    int score,level,tt,userid;
                    String name;
					try {
						userid = json_data.getInt("UserID");
						name = json_data.getString("Name");
						score = json_data.getInt("Score");
						level = (int)score/1000;
						tt = json_data.getInt("Level");
				        text.setText(name+"\nDistinct TTs :"+tt);
				        previouslevel.setText("Level "+level);
				        nextlevel.setText("Level "+(level+1));
				        levelprogress.setProgress(score%1000);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						userid=0;
					}
					
					if(userid== Baseclass.userid)
						convertView.setBackgroundResource(R.color.mild_blue);
           return convertView;
    }


	
}