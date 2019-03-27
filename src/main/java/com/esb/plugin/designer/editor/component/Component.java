package com.esb.plugin.designer.editor.component;

import java.util.HashMap;
import java.util.Map;

public class Component {

    private final String name;
    private final Map<String, Object> properties = new HashMap<>();
    private String description;

    public Component(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

