package com.example.pikanshu.autoredial;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.Spinner;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    static public final int MAX_REDIAL_ATTEMPT = 50; // Maximum number of redials supported by application
    static public final int MAX_REDIAL_DELAY = 10; // Maximum delay( in seconds)  supported by application
    static public final int MAX_offhookThreshold= 50; // Maximum threshold( in seconds) for detecting call not connected supported by application

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPref= PreferenceManager.getDefaultSharedPreferences(this);
        ServiceReceiver.redialFlag = sharedPref.getBoolean("redialFlag",false);
        ServiceReceiver.REDIAL_ATTEMPT = sharedPref.getInt("REDIAL_ATTEMPT",0);
        ServiceReceiver.redialPauseLength = sharedPref.getLong("redialPauseLength",3000);
        ServiceReceiver.Outgoing_OffHook_Time_Threshold = sharedPref.getLong("Outgoing_OffHook_Time_Threshold",5000);

        final CheckedTextView ctv = (CheckedTextView) findViewById(R.id.checkedTextView1);
        if(ServiceReceiver.redialFlag == false) ctv.setChecked(false);
        else if(ServiceReceiver.redialFlag == true) ctv.setChecked(true);

        ctv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ctv.isChecked()) {
                    ServiceReceiver.redialFlag = false;
                    ctv.setChecked(false);
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean("redialFlag",false);
                    editor.commit();

                }
                else{
                    ctv.setChecked(true);
                    ServiceReceiver.redialFlag = true;
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean("redialFlag",true);
                    editor.commit();
                }

            }
        });

        Spinner spinner_redial_attempts = (Spinner) findViewById(R.id.max_number_of_redial_attempts);

        // Spinner Drop down elements
        ArrayList<Integer> redialAttemptOptions = new ArrayList<>();

        for(int i = 0; i <= MAX_REDIAL_ATTEMPT; i = i+2)
            redialAttemptOptions.add(i);

        // Creating adapter for spinner
        ArrayAdapter<Integer> dataAdapter_redial_attempts = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_dropdown_item, redialAttemptOptions);

        // Drop down layout style - list view with radio button
        dataAdapter_redial_attempts.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner_redial_attempts.setAdapter(dataAdapter_redial_attempts);

        //initial selection
        spinner_redial_attempts.setSelection(ServiceReceiver.REDIAL_ATTEMPT /2,true); // because the increament in the values is by 2
        // Spinner click listener
        spinner_redial_attempts.setOnItemSelectedListener(this);




        Spinner spinner_redial_delay = (Spinner) findViewById(R.id.redial_delay);

        // Spinner Drop down elements
        ArrayList<Integer> redialdelayOptions = new ArrayList<>();

        for(int i = 0; i <= MAX_REDIAL_DELAY; i++)
            redialdelayOptions.add(i);

        // Creating adapter for spinner
        ArrayAdapter<Integer> dataAdapter_redial_delay = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_dropdown_item, redialdelayOptions);

        // Drop down layout style - list view with radio button
        dataAdapter_redial_delay.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner_redial_delay.setAdapter(dataAdapter_redial_delay);

        //initial selection
        spinner_redial_delay.setSelection((int)(ServiceReceiver.redialPauseLength/1000),true);
        // Spinner click listener
        spinner_redial_delay.setOnItemSelectedListener(this);


        Spinner spinner_call_not_connected = (Spinner) findViewById(R.id.offhook_threshold_time);

        // Spinner Drop down elements
        ArrayList<Integer> offhookThresholdOptions = new ArrayList<>();

        for(int i = 0; i <= MAX_offhookThreshold; i = i+5)
            offhookThresholdOptions.add(i);

        // Creating adapter for spinner
        ArrayAdapter<Integer> dataAdapter_offhookThreshold= new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_dropdown_item, offhookThresholdOptions);

        // Drop down layout style - list view with radio button
        dataAdapter_offhookThreshold.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner_call_not_connected.setAdapter(dataAdapter_offhookThreshold);

        //initial selection
        spinner_call_not_connected.setSelection((int)((ServiceReceiver.Outgoing_OffHook_Time_Threshold /1000)/5),true); //because the increament in the values is by 5
        // Spinner click listener
        spinner_call_not_connected.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.max_number_of_redial_attempts:
                // On selecting a spinner item
                String item = parent.getItemAtPosition(position).toString();
                ServiceReceiver.REDIAL_ATTEMPT = Integer.valueOf(item);
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt("REDIAL_ATTEMPT", ServiceReceiver.REDIAL_ATTEMPT);
                editor.commit();
//            // Showing selected spinner item
//                Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();

                break;
            case R.id.redial_delay:
                item = parent.getItemAtPosition(position).toString();
                ServiceReceiver.redialPauseLength = Integer.valueOf(item)*1000; // to convert in milliseconds
                sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                editor = sharedPref.edit();
                editor.putLong("redialPauseLength", ServiceReceiver.redialPauseLength);
                editor.commit();

//                Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
                break;
            case R.id.offhook_threshold_time:
                item = parent.getItemAtPosition(position).toString();
                ServiceReceiver.Outgoing_OffHook_Time_Threshold = Integer.valueOf(item)*1000; // to convert in milliseconds
                sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                editor = sharedPref.edit();
                editor.putLong("Outgoing_OffHook_Time_Threshold", ServiceReceiver.Outgoing_OffHook_Time_Threshold);
                editor.commit();

//                Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
                break;

        }

    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }
}
