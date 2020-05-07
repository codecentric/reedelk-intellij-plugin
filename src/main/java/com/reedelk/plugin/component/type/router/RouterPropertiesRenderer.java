package com.reedelk.plugin.component.type.router;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.property.PropertyDescriptor;
import com.reedelk.plugin.component.ComponentData;
import com.reedelk.plugin.component.type.generic.GenericComponentPropertiesRenderer;
import com.reedelk.plugin.component.type.router.widget.RouterRouteTable;
import com.reedelk.plugin.editor.properties.commons.PropertiesPanelTabbedPanel;
import com.reedelk.plugin.editor.properties.commons.PropertiesThreeComponentsSplitter;
import com.reedelk.plugin.editor.properties.context.ContainerContext;
import com.reedelk.plugin.graph.FlowSnapshot;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.runtime.api.commons.ImmutableMap;

import javax.swing.*;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static com.reedelk.plugin.component.type.router.RouterNode.DATA_CONDITION_ROUTE_PAIRS;
import static com.reedelk.plugin.message.ReedelkBundle.message;
import static com.reedelk.runtime.api.commons.Preconditions.checkState;

public class RouterPropertiesRenderer extends GenericComponentPropertiesRenderer {

    public RouterPropertiesRenderer(FlowSnapshot snapshot, Module module) {
        super(snapshot, module);
    }

    @Override
    public JComponent render(GraphNode routerNode) {

        ComponentData componentData = routerNode.componentData();

        String componentFullyQualifiedName = componentData.getFullyQualifiedName();

        List<PropertyDescriptor> descriptors = componentData.getPropertiesDescriptors();

        checkState(descriptors.size() == 1, "Expected only one descriptor for router component.");

        PropertyDescriptor propertyDescriptor = descriptors.get(0);

        String propertyName = propertyDescriptor.getName();
        checkState("conditionAndRouteDefinitions".equals(propertyName), "Expected only one property named 'conditionAndRouteDefinitions' for Router.");

        ContainerContext context = new ContainerContext(snapshot, routerNode, componentFullyQualifiedName);

        Supplier<JComponent> routerTableSupplier = () -> {

            List<RouterConditionRoutePair> conditionRoutePairList = componentData.get(DATA_CONDITION_ROUTE_PAIRS);

            String componentPropertyPath = context.getPropertyPath(propertyName);

            return new RouterRouteTable(module, snapshot, conditionRoutePairList, componentPropertyPath, context);
        };

        String defaultTabKey = message("properties.panel.tab.title.general");

        Map<String, Supplier<JComponent>> tabAndComponentSupplier = ImmutableMap.of(defaultTabKey, routerTableSupplier);

        PropertiesPanelTabbedPanel tabbedPanel = new PropertiesPanelTabbedPanel(componentData, tabAndComponentSupplier, context);

        return new PropertiesThreeComponentsSplitter(module, context, componentFullyQualifiedName, tabbedPanel);
    }
}
