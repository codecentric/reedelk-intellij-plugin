package com.reedelk.plugin.component.type.router;

import com.intellij.openapi.module.Module;
import com.intellij.util.ui.JBUI;
import com.reedelk.module.descriptor.model.PropertyDescriptor;
import com.reedelk.plugin.commons.TooltipContent;
import com.reedelk.plugin.component.ComponentData;
import com.reedelk.plugin.component.type.generic.GenericComponentPropertiesRenderer;
import com.reedelk.plugin.component.type.router.widget.RouterRouteTable;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.commons.PropertiesPanelHolder;
import com.reedelk.plugin.editor.properties.commons.PropertiesPanelTabbedPanel;
import com.reedelk.plugin.graph.FlowSnapshot;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.runtime.api.commons.ImmutableMap;

import javax.swing.*;
import java.util.List;
import java.util.function.Supplier;

import static com.reedelk.plugin.component.type.router.RouterNode.DATA_CONDITION_ROUTE_PAIRS;
import static com.reedelk.plugin.editor.properties.commons.ContainerFactory.createObjectTypeContainer;
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
        String propertyTitle = propertyDescriptor.getDisplayName();

        String propertyName = propertyDescriptor.getName();
        checkState("conditionAndRouteDefinitions".equals(propertyName), "Expected only one property named 'conditionAndRouteDefinitions' for Router.");

        TooltipContent tooltipContent = TooltipContent.from(propertyDescriptor);

        Supplier<JComponent> routerTableSupplier = () -> {
            PropertiesPanelHolder propertiesPanel =
                    new PropertiesPanelHolder(module, componentFullyQualifiedName, componentData, snapshot);

            List<RouterConditionRoutePair> conditionRoutePairList = componentData.get(DATA_CONDITION_ROUTE_PAIRS);

            RouterRouteTable routerRouteTable = new RouterRouteTable(module, snapshot, conditionRoutePairList, propertiesPanel);
            DisposablePanel objectTypeContainer = createObjectTypeContainer(routerRouteTable, propertyTitle, tooltipContent);
            objectTypeContainer.setBorder(JBUI.Borders.empty(10, 5, 0, 0));
            return objectTypeContainer;
        };


        String defaultTabKey = message("properties.panel.tab.title.general");
        return new PropertiesPanelTabbedPanel(componentData, ImmutableMap.of(defaultTabKey, routerTableSupplier));
    }
}
