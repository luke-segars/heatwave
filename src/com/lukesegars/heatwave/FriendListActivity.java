package com.lukesegars.heatwave;

import java.util.ArrayList;
import java.util.Comparator;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.ListActivity;
import android.content.Intent;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class FriendListActivity extends ListActivity {
	private static final String TAG = "FriendListActivity";
	
	private Comparator<Contact> listSorter = new Comparator<Contact>() {
		public int compare(Contact first, Contact second) {
			if (first.getScore() < second.getScore()) return 1;
			else if (first.getScore() > second.getScore()) return -1;
			else return 0;
		}
    };
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storeObjectContext();
		registerForContextMenu(getListView());
        
        setContentView(R.layout.activity_friend_list);
        long startTime = System.currentTimeMillis();

        ArrayList<Contact> contacts = Contact.getAll();
        
        Log.i(TAG, "Contacts fetched after " +  (System.currentTimeMillis() - startTime) / 1000.0 + " seconds");
        ArrayList<String> names = new ArrayList<String>();
        for (Contact c : contacts) {
        	names.add(c.getName());
        }

        ContactArrayAdapter listAdapter = new ContactArrayAdapter(this,
        		R.layout.display_contact_row,
        		contacts);
        
        listAdapter.sort(listSorter);
        
        setListAdapter(listAdapter);
        listAdapter.notifyDataSetChanged();
        
        Log.i(TAG, "Loaded display in " + (System.currentTimeMillis() - startTime) / 1000.0 + " seconds");
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	
    	// Update the list in case anything has changed.
    	updateContactList();
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    }
    
    private void storeObjectContext() {
    	Wave.setContext(getApplicationContext());
    	Contact.setContext(getApplicationContext());
    }
    
    @Override
    public void onListItemClick(ListView lv, View v, int position, long id) {
    	ContactArrayAdapter adapter = (ContactArrayAdapter)getListAdapter();
		Contact c = adapter.getItem(position);
		
		String phoneNum = c.getPhoneNum();
	    Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNum));
	    	
	    startActivity(i);
    }
    
    // TODO: Necessary to clear all and then re-sort?  Would it be faster 
    public void updateContactList() {
    	ArrayList<Contact> contacts = Contact.getAll();
    	ContactArrayAdapter adapter = (ContactArrayAdapter) getListAdapter();
    	
    	// Remove all contacts and re-add them.
    	adapter.clear();
    	
    	for (Contact contact : contacts) {
    		adapter.add(contact);
    	}
    	
    	adapter.sort(listSorter);
    	adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_friend_list, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	// TODO: Add handlers for each of the menu options.
    	switch (item.getItemId()) {
    		case R.id.menu_contacts:
    			startActivity(
    				new Intent(this, SelectContactsActivity.class)
    			);
    			return true;
    		case R.id.menu_waves:
    			startActivity(
    				new Intent(this, DisplayWaveActivity.class)
    			);
    			return true;
    		default:
    			return super.onOptionsItemSelected(item);
    	}
    }
}
