package com.calimero.knx.connection.knxobject;

import tuwien.auto.calimero.GroupAddress;

/**
 * Klasse, die einen Float repräsentiert. Also eine Gleitkommazahl. Zum Beispiel für das Abfaragen der Wetterstation.
 */
public class KnxFloatObject extends KnxComparableObject {
    private float value;

    /**
     * Konstruktor zum Erzeugen eines Objektes ohne Wert. Dieser Konstruktor sollte ausschließlich für lesende Aufträge verwendet werden.
     *
     * @param groupAddress GroupAddress von der gelesen werden soll.
     * @param read         boolean Angabe ob lesend (true) oder schreibend (false).
     */
    public KnxFloatObject(GroupAddress groupAddress, boolean read) {
        super(groupAddress, read);
    }

    /**
     * Konstruktor zum Erzeugen eines Objektes mit Wert. Dieser Konstruktor sollte ausschließlich für schreibende Aufträge verwendet werden.
     * @param groupAddress GroupAddress von der gelesen werden soll.
     * @param value float Wert, der gesendet werden soll.
     * @param read boolean Angabe ob lesend (true) oder schreibend (false).
     */
    public KnxFloatObject(GroupAddress groupAddress, float value, boolean read) {
        super(groupAddress, read);
        this.value = value;
    }

    /**
     * Gibt den Wert des Objektes zurück.
     * @return float Wert des Objektes.
     */
    public float getValue() {
        return value;
    }

    /**
     * Setzt den Wert des Objektes.
     * @param value float Wert des Objektes.
     */
    public void setValue(float value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return super.toString() + " Value: " + value;
    }
}
