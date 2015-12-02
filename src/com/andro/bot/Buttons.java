package com.andro.bot;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class Buttons extends Activity implements View.OnClickListener{
	
	Button start,led;
	ImageButton reverse,left,right;
	TextView results;
	String status = "STOPPED";
	boolean prev = false,rev=false,ledPrevStatus = false;
	ConnectedThread cT;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		if(savedInstanceState != null){
			prev = savedInstanceState.getBoolean("prev");
			ledPrevStatus = savedInstanceState.getBoolean("ledPrevStatus");
			rev = savedInstanceState.getBoolean("rev");
			status = savedInstanceState.getString("status");
		}
		setContentView(R.layout.buttons);
		start = (Button)findViewById(R.id.startButton);
		reverse = (ImageButton) findViewById(R.id.reverseButton);
		left = (ImageButton) findViewById(R.id.leftButton);
		right = (ImageButton) findViewById(R.id.rightButton);
		results = (TextView) findViewById(R.id.buttonsResults);
		led = (Button) findViewById(R.id.lightButton);
		start.setOnClickListener(this);
		reverse.setOnClickListener(this);
		left.setOnClickListener(this);
		right.setOnClickListener(this);
		led.setOnClickListener(this);
		if(BluetoothAdapter.getDefaultAdapter().isEnabled()){
			cT = new ConnectedThread(Blue.btSocket);
			cT.start();
			}
		setPreviousStatus();
	}
	
	private void setPreviousStatus() {
		// TODO Auto-generated method stub
		if(rev == true){
			results.setText("REVERSE");			
			start.setBackgroundResource(R.drawable.stop_button);
		}else if(status.contentEquals("STOPPED"))
			results.setText(status);
		else{
			results.postDelayed(new Runnable(){public void run(){results.setText("MOVING FORWARD");}}, 800);
			results.setText(status);
			start.setBackgroundResource(R.drawable.stop_button);
		}
		if( ledPrevStatus == true)
			led.setBackgroundResource(R.drawable.lightbulb_on);
		else if(ledPrevStatus == false)
			led.setBackgroundResource(R.drawable.lightbulb_off);
		
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		switch(v.getId()){
		
		case R.id.startButton:
			if( prev == false){
				start.setBackgroundResource(R.drawable.stop_button);
				prev = true;
				rev = false;
				if(BluetoothAdapter.getDefaultAdapter().isEnabled())Blue.sendData("w");				
				results.postDelayed(new Runnable(){public void run(){results.setText("MOVING FORWARD");}}, 800);
				results.setText("STARTED");
			}else if(prev == true){
				start.setBackgroundResource(R.drawable.start_button);
				results.setText("STOPPED");
				prev = false;
				rev = false;
				if(BluetoothAdapter.getDefaultAdapter().isEnabled())Blue.sendData("s");
			}
			break;
		case R.id.leftButton:
			start.setBackgroundResource(R.drawable.stop_button);
			prev = true;
			rev = false;
			if(BluetoothAdapter.getDefaultAdapter().isEnabled())Blue.sendData("a");
			results.postDelayed(new Runnable(){public void run(){results.setText("MOVING FORWARD");}}, 200);
			results.setText("LEFT");
			break;
		case R.id.rightButton:
			start.setBackgroundResource(R.drawable.stop_button);
			prev = true;
			rev = false;
			if(BluetoothAdapter.getDefaultAdapter().isEnabled())Blue.sendData("d");
			results.postDelayed(new Runnable(){public void run(){results.setText("MOVING FORWARD");}}, 200);
			results.setText("RIGHT");
			break;
		case R.id.reverseButton:
			results.setText("REVERSE");
			start.setBackgroundResource(R.drawable.stop_button);
			prev = true;
			rev = true;
			if(BluetoothAdapter.getDefaultAdapter().isEnabled())Blue.sendData("x");
			break;
		case R.id.lightButton:
			if( ledPrevStatus == false){
				led.setBackgroundResource(R.drawable.lightbulb_on);
				ledPrevStatus = true;
				if(BluetoothAdapter.getDefaultAdapter().isEnabled())Blue.sendData("l");
			}else if(ledPrevStatus == true){
				led.setBackgroundResource(R.drawable.lightbulb_off);
				ledPrevStatus = false;
				if(BluetoothAdapter.getDefaultAdapter().isEnabled())Blue.sendData("o");
			}
			break;
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putBoolean("prev", prev);
		outState.putBoolean("ledPrevStatus", ledPrevStatus);
		outState.putBoolean("rev", rev);
		outState.putString("status", results.getText().toString());
	}
	
	private class ConnectedThread extends Thread {
	    @SuppressWarnings("unused")
		private final BluetoothSocket mmSocket;
	    private final InputStream mmInStream;
	 
	    public ConnectedThread(BluetoothSocket socket) {
	        mmSocket = socket;
            InputStream tmpIn = null;
	        try {
				tmpIn = socket.getInputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mmInStream = tmpIn;
	    }
	    
	    public void run() {
	        byte[] buffer = new byte[1024];  // buffer store for the stream
	         
	        // Keep listening to the InputStream until an exception occurs
	        while (true) {
	            try {
	                // Read from the InputStream
	                mmInStream.read(buffer);
	                if(buffer.toString().contains("h"));{
	                	 runOnUiThread (new Thread(new Runnable(){public void run() {
	         				start.setBackgroundResource(R.drawable.start_button);
	        				results.setText("Obstacle Detected");
	        				prev = false;
	        				rev = false;	                		 
	                	 }}));
	                }	                	               
	            }catch (IOException e) {break;}
	        }
	    }
	    
    }

}