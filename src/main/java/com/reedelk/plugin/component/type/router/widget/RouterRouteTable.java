package com.reedelk.plugin.component.type.router.widget;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.editor.properties.widget.DisposablePanel;
import com.reedelk.plugin.editor.properties.widget.PropertyTable;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import java.awt.*;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;

public class RouterRouteTable extends DisposablePanel {

    public RouterRouteTable(Module module, ConditionRouteTableModel model) {

        TableColumnModel tableColumnModel = new ConditionRouteTableColumnModel(this, module);
        PropertyTable propertyTable = new PropertyTable(model, tableColumnModel);

        setLayout(new BorderLayout());
        add(propertyTable, NORTH);
        add(Box.createVerticalGlue(), CENTER);
    }
}
