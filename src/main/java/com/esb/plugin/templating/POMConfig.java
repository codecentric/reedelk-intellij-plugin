package com.esb.plugin.templating;

import freemarker.template.Configuration;

public enum POMConfig {

    INSTANCE;

    private Configuration cfg;

    POMConfig() {
        cfg = new Configuration();
        cfg.setClassForTemplateLoading(POMTemplate.class, "/com/esb/plugin/templating/");
        cfg.setDefaultEncoding("UTF-8");

    }

    public static Configuration get() {
        return INSTANCE.cfg;
    }
}
