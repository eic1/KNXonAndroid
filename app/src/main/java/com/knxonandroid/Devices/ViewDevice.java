package com.knxonandroid.Devices;

import android.content.Context;
import android.content.ContextWrapper;
import android.text.Layout;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.knxonandroid.KNXConnectionManager;
import com.knxonandroid.MainActivity;

import java.io.Serializable;

import tuwien.auto.calimero.GroupAddress;

/**
 * Created by User on 25.01.2015.
 */
abstract public class ViewDevice{
    LinearLayout myLayout;

   public boolean removed = false;
    KNXConnectionManager knxConnectionManager;
    ContextWrapper mainContext;

    TextView tv;
    String name;
    public GroupAddress rcvGA;
    int period;

    public ViewDevice(){
        knxConnectionManager = KNXConnectionManager.getInstance();
    }

    public void drawTo(ContextWrapper context,LinearLayout layout) {
        mainContext = context;
        myLayout = new LinearLayout(context);

        tv = new TextView(context);

        tv.setText(name);

        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        llp.setMargins(dp(5), dp(10), dp(5), dp(0));
        tv.setLayoutParams(llp);
        myLayout.setOrientation(LinearLayout.VERTICAL);
        myLayout.addView(tv);
        layout.addView(myLayout);

    }

    public void addLongClick(View.OnClickListener l){
       tv.setOnClickListener(l);
    }

    public void setName(String name){
        this.name = name;
    } ;

    int dp(int value) {
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, mainContext.getResources().getDisplayMetrics());
    }

    public void setRcvGroupAdress(GroupAddress ga,int period){
        rcvGA = ga;
        this.period = period;
    }

    abstract public void startReading();

    abstract public String toString();
    abstract public void fromString(String s);




}
