package com.calimero.knx.connection.knxobject;

import tuwien.auto.calimero.GroupAddress;

/**
 * Created by gerritwolff on 27.11.14.
 */
public class KnxControlObject extends KnxComparableObject {
    private byte value;

    public KnxControlObject(GroupAddress groupAddress, boolean read) {
        super(groupAddress, read);
    }

    public KnxControlObject(GroupAddress groupAddress, byte value, boolean read) {
        super(groupAddress, read);
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public void setValue(byte value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return super.toString() + " Value: " + value;
    }
}
