package de.codecentric.reedelk.plugin.service.module.impl.component.module;

import javax.swing.*;
import java.awt.*;

public class ModuleComponentDTO {

    private final Icon icon;
    private final Image image;
    private final String displayName;
    private final String fullyQualifiedName;
    private final boolean hidden;

    public ModuleComponentDTO(String fullyQualifiedName, String displayName, Image image, Icon icon, boolean hidden) {
        this.icon = icon;
        this.image = image;
        this.hidden = hidden;
        this.displayName = displayName;
        this.fullyQualifiedName = fullyQualifiedName;
    }

    public Icon getIcon() {
        return icon;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getFullyQualifiedName() {
        return fullyQualifiedName;
    }

    public boolean isHidden() {
        return hidden;
    }

    public Image getImage() {
        return image;
    }
}
