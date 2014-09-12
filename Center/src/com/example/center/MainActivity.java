package com.example.center;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

public class MainActivity extends Activity {

/*	private void tryHidIcon(){
		getPackageManager().setComponentEnabledSetting(getComponentName(), 2, 1);
	}*/
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = new Intent(this, MyService.class);
		startService(intent);
		//tryHidIcon();
		finish();
	}
}
