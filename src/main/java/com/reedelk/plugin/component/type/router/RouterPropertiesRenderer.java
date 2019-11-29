package com.reedelk.plugin.component.type.router;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.component.domain.ComponentData;
import com.reedelk.plugin.component.type.generic.GenericComponentPropertiesRenderer;
import com.reedelk.plugin.component.type.router.widget.ConditionRouteTableModel;
import com.reedelk.plugin.component.type.router.widget.RouterRouteTable;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.graph.FlowSnapshot;
import com.reedelk.plugin.graph.node.GraphNode;

import java.awt.*;
import java.util.List;

import static com.reedelk.plugin.component.type.router.RouterNode.DATA_CONDITION_ROUTE_PAIRS;
import static com.reedelk.plugin.editor.properties.commons.ContainerFactory.createObjectTypeContainer;
import static com.reedelk.plugin.message.ReedelkBundle.message;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;

public class RouterPropertiesRenderer extends GenericComponentPropertiesRenderer {

    public RouterPropertiesRenderer(FlowSnapshot snapshot, Module module) {
        super(snapshot, module);
    }

    @Override
    public DisposablePanel render(GraphNode routerNode) {
        DisposablePanel genericProperties = super.render(routerNode);

        ComponentData componentData = routerNode.componentData();
        List<RouterConditionRoutePair> conditionRoutePairList = componentData.get(DATA_CONDITION_ROUTE_PAIRS);

        ConditionRouteTableModel model = new ConditionRouteTableModel(conditionRoutePairList, snapshot);
        RouterRouteTable routerRouteTable = new RouterRouteTable(module, model);
        DisposablePanel routerTableContainer =
                createObjectTypeContainer(routerRouteTable, message("router.table.container.title"));

        DisposablePanel container = new DisposablePanel();
        container.setLayout(new BorderLayout());
        container.add(genericProperties, NORTH);
        container.add(routerTableContainer, CENTER);
        return container;
    }
}
