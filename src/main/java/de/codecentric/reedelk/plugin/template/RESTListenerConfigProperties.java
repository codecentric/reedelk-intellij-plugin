package de.codecentric.reedelk.plugin.template;

import java.util.Properties;

public class RESTListenerConfigProperties extends Properties {

    public void setId(String id) {
        setProperty("id", id);
    }

    public void setTitle(String title) {
        setProperty("title", title);
    }

    public void setHost(String host) {
        setProperty("host", "${HOST:" + host + "}");
    }

    public void setPort(int port) {
        setProperty("port", "${PORT:" + port + "}");
    }
}
