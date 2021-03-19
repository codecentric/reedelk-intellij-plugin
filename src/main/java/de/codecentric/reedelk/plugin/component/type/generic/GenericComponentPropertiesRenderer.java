package de.codecentric.reedelk.plugin.component.type.generic;

import com.intellij.openapi.module.Module;
import de.codecentric.reedelk.module.descriptor.model.property.PropertyDescriptor;
import de.codecentric.reedelk.plugin.component.ComponentData;
import de.codecentric.reedelk.plugin.editor.properties.commons.PropertiesPanelTabbedPanel;
import de.codecentric.reedelk.plugin.editor.properties.commons.PropertiesThreeComponentsSplitter;
import de.codecentric.reedelk.plugin.editor.properties.context.ContainerContext;
import de.codecentric.reedelk.plugin.editor.properties.renderer.AbstractComponentPropertiesRenderer;
import de.codecentric.reedelk.plugin.graph.FlowSnapshot;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;

import javax.swing.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static de.codecentric.reedelk.plugin.message.ReedelkBundle.message;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

public class GenericComponentPropertiesRenderer extends AbstractComponentPropertiesRenderer {

    public GenericComponentPropertiesRenderer(FlowSnapshot snapshot, Module module) {
        super(snapshot, module);
    }

    @Override
    public JComponent render(GraphNode node) {

        ComponentData componentData = node.componentData();

        List<PropertyDescriptor> propertiesDescriptors = componentData.getPropertiesDescriptors();

        String defaultTabKey = message("properties.panel.tab.title.general");

        Map<String, List<PropertyDescriptor>> propertiesByGroup = propertiesDescriptors.stream()
                .collect(groupingBy(propertyDescriptor -> ofNullable(propertyDescriptor.getGroup()).orElse(defaultTabKey),
                        LinkedHashMap::new, toList()));

        String componentFullyQualifiedName = componentData.getFullyQualifiedName();

        ContainerContext context = new ContainerContext(snapshot, node, componentFullyQualifiedName);

        PropertiesPanelTabbedPanel panel = new PropertiesPanelTabbedPanel(module, componentData, propertiesByGroup, context);

        return new PropertiesThreeComponentsSplitter(module, context, panel, context);
    }
}
