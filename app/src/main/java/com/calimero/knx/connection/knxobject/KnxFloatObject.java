package com.calimero.knx.connection.knxobject;

import tuwien.auto.calimero.GroupAddress;

/**
 * Created by gerritwolff on 27.11.14.
 */
public class KnxFloatObject extends KnxComparableObject {
    private float value;

    public KnxFloatObject(GroupAddress groupAddress, boolean read) {
        super(groupAddress, read);
    }

    public KnxFloatObject(GroupAddress groupAddress, float value, boolean read) {
        super(groupAddress, read);
        this.value = value;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return super.toString() + " Value: " + value;
    }
}
