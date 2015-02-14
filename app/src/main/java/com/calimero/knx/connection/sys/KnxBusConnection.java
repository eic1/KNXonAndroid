package com.calimero.knx.connection.sys;


import com.calimero.knx.connection.knxobject.KnxBooleanObject;
import com.calimero.knx.connection.knxobject.KnxComparableObject;
import com.calimero.knx.connection.knxobject.KnxControlObject;
import com.calimero.knx.connection.knxobject.KnxFloatObject;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Observable;

import tuwien.auto.calimero.GroupAddress;
import tuwien.auto.calimero.exception.KNXException;
import tuwien.auto.calimero.link.KNXNetworkLinkIP;
import tuwien.auto.calimero.link.medium.TPSettings;
import tuwien.auto.calimero.process.ProcessCommunicator;
import tuwien.auto.calimero.process.ProcessCommunicatorImpl;

class KnxBusConnection extends Observable implements Runnable {

    private final Container busActionContainer, resultContainer, errorContainer;
    private final InetSocketAddress hostSocket;
    private final InetSocketAddress gatewaySocket;
    private KNXNetworkLinkIP netLinkIp;
    private ProcessCommunicator processCommunicator;
    private boolean connected;
    private boolean terminated = false;

    public KnxBusConnection(String hostIp, String gatewayIp, int gatewayPort, Container busActionContainer, Container resultContainer, Container errorContainer) throws UnknownHostException {
        try {
            this.hostSocket = new InetSocketAddress(InetAddress.getByName(hostIp), 0);
        } catch (UnknownHostException e) {
            System.out.println("Host nicht gefunden");
            e.printStackTrace();
            throw e;
        }
        try {
            this.gatewaySocket = new InetSocketAddress(InetAddress.getByName(gatewayIp), gatewayPort);
        } catch (UnknownHostException e) {
            System.out.println("Gateway nicht gefunden");
            e.printStackTrace();
            throw e;
        }
        this.busActionContainer = busActionContainer;
        this.resultContainer = resultContainer;
        this.errorContainer = errorContainer;
    }

    @Override
    public void run() {
        KnxComparableObject object;
        GroupAddress groupAddress;
        int errorCount = 1;
        while (errorCount < 5) {
            if (!isConnected()) {
                initBus();
                if (isConnected()) {
                    System.out.println("Verbinden erfolgreich im Versuch " + errorCount);
                    errorCount = 1;
                } else {
                    System.out.println("Verbinden fehlgeschlagen im Versuch " + errorCount);
                    errorCount++;
                }
            } else {
                object = busActionContainer.pop();
                if (!object.isUnprocessable()) {
                    groupAddress = object.getGroupAddress();
                    if (object.isRead()) {
                        try {
                            if (object instanceof KnxFloatObject) {
                                System.out.println("Reading Float from Bus: " + object);
                                float read = processCommunicator.readFloat(groupAddress);
                                ((KnxFloatObject) object).setValue(read);
                            } else if (object instanceof KnxBooleanObject) {
                                System.out.println("Reading Boolean from Bus: " + object);
                                boolean read = processCommunicator.readBool(groupAddress);
                                ((KnxBooleanObject) object).setValue(read);
                            } else if (object instanceof KnxControlObject) {
                                System.out.println("Reading Control from Bus: " + object);
                                byte read = processCommunicator.readControl(groupAddress);
                                ((KnxControlObject) object).setValue(read);
                            }
                            resultContainer.push(object);
                        } catch (KNXException e) {
                            e.printStackTrace();
                            object.increaseErrors();
                            busActionContainer.push(object);
                            closeBus();
                        }
                    } else {
                        try {
                            if (object instanceof KnxBooleanObject) {
                                System.out.println("Writing Boolean to Bus: " + object);
                                processCommunicator.write(groupAddress, ((KnxBooleanObject) object).getValue());
                            } else if (object instanceof KnxFloatObject) {
                                System.out.println("Writing Float to Bus: " + object);
                                processCommunicator.write(groupAddress, ((KnxFloatObject) object).getValue());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            object.increaseErrors();
                            busActionContainer.push(object);
                            closeBus();
                        }
                    }
                } else {
                    System.out.println("Can not Process Object: " + object);
                    errorContainer.push(object);
                }
            }
        }
        terminated = true;
        closeBus();
    }

    private synchronized boolean initBus() {
        boolean result = false;
        try {
            netLinkIp = new KNXNetworkLinkIP(KNXNetworkLinkIP.TUNNEL, hostSocket, gatewaySocket, false, new TPSettings(false));
            processCommunicator = new ProcessCommunicatorImpl(netLinkIp);
            processCommunicator.setResponseTimeout(1);
            result = true;
        } catch (Exception e) {
            System.out.println(e.getClass().toString() + ", initBus(" + hostSocket.getAddress() + ", " + gatewaySocket.getAddress() + ")");
            e.printStackTrace();
        }
        return result;
    }

    private void closeBus() {
        if (netLinkIp != null) {
            netLinkIp.close();
            netLinkIp = null;
        }
        setConnected(false);
    }

    public synchronized boolean terminated() {
        return terminated;
    }

    private boolean isConnected() {
        boolean returnVal = false;
        if (netLinkIp != null) {
            returnVal = netLinkIp.isOpen();
        }
        setConnected(returnVal);
        return returnVal;
    }

    private void setConnected(boolean connected) {
        if (connected != this.connected) {
            this.setChanged();
        }
        System.out.println("setConnected: " + connected);
        this.connected = connected;
        this.notifyObservers((Boolean) connected);
    }
}