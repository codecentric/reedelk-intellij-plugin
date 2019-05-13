package com.esb.plugin.designer.properties.widget;

import com.esb.plugin.component.ComponentData;
import com.esb.plugin.converter.PropertyValueConverter;
import com.esb.plugin.converter.PropertyValueConverterFactory;
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
    }

    public void addPropertyField(String displayName, String propertyName) {
        PropertyInput input = new PropertyInput();

        Class<?> propertyType = componentData.getPropertyType(propertyName);
        PropertyValueConverter<?> converter = PropertyValueConverterFactory.forType(propertyType);

        Object propertyValue = componentData.get(propertyName);
        String valueAsString = converter.to(propertyValue);

        input.setText(valueAsString);
        input.addListener(newText -> {
            componentData.set(propertyName, converter.from(newText));
            snapshot.onDataChange();
        });

        FormBuilder.get()
                .addLabel(displayName, this)
                .addLastField(input, this);
    }

}
