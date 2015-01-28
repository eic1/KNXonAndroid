package com.knxonandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by User on 14.01.2015.
 */
public class DeviceActivity extends ActionBarActivity {

    ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.create);

        lv = (ListView) findViewById(R.id.listView);

        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        String[] items = { "Lampe", "Wetteranzeige", "Werteanzeige"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, items);

        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
            {
                System.out.print(position);
                switch(position){
                    case 0:
                        Intent intent = new Intent(DeviceActivity.this, LampeActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case 1:
                        Intent intent2 = new Intent(DeviceActivity.this, WeatherActivity.class);
                        startActivity(intent2);
                        finish();
                        break;
                    case 2:
                        Intent intent3 = new Intent(DeviceActivity.this, FloatActivity.class);
                        startActivity(intent3);
                        finish();
                        break;

                }
            }
        });

    }
}

