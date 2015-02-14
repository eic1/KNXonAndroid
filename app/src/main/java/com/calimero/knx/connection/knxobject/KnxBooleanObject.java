package com.calimero.knx.connection.knxobject;


import tuwien.auto.calimero.GroupAddress;

/**
 * Klasse, die einen Boolean repräsentiert. Also eine 1 oder 0. Zum beispiel für das Schalten von Lampen.
 */
public class KnxBooleanObject extends KnxComparableObject {
    private boolean value;

    /**
     * Konstruktor zum Erzeugen eines Objektes ohne Wert. Dieser Konstruktor sollte ausschließlich für lesende Aufträge verwendet werden.
     *
     * @param groupAddress GroupAddress von der gelesen werden soll.
     * @param read         boolean Angabe ob lesend (true) oder schreibend (false).
     */
    public KnxBooleanObject(GroupAddress groupAddress, boolean read) {
        super(groupAddress, read);
    }

    /**
     * Konstruktor zum Erzeugen eines Objektes mit Wert. Dieser Konstruktor sollte ausschließlich für schreibende Aufträge verwendet werden.
     * @param groupAddress GroupAddress von der gelesen werden soll.
     * @param value boolean Wert, der gesendet werden soll: Eine 1 bei true und 0 bei false.
     * @param read boolean Angabe ob lesend (true) oder schreibend (false).
     */
    public KnxBooleanObject(GroupAddress groupAddress, boolean value, boolean read) {
        super(groupAddress, read);
        this.value = value;
    }

    /**
     * Gibt den Wert des Objektes zurück.
     * @return boolean Wert des Objektes.
     */
    public boolean getValue() {
        return value;
    }

    /**
     * Setzt den Wert des Objektes.
     * @param value boolean Wert des Objektes.
     */
    public void setValue(boolean value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return super.toString() + " Value: " + value;
    }
}
