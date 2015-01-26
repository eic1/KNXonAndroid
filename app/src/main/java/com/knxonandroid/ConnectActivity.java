package com.knxonandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.calimero.knx.connection.knxobject.KnxBooleanObject;
import com.calimero.knx.connection.knxobject.KnxComparableObject;
import com.calimero.knx.connection.knxobject.KnxControlObject;
import com.calimero.knx.connection.knxobject.KnxFloatObject;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by User on 14.01.2015.
 */
public class ConnectActivity extends ActionBarActivity implements Observer{

    Button bVerbinden;
    EditText tfIP,tfPort;

    KNXConnectionManager knxConnectionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connect);


        bVerbinden = (Button) findViewById(R.id.bVerbinden);
        tfIP = (EditText) findViewById(R.id.tfIP);
        tfPort = (EditText) findViewById(R.id.tfPort);

        knxConnectionManager = KNXConnectionManager.getInstance();

        bVerbinden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                knxConnectionManager.connected = true;
                //finish();
                knxConnectionManager.connect(tfIP.getText().toString(),Integer.valueOf(tfPort.getText().toString()));
                knxConnectionManager.addObserver(ConnectActivity.this);
                //Intent intent1 = new Intent(ConnectActivity.this, MainActivity.class);
                //startActivity(intent1);
            }
        });

    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void update(Observable observable, Object data) {
        System.out.println("Update from MainActivity called by: " + observable);
        System.out.println("with data: " + observable);
        if (data instanceof KnxComparableObject) {
            if (data instanceof KnxBooleanObject) {
                final boolean read = ((KnxBooleanObject) data).getValue();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("RunOnUiThread Run runned");

                    }
                });
            } else if (data instanceof KnxFloatObject) {
                final float read = ((KnxFloatObject) data).getValue();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("RunOnUiThread Run runned");
                        //tfRcvValue.setText("Read " + read + " from Bus");
                    }
                });
            } else if (data instanceof KnxControlObject){
                final byte read = ((KnxControlObject) data).getValue();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("RunOnUiThread Run runned");
                        //tfRcvValue.setText("Read " + read + " from Bus");
                    }
                });
            }
        } else if (knxConnectionManager.knxComObj != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("RunOnUiThread Run runned");
                    if (knxConnectionManager.knxComObj.isConnected()) {

                        //Intent intent = new Intent(ConnectActivity.this, MainActivity.class);
                        //startActivity(intent);
                        finish();
                        System.out.println("To the Main.");
                        //connected
                    } else {
                        //disconnected

                    }
                }
            });
        }
    }
}

