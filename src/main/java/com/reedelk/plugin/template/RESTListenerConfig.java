package com.reedelk.plugin.template;

import java.util.Properties;

public class RESTListenerConfig extends Properties {

    public void setId(String id) {
        put("id", id);
    }

    public void setTitle(String title) {
        put("title", title);
    }

    public void setHost(String host) {
        put("host", "${HOST:" + host + "}");
    }

    public void setPort(int port) {
        put("port", "${PORT:" + port + "}");
    }
}
