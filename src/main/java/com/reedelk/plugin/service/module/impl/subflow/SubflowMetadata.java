package com.reedelk.plugin.service.module.impl.subflow;

public class SubflowMetadata {

    private final String id;
    private final String title;
    private final String fileURL;

    public SubflowMetadata(String id, String title) {
        this(id, title, null);
    }

    public SubflowMetadata(String id, String title, String fileURL) {
        this.id = id;
        this.title = title;
        this.fileURL = fileURL;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getFileURL() {
        return fileURL;
    }
}
