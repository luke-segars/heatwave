package com.lukesegars.heatwave;

import java.util.ArrayList;

import android.app.ListActivity;
import android.app.SearchManager;
import android.app.DownloadManager.Query;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;


public class SelectContactsActivity extends ListActivity {
	private static final String TAG = "SelectContactsActivity";
	
	private HeatwaveDatabase database;

	private ArrayList<String> contactNames;
	private ArrayList<Integer> contactIds;
	
	@Override
	protected void onCreate(Bundle saved) {
		super.onCreate(saved);
		setContentView(R.layout.activity_select_contacts);
		
		getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		// Turn on type-to-search.
		setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);
		
		Button sc_btn = (Button)findViewById(R.id.save_contacts_btn);
		sc_btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				SparseBooleanArray arr = getListView().getCheckedItemPositions();
				
				ArrayList<Integer> actives = new ArrayList<Integer>();
				ArrayList<Integer> inactives = new ArrayList<Integer>();

				for (int i = 0; i < arr.size(); i++) {
					int itemId = arr.keyAt(i);
					if (arr.valueAt(i)) actives.add(contactIds.get(itemId));
					else inactives.add(contactIds.get(itemId));
				}
				updateContacts(actives, inactives);
				finish();
			}
		});
		
		Button cc_btn = (Button)findViewById(R.id.cancel_contacts_btn);
		cc_btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		
		database = HeatwaveDatabase.getInstance(this);
		
		contactNames = new ArrayList<String>();
		contactIds = new ArrayList<Integer>();
		
		loadAdrContacts(getIntent());
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		loadAdrContacts(intent);
	}
	
	// TODO: Move this to database layer.
	private void loadAdrContacts(Intent intent) {
		contactIds.clear();
		contactNames.clear();
		
		String searchQuery = null;
		if (intent.ACTION_SEARCH.equals(intent.getAction())) {
			searchQuery = intent.getStringExtra(SearchManager.QUERY);
		}
		
		if (searchQuery != null) Log.i(TAG, "Searching for " + searchQuery);
		else Log.i(TAG, "No search query provided.");
		
		Uri uri = (searchQuery == null) ? ContactsContract.Contacts.CONTENT_URI :
			Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_FILTER_URI, searchQuery);
		
		String[] projection = new String[] {
			ContactsContract.Contacts._ID,
			ContactsContract.Contacts.DISPLAY_NAME
		};

		Cursor cursor = getContentResolver().query(uri, 
			projection, 
			ContactsContract.Contacts.HAS_PHONE_NUMBER + " = 1", 
			null, 
			ContactsContract.Contacts.DISPLAY_NAME + " ASC");
		
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			contactIds.add(cursor.getInt(0));
			contactNames.add(cursor.getString(1));
			
			cursor.moveToNext();
		}
		cursor.close();

		ListView lv = getListView();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
			android.R.layout.simple_list_item_multiple_choice, 
			contactNames);
		lv.setAdapter(adapter);
		
		ArrayList<Integer> actives = database.getActiveContactAdrIds();
		
		// TODO: Optimize this.
		// For each active ID, check to see if the contact ID
		for (int i = 0; i < contactIds.size(); i++) {
			if (actives.contains(contactIds.get(i))) lv.setItemChecked(i, true);
		}
		
		adapter.notifyDataSetChanged();
	}

	private void updateContacts(ArrayList<Integer> actives, ArrayList<Integer> inactives) {
		// 1. Create a new Contact for each ID.  create() will not create
		//    a record if one already exists for the user with this ID.
		for (Integer id : actives) Contact.create(id, null);
		
		// 2. Delete all of the Contacts that are no longer on the list.
		for (Integer id : inactives) Contact.delete(id);
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_select_contacts, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    		case R.id.menu_search_contacts:
//    			startActivity(
//    				new Intent(this, SelectContactsActivity.class)
//    			);
    			onSearchRequested();
    			return true;
    		default:
    			return super.onOptionsItemSelected(item);
    	}
    }
	
	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onStop() {
		super.onStop();
	}
}
