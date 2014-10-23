package org.hsrw.heimautomatisierung.activities;

import android.view.View;
import org.hsrw.heimautomatisierung.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class AlarmClock extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		
		setContentView(R.layout.activity_alarmclock);
		
		// Switch Widget reference
		Switch cSwitch = (Switch) findViewById(R.id.switch1);
		// TextView Reference
		TextView alarmTextView = (TextView) findViewById(R.id.alarmText);
		
		// read next alarm
		String readAlarm = android.provider.Settings.System.getString(getContentResolver(), android.provider.Settings.System.NEXT_ALARM_FORMATTED);
		String alarmText = "";
		
		// check if an alarm has been set
		if(readAlarm.isEmpty()){
			alarmText= "Keine Weckzeit eingestellt! Bitte aktiviere den Wecker, damit die Funktion zum Kochen von Kaffee genutzt werden kann!";
			// switch is supposed to be displayed only on a set alarm
			cSwitch.setVisibility(View.INVISIBLE);
		} else {
			alarmText = "Nächste Weckzeit: " + readAlarm;
		}
		
		// assign text
		alarmTextView.setText(alarmText);
		
		
		// TODO ask the server about the current state of cooking coffee
		// observer the switch and post toasts on changed
		cSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			// TODO send Message to Server
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		        if(isChecked){
		        	Toast.makeText(AlarmClock.this, "Kaffee wird zur nächsten Weckzeit gekocht!", Toast.LENGTH_SHORT).show();
		        } else if (!isChecked){
		        	Toast.makeText(AlarmClock.this, "Zur nächsten Weckzeit gibt's keinen Kaffee.", Toast.LENGTH_SHORT).show();
		        }
		    }
		});
	}
}
