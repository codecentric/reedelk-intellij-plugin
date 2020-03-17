package com.reedelk.plugin.component.type.router;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.PropertyDescriptor;
import com.reedelk.plugin.commons.TooltipContent;
import com.reedelk.plugin.component.ComponentData;
import com.reedelk.plugin.component.type.generic.GenericComponentPropertiesRenderer;
import com.reedelk.plugin.component.type.router.widget.RouterRouteTable;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.commons.PropertiesPanelHolder;
import com.reedelk.plugin.graph.FlowSnapshot;
import com.reedelk.plugin.graph.node.GraphNode;

import java.awt.*;
import java.util.List;

import static com.reedelk.plugin.component.type.router.RouterNode.DATA_CONDITION_ROUTE_PAIRS;
import static com.reedelk.plugin.editor.properties.commons.ContainerFactory.createObjectTypeContainer;
import static com.reedelk.runtime.api.commons.Preconditions.checkState;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;

public class RouterPropertiesRenderer extends GenericComponentPropertiesRenderer {

    public RouterPropertiesRenderer(FlowSnapshot snapshot, Module module) {
        super(snapshot, module);
    }

    @Override
    public DisposablePanel render(GraphNode routerNode) {

        ComponentData componentData = routerNode.componentData();

        String componentFullyQualifiedName = componentData.getFullyQualifiedName();

        List<PropertyDescriptor> descriptors = componentData.getPropertiesDescriptors();

        checkState(descriptors.size() == 1, "Expected only one descriptor for router component.");

        PropertyDescriptor propertyDescriptor = descriptors.get(0);
        String propertyTitle = propertyDescriptor.getDisplayName();

        String propertyName = propertyDescriptor.getName();
        checkState("conditionAndRouteDefinitions".equals(propertyName), "Expected only one property named 'conditionAndRouteDefinitions' for Router.");

        TooltipContent tooltipContent = TooltipContent.from(propertyDescriptor);

        PropertiesPanelHolder propertiesPanel =
                new PropertiesPanelHolder(module, componentFullyQualifiedName, componentData, snapshot);

        List<RouterConditionRoutePair> conditionRoutePairList = componentData.get(DATA_CONDITION_ROUTE_PAIRS);

        RouterRouteTable routerRouteTable = new RouterRouteTable(module, snapshot, conditionRoutePairList, propertiesPanel);
        DisposablePanel routerTableContainer = createObjectTypeContainer(routerRouteTable, propertyTitle, tooltipContent);

        DisposablePanel container = new DisposablePanel();
        container.setLayout(new BorderLayout());
        container.add(propertiesPanel, NORTH);
        container.add(routerTableContainer, CENTER);
        return container;
    }
}
