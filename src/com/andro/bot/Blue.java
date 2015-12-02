package com.andro.bot;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Blue extends Activity implements OnItemClickListener{

	ListView listView;
	ArrayAdapter<String> listAdapter;
	Set<BluetoothDevice> devicesArray;
	boolean present = false;

	private static final int REQUEST_ENABLE_BT = 1;
	static BluetoothAdapter btAdapter;
    static BluetoothSocket btSocket = null;
	static OutputStream outStream = null;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private static String deviceAddress = "00:00:00:00:00:00";

	private final BroadcastReceiver devicesReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				present = false;
				for(int i=0 ; i<listAdapter.getCount();i++){
					if(listAdapter.getItem(i).contains(device.getName() + "\n" + device.getAddress()))
						present = true;
				}
				if(present == false){
					listAdapter.add(device.getName() + "\n" + device.getAddress());
					listAdapter.notifyDataSetChanged();
				}
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.device_list);
		initializeEverything();
		
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		if(btAdapter == null){
        	Toast.makeText(getApplicationContext(), "No bluetooth detected", Toast.LENGTH_LONG).show();
        	finish();
        }else{
        	if(! btAdapter.isEnabled()){
        		turnOnBluetooth();
        	}else{
        		getDevices();
        	}
        }
	}

	private void initializeEverything() {
		// TODO Auto-generated method stub
		
		listView = (ListView) findViewById(R.id.lvDevices);
		listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,0);
		listView.setAdapter(listAdapter);
		listView.setOnItemClickListener(this);

	}

	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		if(btAdapter.isDiscovering()){
			btAdapter.cancelDiscovery();
		}
		String info =  ((TextView)view).getText().toString();
		deviceAddress = info.substring(info.length() - 17);
		
	    BluetoothDevice device = btAdapter.getRemoteDevice(deviceAddress);
	    
	    try {
	      btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
	  
	    try {
	      btSocket.connect();
			Toast.makeText(getApplicationContext(), "Connection established", Toast.LENGTH_SHORT).show();
	    } catch (IOException e) {
	      try {
	        btSocket.close();
	      } catch (IOException e2) {
	  		Toast.makeText(getApplicationContext(), "Unable to connect", Toast.LENGTH_SHORT).show();
	      }
	    }
	    
	    try {
	      outStream = btSocket.getOutputStream();
	      /*ConnectedThread cT = new ConnectedThread(btSocket);
	      cT.start();
	      Toast.makeText(getApplicationContext(), "Successfully started", Toast.LENGTH_SHORT).show();*/
	    } catch (IOException e) {
			Toast.makeText(getApplicationContext(), "outputstream creation failed", Toast.LENGTH_SHORT).show();
	    }
		finish();
	}
	
	private void turnOnBluetooth() {
		// TODO Auto-generated method stub
	    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK){
			Toast.makeText(getApplicationContext(), "Bluetooth turned on", Toast.LENGTH_SHORT).show();
			getDevices();
		}
		else if(resultCode == RESULT_CANCELED){
			Toast.makeText(getApplicationContext(), "Bluetooth must be turned on to use the App", Toast.LENGTH_SHORT).show();
			finish();
		}
	}

	private void getDevices() {
		// TODO Auto-generated method stub
		devicesArray = btAdapter.getBondedDevices();
		if (devicesArray.size() > 0) {
		    for (BluetoothDevice device : devicesArray) {
		        listAdapter.add(device.getName() + "\n" + device.getAddress());
		    }
		    listAdapter.notifyDataSetChanged();
		}
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(devicesReceiver, filter);
		btAdapter.startDiscovery();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		if (btAdapter != null) {
			btAdapter.cancelDiscovery();
		}

		try {
			unregisterReceiver(devicesReceiver);
		} catch (Exception e) {
			e.getStackTrace();
		}
	}
	
	public static void sendData(String message) {

		byte[] msgBuffer = message.getBytes();
		    try {
		      outStream.write(msgBuffer);
		    } catch (IOException e) {
		    	e.printStackTrace();
		    }
		  }	
	
}
