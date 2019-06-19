package com.esb.plugin.service.module.impl;

public class ConfigMetadata {

    private final String fullyQualifiedName;
    private final String title;
    private final String id;

    public ConfigMetadata(final String id, String title) {
        this(null, id, title);
    }

    public ConfigMetadata(final String fullyQualifiedName, String id, String title) {
        this.fullyQualifiedName = fullyQualifiedName;
        this.title = title;
        this.id = id;
    }

    public String getFullyQualifiedName() {
        return fullyQualifiedName;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}
