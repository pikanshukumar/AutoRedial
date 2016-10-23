package com.example.pikanshu.autoredial;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class RedialActivity extends AppCompatActivity {

    private boolean redialCancled = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ///////////// Custom Alert Dialog //////////////////////////
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.custom_alert_dialog, null);

        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        Button cancleButton = (Button) promptView.findViewById(R.id.cancelButton);


        cancleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // btnAdd1 has been clicked
                redialCancled = true;
                if(alertDialog != null) alertDialog.dismiss();
                finish();
            }
        });

        alertDialog.setView(promptView);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        ////////////////////////////////////////////////////

        final TextView timeRemainingView = (TextView) alertDialog.findViewById(R.id.timeRemainingView);

        // since the counter on the alertdialog was starting from ServiceReceiver.redialPauseLength-1 and was ending on 1 sec
        // so the timer length increased  with 2 and display of timermaining decresed by one to reach 0.
        new CountDownTimer(ServiceReceiver.redialPauseLength + 2000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeRemainingView.setText("Time Remaining : "+ ((millisUntilFinished-1000)/1000) + " Seconds");
            }

            @Override
            public void onFinish() {
                //Do something after 100ms
//                timeRemainingView.setText("");
//                if(alertDialog != null) alertDialog.dismiss();
                if(!redialCancled) {
                    redial(RedialActivity.this);
                    ServiceReceiver.redialCount++;
                }
                else{
                    ServiceReceiver.redialCount = 0; // since phone state will not change . So, redial block will not be called in ServiceReceiver.
                    // Therefore, for next attempt of Calling redial Count have to be set to 0.
                }
                if(alertDialog != null) alertDialog.dismiss();
                finish();
            }
        }.start();
    }

    public void redial(Context context){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            if (!ServiceReceiver.PHONE_NUMBER.isEmpty()) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+ServiceReceiver.PHONE_NUMBER));
                context.startActivity(callIntent);
            }
            else{
                Toast.makeText(context,"Not a valid number ",Toast.LENGTH_SHORT).show();
            }
        }

    }
}
