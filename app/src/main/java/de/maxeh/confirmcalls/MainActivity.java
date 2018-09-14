package de.maxeh.confirmcalls;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;

public class MainActivity extends AppCompatActivity {
    final int PERMISSION_PROCESS_OUTGOING_CALLS = 100;
    private Context mContext;

    /**
     * Check permissions and, if needed, request permissions.
     * Start the CallConfirmService.
     * Set listener for checkbox.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        CheckBox checkBox = findViewById(R.id.checkBox);
        if (isMyServiceRunning(CallConfirmService.class)) {
            checkBox.setChecked(true);
        }
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View check) {
                if (((CheckBox) check).isChecked()) {
                    Intent serviceIntent = new Intent(mContext, CallConfirmService.class);
                    if (!isMyServiceRunning(CallConfirmService.class)) {
                        startService(serviceIntent);
                    }
                } else {
                    if (isMyServiceRunning(CallConfirmService.class)) {
                        Intent serviceIntent = new Intent(mContext, CallConfirmService.class);
                        stopService(serviceIntent);
                    }
                }
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED ||
                checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED ||
                checkSelfPermission(Manifest.permission.PROCESS_OUTGOING_CALLS) == PackageManager.PERMISSION_DENIED ||
                checkSelfPermission(Manifest.permission.SYSTEM_ALERT_WINDOW) == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.CALL_PHONE,
                            Manifest.permission.PROCESS_OUTGOING_CALLS,
                            Manifest.permission.SYSTEM_ALERT_WINDOW,
                    },
                    PERMISSION_PROCESS_OUTGOING_CALLS
            );
        }
    }

    /**
     * Check the result of the permission request.
     * If the user denied the permissions, a message is shown.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_PROCESS_OUTGOING_CALLS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.setTitle("Grant permissions");
                    alertDialog.setMessage("This app can only work if you grant the requested permissions.");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Okay, I understand.",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
            }
        }
    }

    /**
     * Check if a specific service is running.
     * @param serviceClass Name of the service class.
     * @return true when service is running, else false
     */
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
