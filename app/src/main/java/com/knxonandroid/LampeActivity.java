package com.knxonandroid;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.knxonandroid.Devices.DeviceData;

/**
 * Created by User on 26.01.2015.
 */
public class LampeActivity  extends ActionBarActivity {

    Button bADd;
    DeviceData data;
    EditText tfName,etRcvMain,etRcvMid,etRcvSub,etSendMain,etSendSub,etSendMid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.lampe);


       data = DeviceData.getInstance();

        tfName = (EditText) findViewById(R.id.tfName);
        etRcvMain = (EditText) findViewById(R.id.etRcvMain);
        etRcvMid = (EditText) findViewById(R.id.etRcvMid);
        etRcvSub = (EditText) findViewById(R.id.etRcvSub);
        etSendMain = (EditText) findViewById(R.id.etSendMain);
        etSendMid = (EditText) findViewById(R.id.etSendMid);
        etSendSub = (EditText) findViewById(R.id.etSendSub);

        bADd = (Button) findViewById(R.id.bAdd);

        bADd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                data.addLamp(tfName.getText().toString(),
                        Integer.valueOf(etRcvMain.getText().toString()),
                        Integer.valueOf(etRcvMid.getText().toString()),
                        Integer.valueOf(etRcvSub.getText().toString()),
                        Integer.valueOf(etSendMain.getText().toString()),
                        Integer.valueOf(etSendMid.getText().toString()),
                        Integer.valueOf(etSendSub.getText().toString()));
                finish();
            }
        });


    }
}
