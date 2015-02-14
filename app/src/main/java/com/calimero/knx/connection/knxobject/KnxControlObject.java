package com.calimero.knx.connection.knxobject;

import tuwien.auto.calimero.GroupAddress;

/**
 * Klasse, die einen Knx-Control-Wert repräsentiert.
 */
public class KnxControlObject extends KnxComparableObject {
    private byte value;

    /**
     * Konstruktor zum Erzeugen eines Objektes ohne Wert. Dieser Konstruktor sollte ausschließlich für lesende Aufträge verwendet werden.
     *
     * @param groupAddress GroupAddress von der gelesen werden soll.
     * @param read         boolean Angabe ob lesend (true) oder schreibend (false).
     */
    public KnxControlObject(GroupAddress groupAddress, boolean read) {
        super(groupAddress, read);
    }

    /**
     * Konstruktor zum Erzeugen eines Objektes mit Wert. Dieser Konstruktor sollte ausschließlich für schreibende Aufträge verwendet werden.
     * @param groupAddress GroupAddress von der gelesen werden soll.
     * @param value byte Wert, der gesendet werden soll.
     * @param read boolean Angabe ob lesend (true) oder schreibend (false).
     */
    public KnxControlObject(GroupAddress groupAddress, byte value, boolean read) {
        super(groupAddress, read);
        this.value = value;
    }

    /**
     * Gibt den Wert des Objektes zurück.
     * @return byte Wert des Objektes.
     */
    public byte getValue() {
        return value;
    }

    /**
     * Setzt den Wert des Objektes.
     * @param value byte Wert des Objektes.
     */
    public void setValue(byte value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return super.toString() + " Value: " + value;
    }
}
