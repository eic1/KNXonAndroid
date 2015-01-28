package com.knxonandroid;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.knxonandroid.Devices.DeviceData;

/**
 * Created by User on 26.01.2015.
 */
public class FloatActivity  extends ActionBarActivity {
    Button bADd;
    DeviceData data;
    EditText tfName,etRcvMain,etRcvMid,etRcvSub,etLabel,etEinheit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.floatdisplay);


        data = DeviceData.getInstance();

        tfName = (EditText) findViewById(R.id.tfName);
        etRcvMain = (EditText) findViewById(R.id.etRcvMain);
        etRcvMid = (EditText) findViewById(R.id.etRcvMid);
        etRcvSub = (EditText) findViewById(R.id.etRcvSub);
        etLabel = (EditText) findViewById(R.id.etLabel);
        etEinheit = (EditText) findViewById(R.id.etEinheit);

        bADd = (Button) findViewById(R.id.bAdd);

        bADd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                data.addFloat(tfName.getText().toString(),
                        etLabel.getText().toString(),
                        etEinheit.getText().toString(),
                        Integer.valueOf(etRcvMain.getText().toString()),
                        Integer.valueOf(etRcvMid.getText().toString()),
                        Integer.valueOf(etRcvSub.getText().toString()));
                finish();
            }
        });


    }
}
