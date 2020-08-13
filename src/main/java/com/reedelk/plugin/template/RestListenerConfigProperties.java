package com.reedelk.plugin.template;

import java.util.Properties;

public class RestListenerConfigProperties extends Properties {

    private static final String DEFAULT_HOST = "0.0.0.0";
    private static final int DEFAULT_PORT = 8282;

    public RestListenerConfigProperties(String id, String title) {
        this(id, title, DEFAULT_HOST, DEFAULT_PORT);
    }

    public RestListenerConfigProperties(String id, String title, String host, int port) {
        put("id", id);
        put("title", title);
        put("host", "${HOST:" + host + "}");
        put("port", "${PORT:" + port + "}");
    }
}
