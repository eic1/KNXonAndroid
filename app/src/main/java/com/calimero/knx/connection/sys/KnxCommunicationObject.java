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
 * Klasse zum Aufbau einer Verbindung mit einem KnxBus. Diese Klasse bietet alle Funktionen zum Lesen und Schreiben des KnxBus.
 */
public class KnxCommunicationObject extends Observable implements Observer {
    private final KnxBusConnection knxBusConnection;
    private final Container taskContainer;
    private final Container resultContainer;
    private final Container errorContainer;
    private final String hostIp, gatewayIp;
    private final int gatewayPort;
    private final Timer timer = new Timer();
    private final List<ContinuousRead> timerTaskList = new LinkedList<ContinuousRead>();

    private final static List<KnxCommunicationObject> communicationObjects = new LinkedList<KnxCommunicationObject>();

    /**
     * Dummy Konstructor um den standard Konstruktor zu verbergen. Sollte niemals aufgerufen werden.
     */
    private KnxCommunicationObject() {
        //Initalisierung damit Kompilieren möglich ist.
        knxBusConnection = null;
        taskContainer = null;
        resultContainer = null;
        errorContainer = null;
        hostIp = null;
        gatewayIp = null;
        gatewayPort = 0;
    }

    /**
     * Konstruktor zum erstellen eines Objektes zur Kommunikation mit einem KnxBus.
     *
     * @param hostIp      String Ip des Gerätes, ass mit dem KnxBus verbunden werden soll.
     * @param gatewayIp   String Ip des KnxIpInterfaces.
     * @param gatewayPort int Port am KnxIpInterface, der verwendet werden soll.
     * @throws UnknownHostException Exception falls Host unbekannt.
     */
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

    /**
     * Sendet einen Boolean Wert an eine Gruppen Adresse. Mit dieser Methode kann ein Boolean an ein Knx-Gerät übertragen werden.
     *
     * @param groupAddress GroupAddress Die Gruppen Adresse des Ziels.
     * @param value        boolean Der Wert, der versendet werden soll.
     */
    public void writeBoolean(GroupAddress groupAddress, boolean value) {
        taskContainer.push(new KnxBooleanObject(groupAddress, value, false));
    }

    /**
     * Sendet einen Float Wert an eine Gruppen Adresse. Mit dieser Methode kann ein Float an ein Knx-Gerät übertragen werden.
     *
     * @param groupAddress GroupAddress Die Gruppen Adresse des Ziels.
     * @param value        float Der Wert, der versendet werden soll.
     */
    public void writeFloat(GroupAddress groupAddress, float value) {
        taskContainer.push(new KnxFloatObject(groupAddress, value, false));
    }

    /**
     * Liest eine Status Adresse. Diese Methode versucht von einer Gruppen Adresse einen Boolean Wert zu lesen.
     * Dabei ist zu beachten, dass die Gruppen Adresse eine abfragbare Adresse sein muss. Sobald das Lesen erfolgt ist,
     * wird das Ergebnis den Observern des Objektes, mit dem das lesen erfolgte, mitgeteilt.
     *
     * @param groupAddress GroupAddress Die Gruppen Adresse, die gelesen werden soll.
     */
    public void readBoolean(GroupAddress groupAddress) {
        taskContainer.push(new KnxBooleanObject(groupAddress, true));
    }

    /**
     * Liest eine Status Adresse. Diese Methode versucht von einer Gruppen Adresse einen Float Wert zu lesen.
     * Dabei ist zu beachten, dass die Gruppen Adresse eine abfragbare Adresse sein muss. Sobald das Lesen erfolgt ist,
     * wird das Ergebnis den Observern des Objektes, mit dem das lesen erfolgte, mitgeteilt.
     *
     * @param groupAddress GroupAddress Die Gruppen Adresse, die gelesen werden soll.
     */
    public void readFloat(GroupAddress groupAddress) {
        taskContainer.push(new KnxFloatObject(groupAddress, true));
    }

    /**
     * Liest eine Status Adresse. Diese Methode versucht von einer Gruppen Adresse einen Control Wert zu lesen.
     * Dabei ist zu beachten, dass die Gruppen Adresse eine abfragbare Adresse sein muss. Sobald das Lesen erfolgt ist,
     * wird das Ergebnis den Observern des Objektes, mit dem das lesen erfolgte, mitgeteilt.
     *
     * @param groupAddress GroupAddress Die Gruppen Adresse, die gelesen werden soll.
     */
    public void readControl(GroupAddress groupAddress) {
        taskContainer.push(new KnxControlObject(groupAddress, true));
    }

    /**
     * Liest eine Status Adresse periodisch. Das Lesen erfolg in einem vorgegebenen Interval. Für weitere Imformationen  siehe readBoolean(GroupAddress groupAddress).
     *
     * @param groupAddress GroupAddress Die Gruppen Adresse, die gelesen werden soll.
     * @param refreshRate  Zeit in Milliesekunden zwischen zwei Lesevorgängen.
     */
    public void readPeriodicBoolean(GroupAddress groupAddress, int refreshRate) {
        ContinuousRead timerTask = new ContinuousRead(new KnxBooleanObject(groupAddress, true));
        timer.schedule(timerTask, 0, refreshRate);
        timerTaskList.add(timerTask);
    }

    /**
     * Liest eine Status Adresse periodisch. Das Lesen erfolg in einem vorgegebenen Interval. Für weitere Imformationen  siehe readFloat(GroupAddress groupAddress).
     *
     * @param groupAddress GroupAddress Die Gruppen Adresse, die gelesen werden soll.
     * @param refreshRate  Zeit in Milliesekunden zwischen zwei Lesevorgängen.
     */
    public void readPeriodicFloat(GroupAddress groupAddress, int refreshRate) {
        ContinuousRead timerTask = new ContinuousRead(new KnxFloatObject(groupAddress, true));
        timer.schedule(timerTask, 0, refreshRate);
        timerTaskList.add(timerTask);
    }

    /**
     * Liest eine Status Adresse periodisch. Das Lesen erfolg in einem vorgegebenen Interval. Für weitere Imformationen  siehe readControl(GroupAddress groupAddress).
     *
     * @param groupAddress GroupAddress Die Gruppen Adresse, die gelesen werden soll.
     * @param refreshRate  Zeit in Milliesekunden zwischen zwei Lesevorgängen.
     */
    public void readPeriodicControl(GroupAddress groupAddress, int refreshRate) {
        ContinuousRead timerTask = new ContinuousRead(new KnxControlObject(groupAddress, true));
        timer.schedule(timerTask, 0, refreshRate);
        timerTaskList.add(timerTask);
    }

    /**
     * Stoppt das periodische Lesen für eine bestimmte Gruppen Adresse. Alle periodischen Lesevorgänge für die gegebene Gruppen Adresse werden abgebrochen.
     *
     * @param groupAddress GroupAddress Die Gruppen Adresse für die das Lesen abgebrochen werden soll.
     */
    public void cancelPeriodicRead(GroupAddress groupAddress) {
        Iterator<ContinuousRead> iterator = timerTaskList.iterator();
        ContinuousRead task = null;
        while (iterator.hasNext()) {
            task = iterator.next();
            if (task.getGroupAddress().equals(groupAddress)) {
                task.cancel();
            }
            iterator.remove();
        }
        timer.purge();
    }

    /**
     * Versucht einen Boolean Wert aus einem Container mit allen Ergebnissen von Lesevorgängen zu beschaffen.
     * Dabei wird direkt ein Ergebnis geliefert und es ist nicht die Verwendung des Observers notwendig.
     *
     * @param groupAddress GroupAddress Die Gruppen Adresse des Ziels.
     * @return boolean Der letzte Wert, der von der übergebenen Adresse geliefert wurde.
     * @throws NotInResultsException Eine Exception wird geworfen, falls kein Wert für die Adresse vorliegt.
     */
    public boolean readBooleanFromResults(GroupAddress groupAddress) throws NotInResultsException {
        KnxComparableObject knxBoolean = resultContainer.getByGroupAddress(groupAddress, true);
        if (knxBoolean != null && knxBoolean instanceof KnxBooleanObject) {
            return ((KnxBooleanObject) knxBoolean).getValue();
        } else {
            throw new NotInResultsException();
        }
    }

    /**
     * Versucht einen Float Wert aus einem Container mit allen Ergebnissen von Lesevorgängen zu beschaffen.
     * Dabei wird direkt ein Ergebnis geliefert und es ist nicht die Verwendung des Observers notwendig.
     *
     * @param groupAddress GroupAddress Die Gruppen Adresse des Ziels.
     * @return float Der letzte Wert, der von der übergebenen Adresse geliefert wurde.
     * @throws NotInResultsException Eine Exception wird geworfen, falls kein Wert für die Adresse vorliegt.
     */
    public float readFloatFromResults(GroupAddress groupAddress) throws NotInResultsException {
        KnxComparableObject knxFloat = resultContainer.getByGroupAddress(groupAddress, true);
        if (knxFloat != null && knxFloat instanceof KnxFloatObject) {
            return ((KnxFloatObject) knxFloat).getValue();
        } else {
            throw new NotInResultsException();
        }
    }

    /**
     * Versucht einen Control Wert aus einem Container mit allen Ergebnissen von Lesevorgängen zu beschaffen.
     * Dabei wird direkt ein Ergebnis geliefert und es ist nicht die Verwendung des Observers notwendig.
     *
     * @param groupAddress GroupAddress Die Gruppen Adresse des Ziels.
     * @return byte Der letzte Wert, der von der übergebenen Adresse geliefert wurde.
     * @throws NotInResultsException Eine Exception wird geworfen, falls kein Wert für die Adresse vorliegt.
     */
    public byte readControlFromResults(GroupAddress groupAddress) throws NotInResultsException {
        KnxComparableObject knxControl = resultContainer.getByGroupAddress(groupAddress, true);
        if (knxControl != null && knxControl instanceof KnxControlObject) {
            return ((KnxControlObject) knxControl).getValue();
        } else {
            throw new NotInResultsException();
        }
    }

    /**
     * Gibt an, ob eine Verbindung zum KNxBus besteht.
     *
     * @return boolean true falls eine Verbindung existiert.
     */
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

    /**
     * Gibt ein Objekt zurück, dass einer Verbindung zu einem KnxBus entspricht. Es kann immer nur eine Verbindung zu einem Bus geben,
     * so dass hier das Singelton-Pattern verwendet wird. Sollte bereits eine Verbindung zu einem Bus mit den übergebenen Parametern existiert wird diese zurückgegeben.
     * Andernfalls wird ein neues erstellt.
     *
     * @param hostIp    String Ip des Gerätes, ass mit dem KnxBus verbunden werden soll.
     * @param gatewayIp String Ip des KnxIpInterfaces.
     * @return KnxCommunicationObject ein Objekt, dass eine Verbindung zu dem Bus mit den übergebenen Parametern darstellt.
     * @throws UnknownHostException Exception falls Host unbekannt.
     */
    public static KnxCommunicationObject getInstance(String hostIp, String gatewayIp) throws UnknownHostException {
        return getInstance(hostIp, gatewayIp, KNXnetIPConnection.IP_PORT);
    }

    /**
     * Gibt ein Objekt zurück, dass einer Verbindung zu einem KnxBus entspricht. Es kann immer nur eine Verbindung zu einem Bus geben,
     * so dass hier das Singelton-Pattern verwendet wird. Sollte bereits eine Verbindung zu einem Bus mit den übergebenen Parametern existiert wird diese zurückgegeben.
     * Andernfalls wird ein neues erstellt.
     *
     * @param hostIp    String Ip des Gerätes, ass mit dem KnxBus verbunden werden soll.
     * @param gatewayIp String Ip des KnxIpInterfaces.
     * @param port      int Port am KnxIpInterface, der verwendet werden soll.
     * @return KnxCommunicationObject ein Objekt, dass eine Verbindung zu dem Bus mit den übergebenen Parametern darstellt.
     * @throws UnknownHostException Exception falls Host unbekannt.
     */
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

    /**
     * Gibt ein Array mit allen Objekten zurück bei denen ein Fehler aufgetreten ist. Bei den Objekten handelt es sich um welche der Klasse KnxComparableObjekt.
     *
     * @return Object[] Die KnxComparableObjekts bei deren Verarbeitung ein Fehler auftrat.
     */
    public Object[] getErrorObjects() {
        return errorContainer.getAll();
    }

    /**
     * Gibt ein Array mit allen Objekten zurück für die erfolgreich ein Wert gelesen wurde. Bei den Objekten handelt es sich um welche der Klasse KnxComparableObjekt.
     *
     * @return Object[] Die KnxComparableObjekts für die ein Wert erfolgreich vom KnxBus gelesn wurde. Es wird immer nur das Objekt mit dem neusten Wert für eine Adresse zurückgegeben.
     */
    public Object[] getResultObjects() {
        return resultContainer.getAll();
    }

    /**
     * Private Klasse zum Realisieren des periodischen Lesens von Werten.
     */
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

        public GroupAddress getGroupAddress() {
            return knxComparableObject.getGroupAddress();
        }
    }
}