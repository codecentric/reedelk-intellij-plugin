package com.esb.plugin.commons;

import java.util.UUID;

public class FlowIdGenerator {

    public static String next() {
        return UUID.randomUUID().toString();
    }
}
