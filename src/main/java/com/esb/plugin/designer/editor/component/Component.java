package com.esb.plugin.designer.editor.component;

public class Component {

    private final String componentName;
    private String componentDescription;

    public Component(String componentName) {
        this.componentName = componentName;
    }

    public String getComponentName() {
        return componentName;
    }

    public String getComponentDescription() {
        return componentDescription;
    }

    public void setComponentDescription(String componentDescription) {
        this.componentDescription = componentDescription;
    }
}

