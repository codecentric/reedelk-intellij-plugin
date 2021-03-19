package de.codecentric.reedelk.plugin.editor.palette;

import java.io.Serializable;

public class PaletteComponent implements Serializable {

    private String componentFullyQualifiedName;

    public String getComponentFullyQualifiedName() {
        return componentFullyQualifiedName;
    }

    public void setComponentFullyQualifiedName(String componentFullyQualifiedName) {
        this.componentFullyQualifiedName = componentFullyQualifiedName;
    }
}
