package de.codecentric.reedelk.plugin.component.type.router.widget;

import com.intellij.openapi.module.Module;
import com.intellij.ui.JBColor;
import de.codecentric.reedelk.plugin.component.type.router.RouterConditionRoutePair;
import de.codecentric.reedelk.plugin.editor.properties.commons.DisposableTable;
import de.codecentric.reedelk.plugin.editor.properties.context.ContainerContext;
import de.codecentric.reedelk.plugin.graph.FlowSnapshot;

import java.util.List;

import static com.intellij.util.ui.JBUI.Borders.customLine;

public class RouterRouteTable extends DisposableTable {

    public RouterRouteTable(Module module,
                            FlowSnapshot snapshot,
                            List<RouterConditionRoutePair> conditionRoutePairList,
                            String componentPropertyPath,
                            ContainerContext context) {
        super(module.getProject(),
                new ConditionRouteTableModel(conditionRoutePairList, snapshot),false);

        ConditionRouteTableColumnModelFactory factory =
                new ConditionRouteTableColumnModelFactory(module, componentPropertyPath, context);

        factory.create(this);
        setOpaque(false);
        setBorder(customLine(JBColor.LIGHT_GRAY, 0, 0, 1, 0));
    }
}
