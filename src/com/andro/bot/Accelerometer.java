package com.andro.bot;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class Accelerometer extends Activity implements View.OnClickListener,SensorEventListener {

	Button led;
	TextView results;
	boolean ledPrevStatus = false;
	float sensorX,sensorY;
	SensorManager sm;
	WakeLock wL;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		PowerManager pM = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wL = pM.newWakeLock(PowerManager.FULL_WAKE_LOCK, "wakeLock");
		super.onCreate(savedInstanceState);
		wL.acquire();
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.accelerometer);
		results = (TextView) findViewById(R.id.accelerometerResults);
		led = (Button) findViewById(R.id.accelerometerLightButton);
		led.setOnClickListener(this);
		sensorX=sensorY=0;
		
		sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		if(sm.getSensorList(Sensor.TYPE_ACCELEROMETER).size() != 0 ){
			Sensor s = sm.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
			sm.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
		}
	}
		
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		sm.unregisterListener(this);
		wL.release();
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		wL.acquire();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		wL.release();
		super.onDestroy();
	}

	public void onClick(View v) {
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
	
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
	
		try {
			Thread.sleep(80);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sensorX = event.values[0];
		sensorY = event.values[1];
		
		if(sensorY <= -5.0){
			if(BluetoothAdapter.getDefaultAdapter().isEnabled())Blue.sendData("w");
			results.setText("Moving Forward");
			sensorX=sensorY=0;
		}else if(sensorY >= 6.0){
			if(BluetoothAdapter.getDefaultAdapter().isEnabled())Blue.sendData("x");
			results.setText("REVERSE");
			sensorX=sensorY=0;
		}else if(sensorX <= -6.5){
			if(BluetoothAdapter.getDefaultAdapter().isEnabled())Blue.sendData("d");
			results.setText("RIGHT");
			sensorX=sensorY=0;
		}else if(sensorX >= 6.5){
			if(BluetoothAdapter.getDefaultAdapter().isEnabled())Blue.sendData("a");
			results.setText("LEFT");
			sensorX=sensorY=0;
		}else{
			if(BluetoothAdapter.getDefaultAdapter().isEnabled())Blue.sendData("s");
			results.setText("STOPPED");
			sensorX=sensorY=0;
		}
	}

}
