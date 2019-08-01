package com.esb.plugin.commons;

public class Half {

    private Half() {
    }

    public static int of(int value) {
        return Math.floorDiv(value, 2);
    }

    public static int of(double value) {
        return Math.floorDiv((int) Math.round(value), 2);
    }
}
