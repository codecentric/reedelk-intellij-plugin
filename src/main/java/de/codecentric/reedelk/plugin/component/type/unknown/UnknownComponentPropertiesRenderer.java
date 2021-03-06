package de.codecentric.reedelk.plugin.component.type.unknown;

import de.codecentric.reedelk.plugin.editor.properties.commons.*;
import com.intellij.openapi.module.Module;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;
import de.codecentric.reedelk.plugin.component.ComponentData;
import de.codecentric.reedelk.plugin.editor.properties.renderer.AbstractComponentPropertiesRenderer;
import de.codecentric.reedelk.plugin.graph.FlowSnapshot;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.runtime.api.commons.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

import static de.codecentric.reedelk.plugin.message.ReedelkBundle.message;

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

        private final transient ComponentData componentData;

        public UnknownPropertiesContainer(ComponentData componentData) {
            super(JTabbedPane.LEFT);
            this.componentData = componentData;
            initialize();
        }

        private void initialize() {
            GenericTab genericTab = new GenericTab(() -> {
                DisposablePanel propertiesPanel = new DisposablePanel(new GridBagLayout());
                componentData.getPropertiesDescriptors().forEach(propertyDescriptor -> {
                    String displayName = propertyDescriptor.getDisplayName();
                    String propertyValue = componentData.get(propertyDescriptor.getName());

                    JBLabel label = new JBLabel(Optional.ofNullable(propertyValue).orElse(StringUtils.EMPTY));
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
