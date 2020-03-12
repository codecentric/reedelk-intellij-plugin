package com.reedelk.plugin.component.type.router.widget;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.component.type.router.RouterConditionRoutePair;
import com.reedelk.plugin.editor.properties.commons.ContainerContext;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.commons.PropertyTable;
import com.reedelk.plugin.graph.FlowSnapshot;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.List;

import static com.reedelk.plugin.editor.properties.commons.PropertyTable.PropertyTableModel;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;

public class RouterRouteTable extends DisposablePanel {

    public RouterRouteTable(Module module, FlowSnapshot snapshot, List<RouterConditionRoutePair> conditionRoutePairList, ContainerContext context) {

        PropertyTableModel model = new ConditionRouteTableModel(conditionRoutePairList, snapshot);
        TableColumnModel tableColumnModel = new ConditionRouteTableColumnModel(module, context);

        PropertyTable propertyTable = new PropertyTable(model, tableColumnModel);

        setLayout(new BorderLayout());
        add(propertyTable, NORTH);
        add(Box.createVerticalGlue(), CENTER);
    }
}
