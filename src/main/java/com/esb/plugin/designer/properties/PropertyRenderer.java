package com.esb.plugin.designer.properties;

import com.esb.plugin.component.Component;
import com.intellij.ui.components.JBPanel;

public interface PropertyRenderer {

    void render(JBPanel panel, Component component);

}
