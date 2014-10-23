package org.hsrw.heimautomatisierung.activities;

import org.hsrw.heimautomatisierung.R;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SelectActivity extends ListActivity{
    
	// selectable activities
    private Object[] activities={
            Monitoring.class,
            SplashScreen.class,
            ProtobufTest.class,
            AlarmClock.class,
    };
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectactivity);

        ListView lv = this.getListView();
        lv.setAdapter(new ArrayAdapter<String>(
                this, 
                android.R.layout.simple_list_item_1, 
                getStringArray()));
    }
    
    // parse activity objects
    private String[] getStringArray() {
        String[] activityNames = new String[activities.length];
        for(int i=0; i < activities.length; i++) {
            activityNames[i] = ((Class<?>) activities[i]).getSimpleName();
        }
        return activityNames;
    }

    // click logics
    public void onListItemClick(ListView parent, View v, int position, long id) {
        if (activities[position] != null) {

            try {

                Intent myIntent = new Intent(SelectActivity.this,(Class<?>) activities[position]);

                Bundle b = new Bundle();
                b.putString("class", ((Class<?>) activities[position]).getName());
                myIntent.putExtras(b);
                startActivityForResult(myIntent, 1);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Intent myIntent = new Intent(SelectActivity.this, (Class<?>) activities[position]);
            this.startActivity(myIntent);
        }
    }
}