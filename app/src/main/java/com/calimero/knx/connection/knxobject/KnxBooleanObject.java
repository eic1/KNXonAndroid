package com.calimero.knx.connection.knxobject;


import tuwien.auto.calimero.GroupAddress;

/**
 * Created by gerritwolff on 27.11.14.
 */
public class KnxBooleanObject extends KnxComparableObject {
    private boolean value;

    public KnxBooleanObject(GroupAddress groupAddress, boolean read) {
        super(groupAddress, read);
    }

    public KnxBooleanObject(GroupAddress groupAddress, boolean value, boolean read) {
        super(groupAddress, read);
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return super.toString() + " Value: " + value;
    }
}
