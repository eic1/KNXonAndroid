package com.knxonandroid.Devices;

import java.io.Serializable;
import java.util.ArrayList;

import tuwien.auto.calimero.GroupAddress;

/**
 * Created by User on 26.01.2015.
 */
public class DeviceData{
    private ArrayList<ViewDevice> viewDevices;

    private static DeviceData instance;

    public static DeviceData getInstance() {

        if (instance == null) {

            instance = new DeviceData();
        }
        return instance;
    }

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
    public void remove(int id){
        viewDevices.get(id).removed = true;
        //viewDevices.remove(id);
    }
    public void addLamp(String Name,int main,int mid,int sub,int sendMain,int sendMid, int sendSub){
        Lamp TestLamp = new Lamp();
        TestLamp.setSendGroupAdress(new GroupAddress(sendMain, sendMid, sendSub));
        TestLamp.setRcvGroupAdress(new GroupAddress(main, mid, sub), 1000);
        TestLamp.setName(Name);
        viewDevices.add(TestLamp);

    }

    public void addFloat(String Name, String Label,String Einheit,int main,int mid,int sub){

        Floatdisplay disp = new Floatdisplay();
        disp.setEinheit(Einheit);
        disp.setLabel(Label);
        disp.setName(Name);
        disp.setRcvGroupAdress(new GroupAddress(main, mid, sub), 5000);
        viewDevices.add(disp);
    }

    public void addWeather(String Name,int main,int mid,int sub){
        Weatherdisplay weather = new Weatherdisplay();
        weather.setName(Name);
        weather.setRcvGroupAdress(new GroupAddress(main, mid, sub), 5000);
        viewDevices.add(weather);
    }
}
