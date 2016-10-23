package com.example.pikanshu.autoredial;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import static android.telephony.TelephonyManager.CALL_STATE_IDLE;
import static android.telephony.TelephonyManager.CALL_STATE_OFFHOOK;
import static android.telephony.TelephonyManager.CALL_STATE_RINGING;
import static com.example.pikanshu.autoredial.MyPhoneStateListener.PHONE_STATE_PREVIOUS;
import static com.example.pikanshu.autoredial.MyPhoneStateListener.PHONE_STATE;

/**
 * Created by pika on 19/10/16.
 */

public class ServiceReceiver extends BroadcastReceiver {
    static public TelephonyManager telephony = null;
    static public MyPhoneStateListener phoneListener = null;
    static public boolean telephonyRegistered = false;

    static public long redialPauseLength = 3000; // in milliseconds
    static public long Outgoing_OffHook_Time_Threshold = 5000; // in milliseconds
    static private boolean wasRinging = false;
    static public boolean redialFlag = false;
    static public int redialCount = 0;

    private long totalTimeOffHook;
    private boolean redialNeeded = true; // if the offHook Time during outgoing call is greater than a threshold then it's value is false
    static private long startOffHook;
    static private long endOffHook;

    static public int REDIAL_ATTEMPT = 0;             // redial attempts selected by user

    static public String PHONE_NUMBER = null;


    public void onReceive(Context context, Intent intent) {

        if(!telephonyRegistered) {

            SharedPreferences sharedPref= PreferenceManager.getDefaultSharedPreferences(context);
            redialFlag=sharedPref.getBoolean("redialFlag",false);
            REDIAL_ATTEMPT = sharedPref.getInt("REDIAL_ATTEMPT",0);
            redialPauseLength = sharedPref.getLong("redialPauseLength",3000);
            Outgoing_OffHook_Time_Threshold = sharedPref.getLong("Outgoing_OffHook_Time_Threshold",5000);

            phoneListener = new MyPhoneStateListener();
            telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            telephony.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
            telephonyRegistered = true;

            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            int callstate = telephony.getCallState();
            PHONE_STATE = callstate;
            if(callstate == CALL_STATE_RINGING) wasRinging = true;
//            Log.d("DEBUG", "BROADCAST RECEIVED State : " + state + " : " + callstate);
        }

//        Log.d("DEBUG", "BROADCAST RECEIVED 2");

//        Log.d("DEBUG", "PHONE_STATE : " + PHONE_STATE);
//        Log.d("DEBUG", "PHONE_STATE_PREVIOUS : " + PHONE_STATE_PREVIOUS);


        Toast.makeText(context, "Auto Redial Active", Toast.LENGTH_SHORT).show();
        if (PHONE_STATE == CALL_STATE_OFFHOOK) {
            if(!wasRinging){
                PHONE_STATE_PREVIOUS = PHONE_STATE;
                startOffHook = System.currentTimeMillis();
            }
            else wasRinging = false;
//            Log.d("DEBUG", "REDIAL NOT CALLED");
        } else if (PHONE_STATE == CALL_STATE_IDLE && PHONE_STATE_PREVIOUS == CALL_STATE_OFFHOOK) {
            endOffHook = System.currentTimeMillis();

            totalTimeOffHook = (endOffHook-startOffHook);

            if(totalTimeOffHook > Outgoing_OffHook_Time_Threshold)
                redialNeeded = false;
            if (redialCount < REDIAL_ATTEMPT && redialFlag && redialNeeded) {
//                Log.d("DEBUG", "REDIAL CALLED");
                Intent i = new Intent(context, RedialActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            } else {
//                Log.d("DEBUG", "MAX REDIAL COUNT REACHED");
                redialCount = 0;
            }

            redialNeeded = true;
            PHONE_STATE_PREVIOUS = PHONE_STATE;
        }
    }
}