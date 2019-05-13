package com.esb.plugin.component.generic;

import com.esb.plugin.component.ComponentData;
import com.esb.plugin.designer.properties.AbstractPropertyRenderer;
import com.esb.plugin.designer.properties.widget.FormBuilder;
import com.esb.plugin.designer.properties.widget.PropertyInput;
import com.esb.plugin.graph.GraphSnapshot;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.ui.components.JBPanel;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;

public class GenericComponentPropertyRenderer extends AbstractPropertyRenderer {

    public GenericComponentPropertyRenderer(GraphSnapshot snapshot) {
        super(snapshot);
    }

    @Override
    public JBPanel render(GraphNode node) {
        ComponentData componentData = node.componentData();

        JBPanel propertiesListPanel = new JBPanel();
        propertiesListPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        propertiesListPanel.setLayout(new GridBagLayout());

        FormBuilder formBuilder = FormBuilder.get()
                .addLabel("Description", propertiesListPanel)
                .addLastField(new JTextField(), propertiesListPanel);


        componentData.descriptorProperties().forEach(propertyName -> {
            PropertyInput input = new PropertyInput();
            input.addListener(newText -> {
                componentData.set(propertyName.toLowerCase(), newText);
                snapshot.onDataChange();
            });
            formBuilder.addLabel(StringUtils.capitalize(propertyName), propertiesListPanel)
                    .addLastField(input, propertiesListPanel);
        });

        return propertiesListPanel;
    }

}
