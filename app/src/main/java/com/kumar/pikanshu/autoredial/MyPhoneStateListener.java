package com.kumar.pikanshu.autoredial;

import android.telephony.PhoneStateListener;
import android.util.Log;

import static android.telephony.TelephonyManager.CALL_STATE_IDLE;
import static android.telephony.TelephonyManager.CALL_STATE_OFFHOOK;
import static android.telephony.TelephonyManager.CALL_STATE_RINGING;

/**
 * Created by pika on 19/10/16.
 */

public class MyPhoneStateListener extends PhoneStateListener {


    public static int PHONE_STATE_PREVIOUS = CALL_STATE_IDLE;
    public static int PHONE_STATE = CALL_STATE_IDLE;

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {

        switch (state) {
            case CALL_STATE_IDLE:
                Log.d("DEBUG", "IDLE : " + incomingNumber);
                PHONE_STATE = CALL_STATE_IDLE;
                ServiceReceiver.PHONE_NUMBER = incomingNumber;
                break;
            case CALL_STATE_OFFHOOK:
                Log.d("DEBUG", "OFFHOOK: " + incomingNumber);
                PHONE_STATE = CALL_STATE_OFFHOOK;
                ServiceReceiver.PHONE_NUMBER = incomingNumber;
                break;
            case CALL_STATE_RINGING:
                Log.d("DEBUG", "RINGING: " + incomingNumber);
                PHONE_STATE = CALL_STATE_RINGING;
                ServiceReceiver.PHONE_NUMBER = incomingNumber;
                break;
        }
    }

}
