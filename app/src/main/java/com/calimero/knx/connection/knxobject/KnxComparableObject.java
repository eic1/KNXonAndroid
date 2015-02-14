package com.calimero.knx.connection.knxobject;


import java.util.Date;

import tuwien.auto.calimero.GroupAddress;

/**
 * Klasse die einen Auftrag oder das Ergebnis für das Senden oder Schreiben auf dem Knx-Bus repräsentiert. Bietet Funktionen zum vergleichen verschiedener Aufträge.
 */
public class KnxComparableObject implements Comparable<KnxComparableObject> {
    private final Date createDate;
    private final GroupAddress groupAddress;
    private final boolean read;
    private boolean unprocessable = false;

    private int errors = 0;

    /**
     * Konstruktor der Klasse. Es wird ein neues Objekt erstellt. Dabei wird das aktuelle Datum abgefragt und im Objekt hinterlegt.
     *
     * @param groupAddress GroupAddress Die Gruppen Adresse auf die der Auftrag ausgeführt werden soll.
     * @param read         boolean Angabe ob lesend (true) oder schreibend (false).
     */
    public KnxComparableObject(GroupAddress groupAddress, boolean read) {
        this.createDate = new Date();
        this.groupAddress = groupAddress;
        this.read = read;
    }

    @Override
    public int compareTo(KnxComparableObject another) {
        //Das ältere Objekt wird als kleiner angenommen (wird von Collection.sort() an den Anfang der Liste gestellt)
        int out = 0;
        if (this.createDate.before(another.createDate)) {
            out = -1;
        } else if (this.createDate.after(another.createDate)) {
            out = 1;
        }
        return out;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof KnxComparableObject && ((KnxComparableObject) o).groupAddress.equals(this.groupAddress) && ((KnxComparableObject) o).isRead() == this.isRead();
    }

    /**
     * Gibt das Datum der Erstellung zurück.
     *
     * @return Date das Erstellungsdatum.
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * Gibt die Gruppen Adresse zurück.
     *
     * @return GroupAddress die Gruppen Adresse des Objektes.
     */
    public GroupAddress getGroupAddress() {
        return groupAddress;
    }

    /**
     * Gibt an, ob es sich um einen lesenden Auftrag handelt
     *
     * @return boolean true falls lesend, false wenn schreibend.
     */
    public boolean isRead() {
        return read;
    }

    @Override
    public String toString() {
        return "Date: " + createDate + " GroupAddress: " + groupAddress + " isRead: " + read;
    }

    /**
     * Gibt die Anzahl der fehlgeschlagenen Verarbeitungs-Versuche an.
     *
     * @return int Anzahl der Fehler.
     */
    public int getErrors() {
        return errors;
    }

    /**
     * Erhöht den Fehlerzähler um eins und setzt ggf. ein Flag was das Objekt als nicht prozessierbar markiert.
     */
    public void increaseErrors() {
        ++this.errors;
        if (errors == 3) {
            unprocessable = true;
        }
    }

    /**
     * Abfrage, ob das Objekt nicht verarbeitet werden konnte.
     *
     * @return boolean true, falls das Objekt nciht verarbeitet werden kann, ansonsten false
     */
    public boolean isUnprocessable() {
        return unprocessable;
    }
}

