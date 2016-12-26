package com.kumar.pikanshu.autoredial;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    static public final int MAX_REDIAL_ATTEMPT = 50; // Maximum number of redials supported by application
    static public final int MAX_REDIAL_DELAY = 10; // Maximum delay( in seconds)  supported by application
    static public final int MAX_offhookThreshold= 50; // Maximum threshold( in seconds) for detecting call not connected supported by application
    private Context context;
    private Activity activity;

    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE};


    private View view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();
        activity = this;


        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        SharedPreferences sharedPref= PreferenceManager.getDefaultSharedPreferences(this);
        ServiceReceiver.redialFlag = sharedPref.getBoolean("redialFlag",false);
        ServiceReceiver.REDIAL_ATTEMPT = sharedPref.getInt("REDIAL_ATTEMPT",4);
        ServiceReceiver.redialPauseLength = sharedPref.getLong("redialPauseLength",2000);
        ServiceReceiver.Outgoing_OffHook_Time_Threshold = sharedPref.getLong("Outgoing_OffHook_Time_Threshold",10000);
        ServiceReceiver.redialForSelected = sharedPref.getBoolean("redialForSelected",false);

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
        ArrayAdapter<Integer> dataAdapter_redial_attempts = new ArrayAdapter<Integer>(this, R.layout.spinner_item, redialAttemptOptions);

        // Drop down layout style - list view with radio button
        dataAdapter_redial_attempts.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner_redial_attempts.setAdapter(dataAdapter_redial_attempts);

        //initial selection
        spinner_redial_attempts.setSelection(2,true); // 2 is position of selected element
        // Spinner click listener
        spinner_redial_attempts.setOnItemSelectedListener(this);




        Spinner spinner_redial_delay = (Spinner) findViewById(R.id.redial_delay);

        // Spinner Drop down elements
        ArrayList<Integer> redialdelayOptions = new ArrayList<>();

        for(int i = 0; i <= MAX_REDIAL_DELAY; i++)
            redialdelayOptions.add(i);

        // Creating adapter for spinner
        ArrayAdapter<Integer> dataAdapter_redial_delay = new ArrayAdapter<Integer>(this, R.layout.spinner_item, redialdelayOptions);

        // Drop down layout style - list view with radio button
        dataAdapter_redial_delay.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner_redial_delay.setAdapter(dataAdapter_redial_delay);

        //initial selection
        spinner_redial_delay.setSelection(2,true); // 2 is position of selected element
        // Spinner click listener
        spinner_redial_delay.setOnItemSelectedListener(this);


        Spinner spinner_call_not_connected = (Spinner) findViewById(R.id.offhook_threshold_time);

        // Spinner Drop down elements
        ArrayList<Integer> offhookThresholdOptions = new ArrayList<>();

        for(int i = 0; i <= MAX_offhookThreshold; i = i+5)
            offhookThresholdOptions.add(i);

        // Creating adapter for spinner
        ArrayAdapter<Integer> dataAdapter_offhookThreshold= new ArrayAdapter<Integer>(this, R.layout.spinner_item, offhookThresholdOptions);

        // Drop down layout style - list view with radio button
        dataAdapter_offhookThreshold.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner_call_not_connected.setAdapter(dataAdapter_offhookThreshold);

        //initial selection
        spinner_call_not_connected.setSelection(2,true); // 2 is position of selected element
        // Spinner click listener
        spinner_call_not_connected.setOnItemSelectedListener(this);


        final CheckBox checkBox = (CheckBox) findViewById(R.id.redial_for_selected_checkbox) ;
        final Button selectContactButton = (Button) findViewById(R.id.action_select_contacts) ;
        if(ServiceReceiver.redialForSelected){
            checkBox.setChecked(ServiceReceiver.redialForSelected);
            selectContactButton.setVisibility(View.VISIBLE);
        }
        else{
            checkBox.setChecked(ServiceReceiver.redialForSelected);
            selectContactButton.setVisibility(View.GONE);

        }
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!checkBox.isChecked()){
                    ServiceReceiver.redialForSelected = false;
                    selectContactButton.setVisibility(View.GONE);
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
                    editor.putBoolean("redialForSelected",ServiceReceiver.redialForSelected);
                    editor.commit();
                }
                else if(checkBox.isChecked()) {
                    ServiceReceiver.redialForSelected = true;
                    selectContactButton.setVisibility(View.VISIBLE);
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
                    editor.putBoolean("redialForSelected",ServiceReceiver.redialForSelected);
                    editor.commit();
                }
            }
        });

        selectContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!hasPermissions(context, PERMISSIONS)){
                    ActivityCompat.requestPermissions(activity, PERMISSIONS, PERMISSION_ALL);
                }else {
                    Intent intent = new Intent(MainActivity.this, ContactSelectionActivity.class);
                    startActivity(intent);
                }
            }
        });
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
                break;

        }

    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }


    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
