package com.esb.plugin.commons;

public class Half {
    public static int of(int value) {
        return Math.floorDiv(value, 2);
    }

    public static long of(double value) {
        return Math.round(Math.floor(value / 2d));
    }
}
