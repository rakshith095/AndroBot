package com.andro.bot;

import java.io.IOException;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Menu extends ListActivity{

	String classes[] = {"Buttons","Accelerometer","Gesture"};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setListAdapter(new ArrayAdapter<String>(Menu.this, android.R.layout.simple_list_item_1, classes));	
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		try{
		@SuppressWarnings("rawtypes")
		Class myClass = Class.forName("com.andro.bot."+classes[position]);
		Intent myIntent = new Intent(Menu.this , myClass);
		startActivity(myIntent);
		}catch(ClassNotFoundException e){
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu);
   	    MenuInflater blowUp = getMenuInflater();
		blowUp.inflate(R.menu.popup_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){
		case R.id.connectDeviceButton:
			Intent myIntent = new Intent(this, Blue.class);
			startActivity(myIntent);
			break;
		case R.id.exitButton:
			if(BluetoothAdapter.getDefaultAdapter().isEnabled()){
			    if (Blue.outStream != null) {
			        try {
			          Blue.outStream.flush();
			        } catch (IOException e) {
			        	e.printStackTrace();
			        }
			      }

			      try     {
			        Blue.btSocket.close();
			      } catch (IOException e2) {
			    	  e2.printStackTrace();
			      }				
			}
			finish();
			break;
		}
		return false;
	}
}
