package com.reedelk.plugin.template;

import com.reedelk.plugin.commons.DefaultConstants;

import java.util.Properties;

public class RestListenerConfigProperties extends Properties {

    public RestListenerConfigProperties(String id, String title) {
        this(id, title, DefaultConstants.Template.DEFAULT_HOST, DefaultConstants.Template.DEFAULT_PORT);
    }

    public RestListenerConfigProperties(String id, String title, String host, int port) {
        put("id", id);
        put("title", title);
        put("host", "${HOST:" + host + "}");
        put("port", "${PORT:" + port + "}");
    }
}
