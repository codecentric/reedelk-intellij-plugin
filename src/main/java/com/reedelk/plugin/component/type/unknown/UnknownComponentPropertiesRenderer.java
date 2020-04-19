package com.reedelk.plugin.component.type.unknown;

import com.intellij.openapi.module.Module;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;
import com.reedelk.plugin.component.ComponentData;
import com.reedelk.plugin.editor.properties.commons.*;
import com.reedelk.plugin.editor.properties.renderer.AbstractComponentPropertiesRenderer;
import com.reedelk.plugin.graph.FlowSnapshot;
import com.reedelk.plugin.graph.node.GraphNode;

import javax.swing.*;
import java.awt.*;

import static com.reedelk.plugin.commons.Colors.PROPERTIES_TABS_BACKGROUND;
import static com.reedelk.plugin.message.ReedelkBundle.message;

public class UnknownComponentPropertiesRenderer extends AbstractComponentPropertiesRenderer {

    private static final int BORDER_TOP_BOTTOM = 5;

    public UnknownComponentPropertiesRenderer(FlowSnapshot snapshot, Module module) {
        super(snapshot, module);
    }

    @Override
    public JComponent render(GraphNode node) {

        ComponentData componentData = node.componentData();

        return new UnknownPropertiesContainer(componentData);
    }

    private static class UnknownPropertiesContainer extends DisposableTabbedPane {

        private final ComponentData componentData;

        public UnknownPropertiesContainer(ComponentData componentData) {
            super(JTabbedPane.LEFT);
            this.componentData = componentData;
            setBackground(PROPERTIES_TABS_BACKGROUND);
            initialize();
        }

        private void initialize() {
            GenericTab genericTab = new GenericTab(() -> {
                DisposablePanel propertiesPanel = new DisposablePanel(new GridBagLayout());
                componentData.getPropertiesDescriptors().forEach(propertyDescriptor -> {
                    String displayName = propertyDescriptor.getDisplayName();
                    String propertyValue = componentData.get(propertyDescriptor.getName());

                    JBLabel label = new JBLabel(propertyValue);
                    label.setBorder(JBUI.Borders.empty(BORDER_TOP_BOTTOM,0));
                    FormBuilder.get()
                            .addLabel(displayName, propertiesPanel)
                            .addLastField(label, propertiesPanel);
                });

                return propertiesPanel;
            });

            String defaultTabKey = message("properties.panel.tab.title.general");
            addTab(defaultTabKey, genericTab);
            setTabComponentAt(0, new TabLabelVertical(defaultTabKey));
        }
    }
}
