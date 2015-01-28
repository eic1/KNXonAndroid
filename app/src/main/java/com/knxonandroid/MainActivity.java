package com.knxonandroid;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import com.calimero.knx.connection.knxobject.KnxBooleanObject;
import com.calimero.knx.connection.knxobject.KnxComparableObject;
import com.calimero.knx.connection.knxobject.KnxControlObject;
import com.calimero.knx.connection.knxobject.KnxFloatObject;
import com.knxonandroid.Devices.DeviceData;
import com.knxonandroid.Devices.Floatdisplay;
import com.knxonandroid.Devices.ViewDevice;
import com.knxonandroid.Devices.Lamp;
import com.knxonandroid.Devices.Weatherdisplay;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import it.neokree.materialtabs.MaterialTabHost;
import tuwien.auto.calimero.GroupAddress;

public class MainActivity extends ActionBarActivity implements Observer {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private ListView leftDrawerList;
    private ArrayAdapter<String> navigationDrawerAdapter;
    private String[] leftSliderData;
    MaterialTabHost tabHost;

    Button bUAn,bUAus,bMAn,bMAus;
    ImageView ivMitte,ivUnten,ivWetter;
    String filename = "DATA";
    TextView tvTemp,tvWind;

    GroupAddress GAMitte,GAUnten;
    GroupAddress adr;
    GroupAddress GAWind,GATemp,GALicht;
    boolean knxinit = false;

    DeviceData data;
    LinearLayout layout;
    KNXConnectionManager knxConnectionManager;

    void drawAllDevices(){
       if(layout.getChildCount() > 0)
           layout.removeAllViews();

        int imgCount = 0;
        LinearLayout ll = new LinearLayout(this);
        for( ViewDevice d: data.getViewDevices()){
            if(!d.removed) {
                if (d instanceof Floatdisplay) {
                    d.drawTo(this, layout);
                    final int d_id = data.getViewDevices().indexOf(d);
                    d.addLongClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            System.out.println("Long Click");
                            data.remove(d_id);
                            drawAllDevices();

                        }
                    });

                } else if (imgCount % 2 == 0) {
                    ll = new LinearLayout(this);
                    d.drawTo(this, ll);
                    final int d_id = data.getViewDevices().indexOf(d);
                    d.addLongClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            System.out.println("Long Click");
                            data.remove(d_id);
                            drawAllDevices();

                        }
                    });
                    layout.addView(ll);
                    imgCount++;
                } else {
                    d.drawTo(this, ll);
                    //if(d instanceof )
                    //d.update(true);
                    final int d_id = data.getViewDevices().indexOf(d);
                    d.addLongClick(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            System.out.println("Long Click");
                            data.remove(d_id);
                            drawAllDevices();
                        }
                    });

                    imgCount++;
                }
            }
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        if(!knxinit){
            System.out.println("KNx init");
            KNXConnectionManager.initialize(this);
            knxinit = true;
        }
        knxConnectionManager = KNXConnectionManager.getInstance();



        layout = (LinearLayout) findViewById(R.id.mainLayout);


        fillWithDummy();
        //loadData();

        drawAllDevices();


        /*bUAn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                knxConnectionManager.knxComObj.writeBoolean(new GroupAddress(1,5,11),true);
            }
        });
        bUAus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                knxConnectionManager.knxComObj.writeBoolean(new GroupAddress(1,5,11),false);
            }
        });
        bMAn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                knxConnectionManager.knxComObj.writeBoolean(new GroupAddress(1,5,10),true);
            }
        });
        bMAus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                knxConnectionManager.knxComObj.writeBoolean(new GroupAddress(1,5,10),false);
            }
        });
    */



        if (!knxConnectionManager.connected){
            System.out.println("To Connect.");
            Intent intent4 = new Intent(MainActivity.this, ConnectActivity.class);
            startActivity(intent4);
        }else {
            knxConnectionManager.addObserver(this);
            System.out.print("Addded Read");
        }

        leftSliderData = getResources().getStringArray(R.array.menuarray);
        nitView();
        if (toolbar != null) {
            toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
            toolbar.setTitle("KnxOnAndroid");
            setSupportActionBar(toolbar);
        }
        initDrawer();


    }

    @Override
    public void onResume(){
        super.onResume();


        drawAllDevices();

       System.out.println("OnResume");
        if (!knxConnectionManager.connected){
           System.out.println("To the Connect.");

        }else {
            knxConnectionManager.addObserver(this);
            System.out.println("Addded Read");
            for( ViewDevice d: data.getViewDevices()){
                d.startReading();
            }
        }
    }

    @Override
    public void onPause(){
        saveData();
        super.onPause();
    }

    private void saveData(){
        FileOutputStream fos = null;
        try {
            fos = this.openFileOutput(filename, Context.MODE_PRIVATE);
            fos.write(data.toData().getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void loadData(){
        data = DeviceData.getInstance();
        String ret = "";
        InputStream inputStream = null;
        try {
            inputStream = openFileInput(filename);
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        data.fromData(ret);

    }

    private void fillWithDummy(){

        data = DeviceData.getInstance();
        Lamp TestLamp = new Lamp();
        TestLamp.setSendGroupAdress(new GroupAddress(1, 5, 10));
        TestLamp.setRcvGroupAdress(new GroupAddress(3, 5, 0), 1000);
        TestLamp.setName("Leuchte Oben");
        Lamp TestLamp2 = new Lamp();
        TestLamp2.setSendGroupAdress(new GroupAddress(1,5,11));
        TestLamp2.setRcvGroupAdress(new GroupAddress(3,5,1),1000);
        TestLamp2.setName("Leuchte Mitte - Unten");
        Weatherdisplay weather = new Weatherdisplay();
        weather.setName("Wetterstation Lux");
        weather.setRcvGroupAdress(new GroupAddress(3,3,0),5000);
        Floatdisplay disp = new Floatdisplay();
        disp.setEinheit("°C");
        disp.setLabel("Temperatur");
        disp.setName("Wetterstation Temperatur");
        disp.setRcvGroupAdress(new GroupAddress(3, 3, 1), 5000);
        Floatdisplay disp2 = new Floatdisplay();
        disp2.setEinheit("km/h");
        disp2.setLabel("Windstärke");
        disp2.setName("Wetterstation Wind");
        disp2.setRcvGroupAdress(new GroupAddress(3,3,2),5000);

        data.getViewDevices().add(TestLamp);
        data.getViewDevices().add(TestLamp2);
        data.getViewDevices().add(weather);
        data.getViewDevices().add(disp);
        data.getViewDevices().add(disp2);
    }
    private void nitView() {
        leftDrawerList = (ListView) findViewById(R.id.left_drawer);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationDrawerAdapter=new ArrayAdapter<String>( MainActivity.this, android.R.layout.simple_list_item_1, leftSliderData);
        leftDrawerList.setAdapter(navigationDrawerAdapter);
        leftDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //int position2 = position +1;
                //Toaster toasterTemp = new Toaster();
                //toasterTemp.customToast(Integer.toString(position2),view, MainActivity.this);

                switch (position){
                    case 0:
                        Intent intent1 = new Intent(MainActivity.this, DeviceActivity.class);
                        startActivity(intent1);
                            break;
                    case 1:
                        knxConnectionManager.connected = false;
                        Intent intent4 = new Intent(MainActivity.this, ConnectActivity.class);
                        startActivity(intent4);
                            break;
                }
            }
        });
    }

    private void initDrawer() {

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

            }
        };
        drawerLayout.setDrawerListener(drawerToggle);


    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void update(Observable observable, Object data) {
        System.out.println("Update from MainActivity called by: " + observable);
        System.out.println("with data: " + observable);
        if (data instanceof KnxComparableObject) {
           adr = ((KnxComparableObject) data).getGroupAddress();
            for( ViewDevice d: this.data.getViewDevices()){
                if(d.rcvGA.equals(adr)){
                    if(d instanceof Floatdisplay){

                        final Floatdisplay fd = (Floatdisplay) d;
                        final float read = ((KnxFloatObject) data).getValue();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                fd.update(read);
                            }
                        });
                    }else if(d instanceof Lamp){
                        final Lamp l = (Lamp) d;
                        final boolean read = ((KnxBooleanObject) data).getValue();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                l.update(read);
                            }
                        });
                    }else if(d instanceof Weatherdisplay){
                        final Weatherdisplay wd = (Weatherdisplay) d;
                        final float read = ((KnxFloatObject) data).getValue();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                wd.update(read);
                            }
                        });
                    }
                }
            }
        } else if (knxConnectionManager.knxComObj != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("RunOnUiThread Run runned");
                    if (knxConnectionManager.knxComObj.isConnected()) {

                        //connected
                    } else {
                        //disconnected
                        Intent intent4 = new Intent(MainActivity.this, ConnectActivity.class);
                        startActivity(intent4);
                    }
                }
            });
        }

    }
}