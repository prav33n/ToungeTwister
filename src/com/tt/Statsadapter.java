/*package com.tt;

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

class Statsadapter extends BaseAdapter implements ListAdapter {

    private final Activity activity;
    private final JSONArray jsonArray;
    public Statsadapter (Activity activity, JSONArray jsonArray) {
        assert activity != null;
        assert jsonArray != null;
        this.jsonArray = jsonArray;
        this.activity = activity;
    }


    @Override 
    public int getCount() {
    	//Log.e("json array",""+jsonArray.length());
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
        convertView.findViewById(R.id.previouslevel).setVisibility(View.GONE);
        convertView.findViewById(R.id.nextlevel).setVisibility(View.GONE);
        convertView.findViewById(R.id.levelprogress).setVisibility(View.GONE);
        ImageView img = (ImageView)convertView.findViewById(R.id.avatar);
        if(position ==0){
        	img.setImageResource(R.drawable.img1);
        }
        else if(position ==1){
        	img.setImageResource(R.drawable.img2);
        }
        else if(position ==2){
        	img.setImageResource(R.drawable.img3);
        }
        else {
        	img.setImageResource(R.drawable.img4);
        }
                    JSONObject json_data = getItem(position);  
                    int score,attemptnumber,totalgreentime,userid;
                    String name;
					try {
						name = json_data.getString("name");
						score= json_data.getInt("Score");
						attemptnumber = json_data.getInt("AttemptNumber");
						totalgreentime = json_data.getInt("totalgreentime");
						userid = json_data.getInt("UserID");
				        text.setText(name+"\nTotal Attempts :"+attemptnumber+"\nTime :"+(float)totalgreentime/1000+"S");
				       // previouslevel.setText(""+level);
				        //nextlevel.setText(""+(level+1));
				        //levelprogress.setProgress(score%1000);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						userid=0;
					}
					if(userid== Baseclass.userid)
						convertView.setBackgroundResource(R.color.mild_blue);
           return convertView;
    }


	
}*/

/**
 * 
 */
package com.tt;

import com.tt.R;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

/**
 * @author praveen jelish
 * 
 */
public class Statsadapter extends SimpleCursorAdapter implements ListAdapter {

	Context con;
	LayoutInflater inflator;

	public Statsadapter(Context context, int layout, Cursor c, String[] from,
			int[] to, int flags) {
		super(context, layout, c, from, to, flags);
		this.con = context;
		inflator = LayoutInflater.from(context);
		// TODO Auto-generated constructor stub
	}

	/**
	 * adapter for the TT list in home screen
	 */

	@Override
	public void bindView(View v, Context context, Cursor cur) {

		// TODO set values to the list view element using cursor data
		TextView text = (TextView) v.findViewById(R.id.scoredetails);
		v.findViewById(R.id.previouslevel).setVisibility(View.GONE);
		v.findViewById(R.id.nextlevel).setVisibility(View.GONE);
		v.findViewById(R.id.levelprogress).setVisibility(View.GONE);
		ImageView img = (ImageView) v.findViewById(R.id.avatar);
		if (cur.getPosition() == 0) {
			img.setImageResource(R.drawable.img1);
		} else if (cur.getPosition() == 1) {
			img.setImageResource(R.drawable.img2);
		} else if (cur.getPosition() == 2) {
			img.setImageResource(R.drawable.img3);
		} else if (cur.getPosition() == 3) {
			img.setImageResource(R.drawable.img4);
		} else if (cur.getPosition() == 4) {
			img.setImageResource(R.drawable.img5);
		} else if (cur.getPosition() == 5) {
			img.setImageResource(R.drawable.btn_check_holo_dark);
		}

		int totaltime, id = 0;
		String name;
		id = cur.getInt(cur.getColumnIndex("UserID"));
		if (cur.getPosition() < 5) {
			name = cur.getString(cur.getColumnIndex("Name"));
			totaltime = cur.getInt(cur.getColumnIndex("msecs"));
			text.setText(name + "\nBest Time: "
					+ String.format("%.2f", (float) totaltime / 1000) + " Secs");

			if (id == Baseclass.userid) {
				v.setBackgroundResource(R.color.mild_blue);
			} else
				v.setBackgroundResource(R.color.transparent);
		} else if (id == Baseclass.userid && cur.getPosition() == 5) {
			name = cur.getString(cur.getColumnIndex("Name"));
			totaltime = cur.getInt(cur.getColumnIndex("msecs"));
			text.setText(name + "\nBest Time: "
					+ String.format("%.2f", (float) totaltime / 1000) + " Secs");
			v.setBackgroundResource(R.color.mild_blue);
		}
		if (text.getText().equals("Score view")) {
			text.setVisibility(View.INVISIBLE);
			img.setVisibility(View.INVISIBLE);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.widget.CursorAdapter#newView(android.content.Context,
	 * android.database.Cursor, android.view.ViewGroup)
	 */
	@Override
	public View newView(Context context, Cursor cur, ViewGroup parent) {
		// TODO return the view to inflate
		View v = inflator.inflate(R.layout.scoreview, parent, false);
		bindView(v, context, cur);
		return v;
	}
}
