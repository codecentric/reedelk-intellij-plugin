package com.esb.plugin.component.fork;

import com.esb.plugin.designer.properties.AbstractPropertyRenderer;
import com.esb.plugin.designer.properties.widget.FormBuilder;
import com.esb.plugin.graph.GraphSnapshot;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.ui.components.JBPanel;

import javax.swing.*;
import java.awt.*;

public class ForkPropertyRenderer extends AbstractPropertyRenderer {

    public ForkPropertyRenderer(GraphSnapshot snapshot) {
        super(snapshot);
    }

    @Override
    public JBPanel render(GraphNode node) {
        JBPanel propertiesListPanel = new JBPanel();
        propertiesListPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        propertiesListPanel.setLayout(new GridBagLayout());
        FormBuilder.get()
                .addLabel("Description", propertiesListPanel)
                .addLastField(new JTextField(), propertiesListPanel)
                .addLabel("Thread Pool Size", propertiesListPanel)
                .addLastField(new JTextField(), propertiesListPanel);

        return propertiesListPanel;
    }

}
