package com.knxonandroid;

import android.content.Context;
import android.content.ContextWrapper;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.calimero.knx.connection.sys.KnxCommunicationObject;

import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by User on 21.01.2015.
 */
public class KNXConnectionManager{
    // singleton
    private static KNXConnectionManager instance;


    public KnxCommunicationObject knxComObj;
    private static ContextWrapper contextWrapper;
    private String hostIp;

    public boolean connected = false;

    private KNXConnectionManager() {
        WifiManager wifiManager = (WifiManager) contextWrapper.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        hostIp = String.format("%d.%d.%d.%d",(ip & 0xff),(ip >> 8 & 0xff),(ip >> 16 & 0xff),(ip >> 24 & 0xff));
    }


    public boolean connect(String IP,int port) {

        try {
            knxComObj = KnxCommunicationObject.getInstance(hostIp,IP,port);
            return true;
        } catch (UnknownHostException e) {
            return false;
        }
    }

    public static void initialize(ContextWrapper contextWrapper) {

        instance = null;
        KNXConnectionManager.contextWrapper = contextWrapper;

    }
    public static KNXConnectionManager getInstance() {

        if (instance == null) {

            instance = new KNXConnectionManager();
        }
        return instance;
    }

    public void addObserver(Observer observer){
        knxComObj.addObserver(observer);
    }

}
