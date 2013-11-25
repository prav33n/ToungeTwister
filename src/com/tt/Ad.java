package com.tt;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.widget.LinearLayout;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

@SuppressLint("Registered")
public class Ad extends Activity {
	private AdView adView;
	String banid = "ca-app-pub-9339984839702541/3888813714";

	public Ad(LinearLayout ads, Activity act) {
		// Create an ad
		adView = new AdView(act, AdSize.BANNER, banid);
		// Add the AdView to the view hierarchy. The view will have no size
		// until the ad is loaded.
		Log.e("ads", "" + ads.getId());
		ads.addView(adView);
		AdRequest adRequest = new AdRequest();
		adView.loadAd(adRequest);
	}

}
