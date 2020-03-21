package com.reedelk.plugin.editor.properties.renderer.typemap;

import com.intellij.openapi.module.Module;
import com.intellij.util.ui.JBUI;
import com.reedelk.module.descriptor.model.PropertyDescriptor;
import com.reedelk.plugin.editor.properties.commons.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class MapTableContainer extends DisposablePanel {

    public MapTableContainer(
            @NotNull PropertyDescriptor propertyDescriptor,
            @NotNull Module module,
            @NotNull DisposableTableModel tableModel,
            @NotNull DisposableTableColumnModelFactory columnModelFactory) {

        setLayout(new BorderLayout());
        setBorder(JBUI.Borders.emptyTop(5));
        add(new LabelContainer(propertyDescriptor), BorderLayout.NORTH);
        add(new TableContainer(module, tableModel, columnModelFactory), BorderLayout.CENTER);
    }

    static class TableContainer extends DisposablePanel {

        TableContainer(@NotNull Module module,
                       @NotNull DisposableTableModel tableModel,
                       @NotNull DisposableTableColumnModelFactory columnModelFactory) {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            DisposableTable disposablePropertyTable = new DisposableTable(module.getProject(), tableModel, columnModelFactory);
            JPanel actionPanel = new MapTableActionPanel(disposablePropertyTable);
            add(actionPanel);
            add(disposablePropertyTable);
        }
    }

    static class LabelContainer extends DisposablePanel {

        LabelContainer(PropertyDescriptor propertyDescriptor) {
            setLayout(new BorderLayout());
            PropertyTitleLabel propertyTitleLabel = new PropertyTitleLabel(propertyDescriptor);
            add(propertyTitleLabel, BorderLayout.NORTH);
        }
    }
}
