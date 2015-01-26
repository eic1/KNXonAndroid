package com.knxonandroid.Devices;

import android.content.ContextWrapper;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.knxonandroid.R;

import java.util.Random;

import tuwien.auto.calimero.GroupAddress;

/**
 * Created by User on 25.01.2015.
 */
public class Floatdisplay extends ViewDevice {

    float value;

    TextView tvAnzeige;

    private String label,einheit;

    @Override
    public void drawTo(ContextWrapper context, LinearLayout layout) {
        super.drawTo(context,layout);

        tvAnzeige = new TextView(context);
        tvAnzeige.setText(label+": "+value+einheit);
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        llp.setMargins(dp(10),dp(2),dp(10),dp(2));
        tvAnzeige.setTextSize(TypedValue.COMPLEX_UNIT_SP,28);
        tvAnzeige.setLayoutParams(llp);
        myLayout.addView(tvAnzeige);
    }

    public void update(float value){
        this.value = value;
        tvAnzeige.setText(label+": "+value+einheit);
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
        return name+";"+rcvGA.getMainGroup()+";"+rcvGA.getMiddleGroup()+";"+rcvGA.getSubGroup8()+";"+period
                +";"+label+";"+einheit;
    }

    @Override
    public void fromString(String s) {
        String[] parts = s.split(";");
        this.setName(parts[0]);
        this.setRcvGroupAdress(new GroupAddress(Integer.valueOf(parts[1]),Integer.valueOf(parts[2]),Integer.valueOf(parts[3]))
                ,Integer.valueOf(parts[4]));
        this.setLabel(parts[5]);
        this.setEinheit(parts[6]);
    }

    public void setEinheit(String einheit) {
        this.einheit = einheit;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
