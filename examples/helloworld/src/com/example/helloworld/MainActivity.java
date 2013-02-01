package com.example.helloworld;

import com.appnexus.opensdk.AdView;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {
	private AdView av;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		RelativeLayout rl = (RelativeLayout)(findViewById(R.id.mainview));
		av = new AdView(this);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, 100);
		av.setAdHeight(50);
		av.setAdWidth(320);
		av.setLayoutParams(lp);
		av.setPlacementID("656561");
		rl.addView(av);
		av.loadAd();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public void loadAd(MenuItem mi){
		av.loadAd();
	}
}
