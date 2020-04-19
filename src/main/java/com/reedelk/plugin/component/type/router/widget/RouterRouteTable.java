package com.reedelk.plugin.component.type.router.widget;

import com.intellij.openapi.module.Module;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;
import com.reedelk.plugin.commons.Colors;
import com.reedelk.plugin.component.type.router.RouterConditionRoutePair;
import com.reedelk.plugin.editor.properties.commons.ContainerContext;
import com.reedelk.plugin.editor.properties.commons.DisposableTable;
import com.reedelk.plugin.graph.FlowSnapshot;

import java.util.List;

public class RouterRouteTable extends DisposableTable {

    public RouterRouteTable(Module module,
                            FlowSnapshot snapshot,
                            List<RouterConditionRoutePair> conditionRoutePairList,
                            ContainerContext context) {

        super(module.getProject(),
                new ConditionRouteTableModel(conditionRoutePairList, snapshot),
                new ConditionRouteTableColumnModelFactory(module, context),
                false);

        setOpaque(true);
        setBackground(Colors.PROPERTIES_EMPTY_SELECTION_BACKGROUND);
        setBorder(JBUI.Borders.customLine(JBColor.LIGHT_GRAY, 0, 0, 1, 0));
    }
}
