package com.knxonandroid.Devices;

import android.content.ContextWrapper;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.knxonandroid.R;

import java.util.Random;

import tuwien.auto.calimero.GroupAddress;

/**
 * Created by User on 25.01.2015.
 */
public class Weatherdisplay extends ViewDevice {


    ImageView iv;
    float luxValue;

    @Override
    public void drawTo(ContextWrapper context, LinearLayout layout) {
        super.drawTo(context,layout);

        iv = new ImageView(context);
        iv.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.wolke));
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(dp(120),dp(120));
        llp.setMargins(dp(10),dp(0),dp(10),dp(0));
        iv.setLayoutParams(llp);
        myLayout.addView(iv);
    }

    public void update(float value){
        luxValue = value;
        if(iv != null){
            if (value > 200) {
                iv.setImageBitmap(BitmapFactory.decodeResource(mainContext.getResources(),
                        R.drawable.sonne2));
            } else if (value > 100) {
                iv.setImageBitmap(BitmapFactory.decodeResource(mainContext.getResources(),
                        R.drawable.bewoelkt2));
            } else if (value > 50) {
                iv.setImageBitmap(BitmapFactory.decodeResource(mainContext.getResources(),
                        R.drawable.wolke));
            } else if (value > 10) {
                iv.setImageBitmap(BitmapFactory.decodeResource(mainContext.getResources(),
                        R.drawable.mond));
            }
        }
    }

    @Override
    public void startReading() {
        Random r = new Random();
        int i = r.nextInt(1000);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                knxConnectionManager.knxComObj.readPeriodicFloat(rcvGA, period);
            }
        }, i);

    }

    @Override
    public String toString() {
        return name+";"+rcvGA.getMainGroup()+";"+rcvGA.getMiddleGroup()+";"+rcvGA.getSubGroup8()+";"+period;
    }

    @Override
    public void fromString(String s) {
        String[] parts = s.split(";");
        this.setName(parts[0]);
        this.setRcvGroupAdress(new GroupAddress(Integer.valueOf(parts[1]),Integer.valueOf(parts[2]),Integer.valueOf(parts[3]))
                ,Integer.valueOf(parts[4]));
    }

}
