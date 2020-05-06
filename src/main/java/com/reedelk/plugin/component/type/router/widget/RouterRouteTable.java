package com.reedelk.plugin.component.type.router.widget;

import com.intellij.openapi.module.Module;
import com.intellij.ui.JBColor;
import com.reedelk.plugin.commons.Colors;
import com.reedelk.plugin.component.type.router.RouterConditionRoutePair;
import com.reedelk.plugin.editor.properties.commons.DisposableTable;
import com.reedelk.plugin.graph.FlowSnapshot;

import java.util.List;

import static com.intellij.util.ui.JBUI.Borders.customLine;

public class RouterRouteTable extends DisposableTable {

    public RouterRouteTable(Module module,
                            FlowSnapshot snapshot,
                            List<RouterConditionRoutePair> conditionRoutePairList,
                            String componentPropertyPath,
                            String inputFullyQualifiedName) {
        super(module.getProject(),
                new ConditionRouteTableModel(conditionRoutePairList, snapshot),false);

        ConditionRouteTableColumnModelFactory factory =
                new ConditionRouteTableColumnModelFactory(module, componentPropertyPath, inputFullyQualifiedName);

        factory.create(this);
        setOpaque(true);
        setBackground(Colors.PROPERTIES_EMPTY_SELECTION_BACKGROUND);
        setBorder(customLine(JBColor.LIGHT_GRAY, 0, 0, 1, 0));
    }
}
