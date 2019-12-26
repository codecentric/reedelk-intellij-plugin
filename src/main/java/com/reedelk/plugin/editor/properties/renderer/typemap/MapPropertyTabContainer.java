package com.reedelk.plugin.editor.properties.renderer.typemap;

import com.intellij.openapi.module.Module;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.messages.MessageBusConnection;
import com.reedelk.plugin.commons.TableColumnModelUtils;
import com.reedelk.plugin.editor.properties.CommitPropertiesListener;
import com.reedelk.plugin.editor.properties.commons.ClickableLabel;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.commons.PropertyTable;
import com.reedelk.plugin.topic.ReedelkTopics;

import javax.swing.*;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import java.awt.*;

import static com.intellij.icons.AllIcons.General.Add;
import static com.intellij.icons.AllIcons.General.Remove;
import static com.reedelk.plugin.message.ReedelkBundle.message;

class MapPropertyTabContainer extends DisposablePanel {

    private transient MessageBusConnection busConnection;

    private static final String[] COLUMN_NAMES = {"Key", "Value"};

    MapPropertyTabContainer(Module module, PropertyTable.PropertyTableModel tableModel) {
        MapTableColumnModel columnModel = new MapTableColumnModel();
        PropertyTable propertyTable = new PropertyTable(tableModel, columnModel);

        JPanel actionPanel = new TableActionPanel(propertyTable);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(actionPanel);
        add(propertyTable);

        busConnection = module.getProject().getMessageBus().connect();
        busConnection.subscribe(ReedelkTopics.COMMIT_COMPONENT_PROPERTIES_EVENTS, columnModel);
    }

    @Override
    public void dispose() {
        super.dispose();
        busConnection.disconnect();
    }

    static class TableActionPanel extends DisposablePanel {
        TableActionPanel(PropertyTable table) {
            setLayout(new FlowLayout(FlowLayout.LEFT));
            add(new ClickableLabel(message("properties.type.map.table.add"), Add, table::addEmptyRow));
            add(new ClickableLabel(message("properties.type.map.table.remove"), Remove, table::removeSelectedRow));
        }
    }

    static class MapTableColumnModel extends DefaultTableColumnModel implements CommitPropertiesListener {
        MapTableColumnModel() {
            // Column 1 (the map key)
            TableColumn keyColumn = new TableColumn(0);
            keyColumn.setHeaderValue(COLUMN_NAMES[0]);
            keyColumn.setCellEditor(new DefaultCellEditor(new JBTextField()));
            addColumn(keyColumn);

            // Column 2 (the map value)
            TableColumn valueColumn = new TableColumn(1);
            valueColumn.setHeaderValue(COLUMN_NAMES[1]);
            valueColumn.setCellEditor(new DefaultCellEditor(new JBTextField()));
            addColumn(valueColumn);
        }

        @Override
        public void onCommit() {
            TableColumnModelUtils.onCommit(this);
        }
    }
}
