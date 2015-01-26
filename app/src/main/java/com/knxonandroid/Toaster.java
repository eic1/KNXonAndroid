package com.knxonandroid;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


public class Toaster{
    public Toaster() {
    }

    public void customToast(String message, View viewer, Activity activity){
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_layout, (ViewGroup) viewer.findViewById(R.id.toast_layout_root));
        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(message);

        Toast toast = new Toast(activity.getApplicationContext());
        // toast.setGravity(Gravity.BOTTOM|FILL_VERTICAL , 0, 0);
        toast.setGravity(Gravity.BOTTOM|Gravity.FILL_HORIZONTAL , 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
}
