package com.esb.plugin.component.choice.widget;

import com.esb.plugin.graph.node.GraphNode;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.ListCellRendererWrapper;

import javax.swing.*;

import static com.esb.plugin.component.ComponentData.DESCRIPTION_PROPERTY_NAME;


public class RouteComboBox extends ComboBox<GraphNode> {

    public RouteComboBox() {
        setRenderer(new RouteComboBoxRenderer());
    }

    class RouteComboBoxRenderer extends ListCellRendererWrapper<GraphNode> {
        @Override
        public void customize(JList list, GraphNode node, int index, boolean selected, boolean hasFocus) {
            String description = (String) node.component().get(DESCRIPTION_PROPERTY_NAME);
            setText(description);
        }
    }
}
