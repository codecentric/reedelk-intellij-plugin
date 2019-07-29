package com.esb.plugin.component.type.router;

import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.component.type.generic.GenericComponentPropertiesRenderer;
import com.esb.plugin.component.type.router.widget.ConditionRouteTableModel;
import com.esb.plugin.component.type.router.widget.RouterRouteTable;
import com.esb.plugin.editor.properties.widget.DisposablePanel;
import com.esb.plugin.graph.FlowSnapshot;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.openapi.module.Module;

import java.awt.*;
import java.util.List;

import static com.esb.plugin.component.type.router.RouterNode.DATA_CONDITION_ROUTE_PAIRS;
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

        DisposablePanel container = new DisposablePanel();
        container.setLayout(new BorderLayout());
        container.add(genericProperties, NORTH);
        container.add(routerRouteTable, CENTER);
        return container;
    }
}
