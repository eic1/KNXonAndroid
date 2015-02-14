package com.calimero.knx.connection.sys;

import com.calimero.knx.connection.knxobject.KnxComparableObject;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

import tuwien.auto.calimero.GroupAddress;

/**
 * Container zum halten von Aufträgen, die über den Knx-Bus verarbeitet werden sollen/wurden.
 */
class Container extends Observable {
    private List<KnxComparableObject> objects = new LinkedList<KnxComparableObject>();

    /**
     * Fügt ein Objekt dem Container hinzu. Dabei werden ältere Objekte, die gleiche Adresse betreffend ggf. gelöscht und durch das neuere Objekt ersetzt. Die Objekte im Container werden nach erstellungsdatum sortiert. Ältere Objekte werden zuerst bearbeitet (FiFo).
     *
     * @param object KnxComparableObject Das Objekt was in den Container eingefügt werden soll
     */
    public synchronized void push(KnxComparableObject object) {
        int index = objects.indexOf(object);
        if (index == -1) {
            objects.add(object);
            System.out.println("Push: Added: " + object);
            Collections.sort(objects);
            setChanged();
            notifyObservers(object);
        } else {
            if (object.compareTo(objects.get(index)) == 1) {
                KnxComparableObject removedObject = objects.remove(index);
                System.out.println("Push: Removed: " + removedObject);
                objects.add(object);
                System.out.println("Push: Added: " + object);
                Collections.sort(objects);
                setChanged();
                notifyObservers(object);
            }
        }
        notifyAll();
    }

    /**
     * Gibt das älteste Objekt aus dem Container zurück und entfernt es dann.
     *
     * @return KnxComparableObject das älteste Objekt aus dem Container
     */
    public synchronized KnxComparableObject pop() {
        while (objects.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
        KnxComparableObject object = objects.remove(0);
        System.out.println("Pop: Removed: " + object);
        notifyAll();
        return object;
    }

    /**
     * Gibt alle Objekte im Container zurück. Der Container wird dabei nicht verändert.
     *
     * @return Object[] ein Array von Objekten, dass den Inhalt des Container repräsentiert.
     */
    public synchronized Object[] getAll() {
        return objects.toArray();
    }

    /**
     * Gibt ein Objekt zu einer bestimmten Knx-Adresse aus dem Container zurück.
     *
     * @param groupAddress GroupAddress die Gruppen Adresse für die das Objekt geliefert werden soll.
     * @param read         boolean Information,ob es sich um einen lesenden (true) Auftrag oder einen schreibenden (false) handelt.
     * @return KnxComparableObject Das Objekt, welches für die Angaben gefunden wurde. Wenn kein Wert gefunden wurde wird null zurückgegeben.
     */
    public synchronized KnxComparableObject getByGroupAddress(GroupAddress groupAddress, boolean read) {
        int objectInd = objects.indexOf(new KnxComparableObject(groupAddress, read));
        KnxComparableObject object;
        if (objectInd != -1) {
            object = objects.get(objectInd);
            System.out.println("GetByGroupAddress: Return: " + object);
        } else {
            object = null;
        }
        notifyAll();
        return object;
    }

    /**
     * Abfrage, ob der Container leer ist.
     *
     * @return boolean true, falls der Container leer ist, ansonsten false.
     */
    public synchronized boolean isEmpty() {
        return objects.isEmpty();
    }
}
