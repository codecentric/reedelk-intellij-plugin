package com.esb.plugin.designer.editor;

public enum SystemComponents {

    FORK("com.esb.component.Fork"),
    CHOICE("com.esb.component.Choice"),
    FLOW_REFERENCE("com.esb.component.FlowReference");

    private final String qualifiedName;

    SystemComponents(String qualifiedName) {
        this.qualifiedName = qualifiedName;
    }

    public String qualifiedName() {
        return qualifiedName;
    }

}
