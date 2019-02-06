package devoops.smartwastemanagement;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;

public class MainActivity extends AppCompatActivity{

    Button btlogin;
    int i = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btlogin = findViewById(R.id.btlogin);
        btlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (MainActivity.this, Login.class);
                startActivity(intent);
            }
        });

        new CountDownTimer(300000000, 200) {

            public void onTick(long millisUntilFinished) {
                if (i==0)
                {
                    i = 1;
                    btlogin.setBackgroundResource(R.drawable.blinkingborder);
                }
                else
                {
                    btlogin.setBackgroundResource(R.drawable.blinkingborder2);
                    i = 0;
                }
            }

            public void onFinish() {
            }
        }.start();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        moveTaskToBack(true);
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}
