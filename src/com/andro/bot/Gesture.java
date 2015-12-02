package com.andro.bot;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Prediction;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class Gesture extends Activity implements OnGesturePerformedListener,View.OnClickListener {

	GestureLibrary mLibrary;
	Button led;
	TextView results;
	boolean ledPrevStatus = false;
	ConnectedThread cT;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.gesture);
		mLibrary = GestureLibraries.fromRawResource(this, R.raw.gestures);
		if (!mLibrary.load()) {
			finish();
		}
		GestureOverlayView gestures = (GestureOverlayView) findViewById(R.id.gestures);
		gestures.addOnGesturePerformedListener(this);
		results = (TextView) findViewById(R.id.gestureResults);
		led = (Button) findViewById(R.id.gestureLightButton);
		led.setOnClickListener(this);
		if(BluetoothAdapter.getDefaultAdapter().isEnabled()){
			cT = new ConnectedThread(Blue.btSocket);
			cT.start();
			}
	}

	public void onGesturePerformed(GestureOverlayView overlay,android.gesture.Gesture gesture) {
		// TODO Auto-generated method stub
		ArrayList<Prediction> predictions = mLibrary.recognize(gesture);

		if (predictions.size() > 0 && predictions.get(0).score > 4.0) {
			String result = predictions.get(0).name;

			if ("straight".equalsIgnoreCase(result)) {
				if(BluetoothAdapter.getDefaultAdapter().isEnabled())Blue.sendData("w");
				results.setText("MOVING FORWARD");
			} else if ("stop".equalsIgnoreCase(result)) {
				if(BluetoothAdapter.getDefaultAdapter().isEnabled())Blue.sendData("s");
				results.setText("STOPPED");
			} else if ("reverse".equalsIgnoreCase(result)) {
				if(BluetoothAdapter.getDefaultAdapter().isEnabled())Blue.sendData("x");
				results.setText("REVERSE");
			} else if ("right".equalsIgnoreCase(result)) {
				if(BluetoothAdapter.getDefaultAdapter().isEnabled())Blue.sendData("d");
				results.postDelayed(new Runnable(){public void run(){results.setText("MOVING FORWARD");}}, 300);
				results.setText("RIGHT");
			} else if ("left".equalsIgnoreCase(result)) {
				if(BluetoothAdapter.getDefaultAdapter().isEnabled())Blue.sendData("a");
				results.postDelayed(new Runnable(){public void run(){results.setText("MOVING FORWARD");}}, 300);
				results.setText("LEFT");
			}
		}
	}

	public void onClick(View view) {
		// TODO Auto-generated method stub
		if( ledPrevStatus == false){
			led.setBackgroundResource(R.drawable.lightbulb_on);
			ledPrevStatus = true;
			if(BluetoothAdapter.getDefaultAdapter().isEnabled())Blue.sendData("l");
		}else if(ledPrevStatus == true){
			led.setBackgroundResource(R.drawable.lightbulb_off);
			ledPrevStatus = false;
			if(BluetoothAdapter.getDefaultAdapter().isEnabled())Blue.sendData("o");
		}
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
	        				results.setText("Obstacle Detected");
	                	 }}));
	                }	                	               
	            }catch (IOException e) {break;}
	        }
	    }
	    
    }
}
