package com.kumar.pikanshu.autoredial;

import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class ContactSelectionActivity extends AppCompatActivity {
	private ArrayList<Contact> contacts = new ArrayList<Contact>();
	private SharedPreferences contactSelectionStatus;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_selection);
//		outputText = (TextView) findViewById(R.id.textView1);

		contactSelectionStatus= PreferenceManager.getDefaultSharedPreferences(this);



		fetchContacts(contacts);

		ContactAdapter contactArrayAdapter = new ContactAdapter(this,contacts);

		final ListView contactListView = (ListView) findViewById(R.id.contactList);

		contactListView.setAdapter(contactArrayAdapter);


		Button done = (Button) findViewById(R.id.action_done);

		done.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onBackPressed();
			}
		});

		Button selectAll = (Button) findViewById(R.id.action_selectAll);

		selectAll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				for(int i = 0; i < contacts.size(); i++) {
					contacts.get(i).setSelected(true);

				}
				contactListView.invalidateViews();
			}
		});

		Button deSelectAll = (Button) findViewById(R.id.action_deSelectAll);

		deSelectAll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				for(int i = 0; i < contacts.size(); i++) {
					contacts.get(i).setSelected(false);

				}
				contactListView.invalidateViews();
			}
		});


	}

	@Override
	public void onBackPressed() {
		SharedPreferences.Editor editor = contactSelectionStatus.edit();

		for(int i = 0; i < contacts.size(); i++) {
			editor.putBoolean(contacts.get(i).getPhoneNumber(),contacts.get(i).isSelected());
		}
		// Commit the edits!
		editor.commit();
		super.onBackPressed();
	}

	public void fetchContacts(ArrayList<Contact> contacts) {
		Contact tempContact;

		String phoneNumber = null;
		String email = null;
		
		Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
		String _ID = ContactsContract.Contacts._ID;
		String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
		String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
		
		Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
		String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
		
		/*Uri EmailCONTENT_URI =  ContactsContract.CommonDataKinds.Email.CONTENT_URI;
		String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
		String DATA = ContactsContract.CommonDataKinds.Email.DATA;*/
		
		ContentResolver contentResolver = getContentResolver();
		
		Cursor cursor = contentResolver.query(CONTENT_URI, null,null, null, ContactsContract.Contacts.SORT_KEY_PRIMARY + " ASC");
		
		// Loop for every contact in the phone
		if (cursor.getCount() > 0) {
			
			while (cursor.moveToNext()) {
				
				String contact_id = cursor.getString(cursor.getColumnIndex( _ID ));
				String name = cursor.getString(cursor.getColumnIndex( DISPLAY_NAME ));

				int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex( HAS_PHONE_NUMBER )));
				
				if (hasPhoneNumber > 0) {
					

					// Query and loop for every phone number of the contact
					Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[] { contact_id }, null);
					
					while (phoneCursor.moveToNext()) {

						phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));

						String normalizedNumber = phoneNumber.replaceAll(" ","").replaceAll("-", "");

						if(normalizedNumber.length() >= 10){
							String tempPhoneNumber = normalizedNumber.substring((normalizedNumber.length()-10),normalizedNumber.length());
							tempContact = new Contact(name,tempPhoneNumber);

							if(!contacts.contains(tempContact)){
								boolean selectionStatus = contactSelectionStatus.getBoolean(tempPhoneNumber,false);
								tempContact.setSelected(selectionStatus);
								contacts.add(tempContact);
							}
						}
						
					
					}
					
					phoneCursor.close();

					/*// Query and loop for every email of the contact
					Cursor emailCursor = contentResolver.query(EmailCONTENT_URI,	null, EmailCONTACT_ID+ " = ?", new String[] { contact_id }, null);
					
					while (emailCursor.moveToNext()) {
					
						email = emailCursor.getString(emailCursor.getColumnIndex(DATA));
						
						output.append("\nEmail:" + email);
						

					}

					emailCursor.close();*/
				}


			}
		}
	}

}