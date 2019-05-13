package com.esb.plugin.designer.properties.widget;

import com.esb.plugin.component.ComponentData;
import com.esb.plugin.graph.GraphSnapshot;
import com.intellij.ui.components.JBPanel;

import javax.swing.*;
import java.awt.*;

public class DefaultPropertiesPanel extends JBPanel {

    private final ComponentData componentData;
    private final GraphSnapshot snapshot;

    public DefaultPropertiesPanel(GraphSnapshot snapshot, ComponentData componentData) {
        super(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        this.componentData = componentData;
        this.snapshot = snapshot;

        addPropertyField("Description", "description");
    }

    public void addPropertyField(String displayName, String propertyName) {
        PropertyInput input = new PropertyInput();
        input.setText((String) componentData.get(propertyName));
        input.addListener(newText -> {
            componentData.set(propertyName, newText);
            snapshot.onDataChange();
        });

        FormBuilder.get()
                .addLabel(displayName, this)
                .addLastField(input, this);
    }
}
