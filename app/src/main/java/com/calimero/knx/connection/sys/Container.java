package com.calimero.knx.connection.sys;

import com.calimero.knx.connection.knxobject.KnxComparableObject;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

import tuwien.auto.calimero.GroupAddress;

class Container extends Observable {
    private List<KnxComparableObject> objects = new LinkedList<KnxComparableObject>();

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

    public synchronized Object[] getAll() {
        return objects.toArray();
    }

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

    public synchronized boolean isEmpty() {
        return objects.isEmpty();
    }
}
