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

/**
 * Klasse, die für die Kommunikation mit dem KnxBus verwendet wird.
 */
class KnxBusConnection extends Observable implements Runnable {

    private final Container busActionContainer, resultContainer, errorContainer;
    private final InetSocketAddress hostSocket;
    private final InetSocketAddress gatewaySocket;
    private KNXNetworkLinkIP netLinkIp;
    private ProcessCommunicator processCommunicator;
    private boolean connected;
    private boolean terminated = false;

    /**
     * Konstruktor der Klasse. Es werden die Angaben übergeben, die für den Aufbau einer Verbindung zu einem Knx-Bus benötigt werden.
     *
     * @param hostIp             String IP des Geräts.
     * @param gatewayIp          String IP des Knx-IP-Interfaces.
     * @param gatewayPort        int Port auf dem das IP-Interface Verbindungen zulässt.
     * @param busActionContainer Container Ein Container, der Lese- und Schreibaufträge enthält.
     * @param resultContainer    Container Ein Container, der die Ergebnisse von Leseaufträgen am Bus enthält.
     * @param errorContainer     Container Ein Container, der die fehlgeschlagenen Aufträge enthält.
     * @throws UnknownHostException
     */
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

    /**
     * Methode zum initialisieren der Bus-Verbindung. Es wird versucht eine Verbindung zu einem Knx-Bus aufzubauen.
     *
     * @return boolean true wenn eine Verbindung aufgebaut werden konnte, ansonsten false.
     */
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

    /**
     * Beenden der Verbindung zum Bus. Bestehende Verbindungen werden ggf. getrennt und Ressourcen freigegeben.
     */
    private void closeBus() {
        if (netLinkIp != null) {
            netLinkIp.close();
            netLinkIp = null;
        }
        setConnected(false);
    }

    /**
     * Abfrage, ob sich der Thread terminiert hat. Bei einer zu hohen Zahl von Fehlern beendet sich der Thread selbst.
     *
     * @return boolean true falls der Thread sich terminiert hat, ansonsten false.
     */
    public synchronized boolean terminated() {
        return terminated;
    }

    /**
     * Überprüfung ob eine Verbindung zum Knx-Bus besteht.
     *
     * @return boolean true falls eine Verbindung besteht, ansonsten false.
     */
    private boolean isConnected() {
        boolean returnVal = false;
        if (netLinkIp != null) {
            returnVal = netLinkIp.isOpen();
        }
        setConnected(returnVal);
        return returnVal;
    }

    /**
     * Methode zum setzen des connected Flags. Wird benötigt, um bei eventuellen Wechseln des Status eine Benachrichtigung via Observer-Pattern auszuführen.
     *
     * @param connected boolean Status der Verbindung. true wenn Verbindung besteht ansonsten false.
     */
    private void setConnected(boolean connected) {
        if (connected != this.connected) {
            this.setChanged();
        }
        System.out.println("setConnected: " + connected);
        this.connected = connected;
        this.notifyObservers((Boolean) connected);
    }
}