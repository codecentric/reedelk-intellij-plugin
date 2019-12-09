package com.reedelk.plugin.service.module.impl.script;

public class ScriptResource {

    private final String path;
    private final String displayName;

    public ScriptResource(String path, String displayName) {
        this.path = path;
        this.displayName = displayName;
    }

    public String getPath() {
        return path;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isEditable() {
        return true;
    }

    public boolean isRemovable() {
        return true;
    }
}
