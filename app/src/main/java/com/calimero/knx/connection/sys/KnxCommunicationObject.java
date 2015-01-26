package com.calimero.knx.connection.sys;


import com.calimero.knx.connection.exception.NotInResultsException;
import com.calimero.knx.connection.knxobject.KnxBooleanObject;
import com.calimero.knx.connection.knxobject.KnxComparableObject;
import com.calimero.knx.connection.knxobject.KnxControlObject;
import com.calimero.knx.connection.knxobject.KnxFloatObject;

import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import tuwien.auto.calimero.GroupAddress;
import tuwien.auto.calimero.knxnetip.KNXnetIPConnection;

/**
 * Created by gerritwolff on 08.12.14.
 */
public class KnxCommunicationObject extends Observable implements Observer {
    private final KnxBusConnection knxBusConnection;
    private final Container taskContainer;
    private final Container resultContainer;
    private final Container errorContainer;
    private final String hostIp, gatewayIp;
    private final int gatewayPort;
    private final Timer timer = new Timer();

    private final static List<KnxCommunicationObject> communicationObjects = new LinkedList<KnxCommunicationObject>();

    private KnxCommunicationObject() {
        //Dummy Konstructor um den standard Konstruktor zu verbergen. Sollte niemals aufgerufen werden.
        knxBusConnection = null;
        taskContainer = null;
        resultContainer = null;
        errorContainer = null;
        hostIp = null;
        gatewayIp = null;
        gatewayPort = 0;
    }

    private KnxCommunicationObject(String hostIp, String gatewayIp, int gatewayPort) throws UnknownHostException {
        this.hostIp = hostIp;
        this.gatewayIp = gatewayIp;
        this.gatewayPort = gatewayPort;
        taskContainer = new Container();
        resultContainer = new Container();
        errorContainer = new Container();
        knxBusConnection = new KnxBusConnection(this.hostIp, this.gatewayIp, this.gatewayPort, taskContainer, resultContainer, errorContainer);
        resultContainer.addObserver(this);
        knxBusConnection.addObserver(this);

        Thread knxBusConnectionThread = new Thread(knxBusConnection);
        knxBusConnectionThread.start();
    }

    public void writeBoolean(GroupAddress groupAddress, boolean value) {
        taskContainer.push(new KnxBooleanObject(groupAddress, value, false));
    }

    public void writeFloat(GroupAddress groupAddress, float value) {
        taskContainer.push(new KnxFloatObject(groupAddress, value, false));
    }

    public void readBoolean(GroupAddress groupAddress) {
        taskContainer.push(new KnxBooleanObject(groupAddress, true));
    }

    public void readFloat(GroupAddress groupAddress) {
        taskContainer.push(new KnxFloatObject(groupAddress, true));
    }

    public void readControl(GroupAddress groupAddress) {
        taskContainer.push(new KnxControlObject(groupAddress, true));
    }

    public void readPeriodicBoolean(GroupAddress groupAddress, int refreshRate) {
        timer.schedule(new ContinuousRead(new KnxBooleanObject(groupAddress, true)), 0, refreshRate);
    }

    public void readPeriodicFloat(GroupAddress groupAddress, int refreshRate) {
        timer.schedule(new ContinuousRead(new KnxFloatObject(groupAddress, true)), 0, refreshRate);
    }

    public void readPeriodicControl(GroupAddress groupAddress, int refreshRate) {
        timer.schedule(new ContinuousRead(new KnxControlObject(groupAddress, true)), 0, refreshRate);
    }

    public boolean readBooleanFromResults(GroupAddress groupAddress) throws NotInResultsException {
        KnxComparableObject knxBoolean = resultContainer.getByGroupAddress(groupAddress, true);
        if (knxBoolean != null && knxBoolean instanceof KnxBooleanObject) {
            return ((KnxBooleanObject) knxBoolean).getValue();
        } else {
            throw new NotInResultsException();
        }
    }

    public float readFloatFromResults(GroupAddress groupAddress) throws NotInResultsException {
        KnxComparableObject knxFloat = resultContainer.getByGroupAddress(groupAddress, true);
        if (knxFloat != null && knxFloat instanceof KnxBooleanObject) {
            return ((KnxFloatObject) knxFloat).getValue();
        } else {
            throw new NotInResultsException();
        }
    }

    public byte readControlFromResults(GroupAddress groupAddress) throws NotInResultsException {
        KnxComparableObject knxControl = resultContainer.getByGroupAddress(groupAddress, true);
        if (knxControl != null && knxControl instanceof KnxControlObject) {
            return ((KnxControlObject) knxControl).getValue();
        } else {
            throw new NotInResultsException();
        }
    }

    public boolean isConnected() {
        return !knxBusConnection.terminated();
    }

    @Override
    public void update(Observable observable, Object data) {
        if (data instanceof Boolean) {
            if (knxBusConnection.terminated()) {
                timer.cancel();
            }
        }
        this.setChanged();
        this.notifyObservers(data);
    }

    public static KnxCommunicationObject getInstance(String hostIp, String gatewayIp) throws UnknownHostException {
        return getInstance(hostIp, gatewayIp, KNXnetIPConnection.IP_PORT);
    }

    public static KnxCommunicationObject getInstance(String hostIp, String gatewayIp, int port) throws UnknownHostException {
        Iterator<KnxCommunicationObject> iterator = communicationObjects.iterator();
        KnxCommunicationObject knxComObj;
        while (iterator.hasNext()) {
            knxComObj = iterator.next();
            if (knxComObj.hostIp.equals(hostIp) &&
                    knxComObj.gatewayIp.equals(gatewayIp) && knxComObj.gatewayPort == port) {
                if (knxComObj.isConnected()) {
                    return knxComObj;
                } else {
                    iterator.remove();
                    break;
                }
            }
        }
        try {
            knxComObj = new KnxCommunicationObject(hostIp, gatewayIp, port);
            communicationObjects.add(knxComObj);
        } catch (UnknownHostException e) {
            knxComObj = null;
            throw e;
        }
        return knxComObj;
    }

    public Object[] getErrorObjects() {
        return errorContainer.getAll();
    }

    public Object[] getResultObjects() {
        return resultContainer.getAll();
    }

    private class ContinuousRead extends TimerTask {
        KnxComparableObject knxComparableObject;

        private ContinuousRead(KnxComparableObject knxObject) {
            this.knxComparableObject = knxObject;
        }

        @Override
        public void run() {
            KnxComparableObject newObj = null;
            if (knxComparableObject instanceof KnxControlObject) {
                newObj = new KnxControlObject(knxComparableObject.getGroupAddress(), true);
            } else if (knxComparableObject instanceof KnxBooleanObject) {
                newObj = new KnxBooleanObject(knxComparableObject.getGroupAddress(), true);
            } else if (knxComparableObject instanceof KnxFloatObject) {
                newObj = new KnxFloatObject(knxComparableObject.getGroupAddress(), true);
            }
            if (newObj != null) {
                taskContainer.push(newObj);
            }
        }
    }
}