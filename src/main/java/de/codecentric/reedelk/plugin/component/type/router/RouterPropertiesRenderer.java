package de.codecentric.reedelk.plugin.component.type.router;

import de.codecentric.reedelk.plugin.component.type.router.widget.RouterRouteTable;
import com.intellij.openapi.module.Module;
import de.codecentric.reedelk.module.descriptor.model.property.PropertyDescriptor;
import de.codecentric.reedelk.plugin.component.ComponentData;
import de.codecentric.reedelk.plugin.component.type.generic.GenericComponentPropertiesRenderer;
import de.codecentric.reedelk.plugin.editor.properties.commons.PropertiesPanelTabbedPanel;
import de.codecentric.reedelk.plugin.editor.properties.commons.PropertiesThreeComponentsSplitter;
import de.codecentric.reedelk.plugin.editor.properties.context.ContainerContext;
import de.codecentric.reedelk.plugin.graph.FlowSnapshot;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.runtime.api.commons.ImmutableMap;

import javax.swing.*;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static de.codecentric.reedelk.plugin.component.type.router.RouterNode.DATA_CONDITION_ROUTE_PAIRS;
import static de.codecentric.reedelk.plugin.message.ReedelkBundle.message;
import static de.codecentric.reedelk.runtime.api.commons.Preconditions.checkState;

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

        return new PropertiesThreeComponentsSplitter(module, context, tabbedPanel, context);
    }
}
