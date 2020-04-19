package com.reedelk.plugin.component.type.router.widget;

import com.intellij.openapi.module.Module;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.JBUI;
import com.reedelk.plugin.commons.Colors;
import com.reedelk.plugin.commons.Sizes;
import com.reedelk.plugin.component.type.router.RouterConditionRoutePair;
import com.reedelk.plugin.editor.properties.commons.ContainerContext;
import com.reedelk.plugin.graph.FlowSnapshot;

import java.awt.*;
import java.util.List;

// TODO: Need to add on commit and dispose.
public class RouterRouteTable extends JBTable {

    public RouterRouteTable(Module module,
                            FlowSnapshot snapshot,
                            List<RouterConditionRoutePair> conditionRoutePairList,
                            ContainerContext context) {

        super(new ConditionRouteTableModel(conditionRoutePairList, snapshot));
        ConditionRouteTableColumnModelFactory factory = new ConditionRouteTableColumnModelFactory(module, context);

        setShowHorizontalLines(true);
        setShowVerticalLines(true);
        setOpaque(true);
        //this.table.addFocusListener(new DisposableTable.ClearSelectionFocusListener());
        setRowHeight(Sizes.Table.ROW_HEIGHT);
        setFillsViewportHeight(false);
        factory.create(this);
        setBackground(Colors.PROPERTIES_EMPTY_SELECTION_BACKGROUND);
        setBorder(JBUI.Borders.customLine(Color.LIGHT_GRAY, 0, 0, 1, 0));
    }
}
