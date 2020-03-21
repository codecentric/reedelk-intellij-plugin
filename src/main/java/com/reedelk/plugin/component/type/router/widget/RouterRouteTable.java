package com.reedelk.plugin.component.type.router.widget;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.commons.Sizes;
import com.reedelk.plugin.component.type.router.RouterConditionRoutePair;
import com.reedelk.plugin.editor.properties.commons.ContainerContext;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.commons.DisposableTable;
import com.reedelk.plugin.editor.properties.commons.DisposableTableModel;
import com.reedelk.plugin.graph.FlowSnapshot;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;

public class RouterRouteTable extends DisposablePanel {

    public RouterRouteTable(Module module, FlowSnapshot snapshot, List<RouterConditionRoutePair> conditionRoutePairList, ContainerContext context) {

        DisposableTableModel model = new ConditionRouteTableModel(conditionRoutePairList, snapshot);
        ConditionRouteTableColumnModelFactory factory = new ConditionRouteTableColumnModelFactory(module, context);

        DisposableTable table = new DisposableTable(module.getProject(), Sizes.Table.ROUTER, model, factory);

        setLayout(new BorderLayout());
        add(table, NORTH);
        add(Box.createVerticalGlue(), CENTER);
    }
}
