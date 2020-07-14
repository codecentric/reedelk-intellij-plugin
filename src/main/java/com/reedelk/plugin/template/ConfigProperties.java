package com.reedelk.plugin.template;

import java.util.Properties;

public class ConfigProperties extends Properties {

    public ConfigProperties(String id, String title) {
        this(id, title, "0.0.0.0", "8282");
    }

    public ConfigProperties(String id, String title, String host, String port) {
        put("id", id);
        put("title", title);
        put("host", "${HOST:" + host + "}");
        put("port", "${PORT:" + port + "}");
    }
}
