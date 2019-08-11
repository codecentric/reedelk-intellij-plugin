package com.reedelk.plugin.service.module.impl;

public class SubflowMetadata {

    private final String id;
    private final String title;

    public SubflowMetadata(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}
