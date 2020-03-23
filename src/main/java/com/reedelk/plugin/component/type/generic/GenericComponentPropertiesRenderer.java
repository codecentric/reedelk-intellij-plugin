package com.reedelk.plugin.component.type.generic;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.PropertyDescriptor;
import com.reedelk.plugin.component.ComponentData;
import com.reedelk.plugin.editor.properties.commons.PropertiesPanelTabbedPanel;
import com.reedelk.plugin.editor.properties.renderer.AbstractComponentPropertiesRenderer;
import com.reedelk.plugin.graph.FlowSnapshot;
import com.reedelk.plugin.graph.node.GraphNode;

import javax.swing.*;
import java.util.*;

import static com.reedelk.plugin.message.ReedelkBundle.message;

public class GenericComponentPropertiesRenderer extends AbstractComponentPropertiesRenderer {

    public GenericComponentPropertiesRenderer(FlowSnapshot snapshot, Module module) {
        super(snapshot, module);
    }

    @Override
    public JComponent render(GraphNode node) {

        ComponentData componentData = node.componentData();

        Map<String, List<PropertyDescriptor>> propertiesByGroup = group(componentData.getPropertiesDescriptors());

        return new PropertiesPanelTabbedPanel(module, snapshot, componentData, propertiesByGroup);
    }

    private Map<String, List<PropertyDescriptor>> group(List<PropertyDescriptor> descriptors) {
        Map<String, List<PropertyDescriptor>> map = new LinkedHashMap<>();
        String defaultTabKey = message("properties.panel.tab.title.general");
        descriptors.forEach(propertyDescriptor -> {
            String group = Optional.ofNullable(propertyDescriptor.getGroup()).orElse(defaultTabKey);
            if (!map.containsKey(group)) {
                map.put(group, new ArrayList<>());
            }
            List<PropertyDescriptor> propertiesForGroup = map.get(group);
            propertiesForGroup.add(propertyDescriptor);
        });
        return map;
    }
}
