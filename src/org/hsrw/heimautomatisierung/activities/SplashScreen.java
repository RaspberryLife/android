package org.hsrw.heimautomatisierung.activities;

import org.hsrw.heimautomatisierung.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

/* @ Marco Pleines - Hochschule Rhein-Waal */

/*
 * The Splash screen is supposed to last as long as the data needs to be loaded (only if the user wants to be logged in automatically)
 */

public class SplashScreen extends Activity{
    private static final int SPLASH_DISPLAY_LENGTH = 3000;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//set up notitle 
        requestWindowFeature(Window.FEATURE_NO_TITLE);  
		
		setContentView(R.layout.acitivty_splashscreen);
		
		new Handler().postDelayed(new Runnable(){
			@Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(SplashScreen.this, SelectActivity.class);
                startActivity(i);
 
                // close this activity
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH );
	}
}