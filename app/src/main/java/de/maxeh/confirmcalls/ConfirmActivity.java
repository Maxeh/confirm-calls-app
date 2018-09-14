package de.maxeh.confirmcalls;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

public class ConfirmActivity extends AppCompatActivity {

    /**
     * Uncheck all radioButtons and disable the confirmButton.
     */
    @Override
    protected void onPause() {
        super.onPause();
        RadioButton checkButton1 = findViewById(R.id.checkButton1);
        RadioButton checkButton2 = findViewById(R.id.checkButton2);
        RadioButton checkButton3 = findViewById(R.id.checkButton3);
        final RadioButton[] checkButtons = {checkButton1, checkButton2, checkButton3};
        for (RadioButton checkButton : checkButtons) {
            checkButton.setChecked(false);
        }

        Button confirmButton = findViewById(R.id.buttonConfirm);
        confirmButton.setEnabled(false);
    }

    /**
     * Set listeners for buttons on the UI.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);
        getSupportActionBar().hide();

        Button cancelButton = findViewById(R.id.buttonCancel);
        cancelButton.setVisibility(View.VISIBLE);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAndRemoveTask();
            }
        });

        final String phoneNumber = getIntent().getStringExtra("phoneNumber");
        final Button confirmButton = findViewById(R.id.buttonConfirm);
        confirmButton.setVisibility(View.VISIBLE);
        confirmButton.setText("Call " + phoneNumber);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
                CallConfirmService.sAllowCall = true;
                startActivity(intent);

                finishAndRemoveTask();
            }
        });

        RadioButton checkButton1 = findViewById(R.id.checkButton1);
        RadioButton checkButton2 = findViewById(R.id.checkButton2);
        RadioButton checkButton3 = findViewById(R.id.checkButton3);
        final RadioButton[] checkButtons = {checkButton1, checkButton2, checkButton3};
        for (RadioButton checkButton : checkButtons) {
            checkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean enableButton = true;
                    for (RadioButton checkButton : checkButtons) {
                        if (!checkButton.isChecked())
                            enableButton = false;
                    }
                    if (enableButton) {
                        confirmButton.setEnabled(true);
                    }
                }
            });
        }
    }
}
