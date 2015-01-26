package com.knxonandroid.Devices;

import java.io.Serializable;
import java.util.ArrayList;

import tuwien.auto.calimero.GroupAddress;

/**
 * Created by User on 26.01.2015.
 */
public class DeviceData{
    private ArrayList<ViewDevice> viewDevices;

    public DeviceData(){
        viewDevices = new ArrayList<ViewDevice>();
    }


    public ArrayList<ViewDevice> getViewDevices() {
        return viewDevices;
    }

    public void setViewDevices(ArrayList<ViewDevice> viewDevices) {
        this.viewDevices = viewDevices;
    }

    public String toData(){
        StringBuilder s = new StringBuilder();
        for(ViewDevice d : viewDevices){
            if(d instanceof Lamp){
                s.append("lamp#");
            }else if (d instanceof Floatdisplay){
                s.append("floatdisplay#");
            }else if (d instanceof Weatherdisplay) {
                s.append("weatherdisplay#");
            }else{
                s.append("error#");
            }
            s.append(d.toString());
            s.append("-next-");
        }
        return s.toString();
    }
    public void fromData(String s){
        System.out.print(s);
       String[] parts = s.split("-next-");
        for(String s2 : parts){
            String[] split = s2.split("#");
            if(split[0].equals("lamp")){
                Lamp o = new Lamp();
                o.fromString(split[1]);
                viewDevices.add(o);
            }else if(split[0].equals("floatdisplay")){
                Floatdisplay o = new Floatdisplay();
                o.fromString(split[1]);
                viewDevices.add(o);
            }else if(split[0].equals("weatherdisplay")){
                Weatherdisplay o = new Weatherdisplay();
                o.fromString(split[1]);
                viewDevices.add(o);
            }
        }

    }
}
