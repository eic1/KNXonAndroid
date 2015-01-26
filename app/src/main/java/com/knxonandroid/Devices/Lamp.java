package com.knxonandroid.Devices;

import android.content.ContextWrapper;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.knxonandroid.R;

import java.util.Random;

import tuwien.auto.calimero.GroupAddress;

/**
 * Created by User on 25.01.2015.
 */
public class Lamp extends ViewDevice {

    ImageView iv;

    boolean state;


    GroupAddress sendGA;

    public Lamp(){
        super();

    }

    @Override
    public void drawTo(ContextWrapper context,LinearLayout layout) {
        super.drawTo(context,layout);

        iv = new ImageView(context);
        iv.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.aus));
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(dp(120),dp(120));
        llp.setMargins(dp(10),dp(0),dp(10),dp(7));
        iv.setLayoutParams(llp);
        myLayout.addView(iv);

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(state) {
                    knxConnectionManager.knxComObj.writeBoolean(sendGA, false);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            knxConnectionManager.knxComObj.readBoolean(rcvGA);
                        }
                    }, 500);

                }else {
                    knxConnectionManager.knxComObj.writeBoolean(sendGA, true);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            knxConnectionManager.knxComObj.readBoolean(rcvGA);
                        }
                    }, 500);
                }
            }
        });

    }

    public void update(boolean value){
        state = value;
        if(iv != null){
            if(value){
                iv.setImageBitmap(BitmapFactory.decodeResource(mainContext.getResources(), R.drawable.an));
            }else{
                iv.setImageBitmap(BitmapFactory.decodeResource(mainContext.getResources(), R.drawable.aus));
            }
        }
    }



    public void setSendGroupAdress(GroupAddress ga){
        sendGA = ga;
    }

    @Override
    public void startReading() {
        Random r = new Random();
        int i = r.nextInt(1000);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                knxConnectionManager.knxComObj.readPeriodicBoolean(rcvGA, period);
            }
        }, i);
    }

    @Override
    public String toString() {
        return name+";"+rcvGA.getMainGroup()+";"+rcvGA.getMiddleGroup()+";"+rcvGA.getSubGroup8()+";"+period+
            ";"+sendGA.getMainGroup()+";"+sendGA.getMiddleGroup()+";"+sendGA.getSubGroup8();
    }

    @Override
    public void fromString(String s) {
        String[] parts = s.split(";");
        this.setName(parts[0]);
        this.setRcvGroupAdress(new GroupAddress(Integer.valueOf(parts[1]),Integer.valueOf(parts[2]),Integer.valueOf(parts[3]))
                ,Integer.valueOf(parts[4]));
        this.setSendGroupAdress(new GroupAddress(Integer.valueOf(parts[5]),Integer.valueOf(parts[6]),Integer.valueOf(parts[7])));
    }

}
