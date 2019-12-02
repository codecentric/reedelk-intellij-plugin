package com.reedelk.plugin.template;

import java.util.Properties;

public class ConfigProperties extends Properties {

    public ConfigProperties(String id, String title) {
        put("id", id);
        put("title", title);
    }
}
