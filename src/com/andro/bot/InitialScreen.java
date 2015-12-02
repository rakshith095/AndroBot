package com.andro.bot;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.app.Activity;
import android.content.Intent;

public class InitialScreen extends Activity {

	ProgressBar progress;
    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.initial_screen);
		progress = new ProgressBar(InitialScreen.this);
		progress.findViewById(R.id.initialProgressBar);
		Thread timer = new Thread(){	
			public void run(){
				try{
					progress.setVisibility(View.VISIBLE);
					sleep(2000);
				}catch(InterruptedException e){
					e.printStackTrace();
				}finally{
					Intent openMenu = new Intent("com.andro.bot.MENU");
					startActivity(openMenu);
					progress.setVisibility(View.INVISIBLE);
				}
			}
		};
		timer.start();
    }

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		finish();
	}

}
