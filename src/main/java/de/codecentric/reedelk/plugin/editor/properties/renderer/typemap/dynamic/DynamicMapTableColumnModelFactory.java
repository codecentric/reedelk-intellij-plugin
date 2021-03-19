package de.codecentric.reedelk.plugin.editor.properties.renderer.typemap.dynamic;

import de.codecentric.reedelk.plugin.editor.properties.commons.DisposableTableColumnModelFactory;
import de.codecentric.reedelk.plugin.editor.properties.commons.StripedRowCellRenderer;
import de.codecentric.reedelk.plugin.editor.properties.commons.TableDynamicCellEditor;
import de.codecentric.reedelk.plugin.editor.properties.commons.TableDynamicCellRenderer;
import de.codecentric.reedelk.plugin.editor.properties.context.ContainerContext;
import com.intellij.openapi.module.Module;
import com.intellij.ui.table.JBTable;
import de.codecentric.reedelk.module.descriptor.model.property.MapDescriptor;

import javax.swing.table.TableColumn;
import java.util.Optional;

import static de.codecentric.reedelk.plugin.message.ReedelkBundle.message;

public class DynamicMapTableColumnModelFactory implements DisposableTableColumnModelFactory {

    private final Module module;
    private final ContainerContext context;
    private final MapDescriptor propertyType;
    private final String componentPropertyPath;

    DynamicMapTableColumnModelFactory(Module module,
                                      MapDescriptor propertyType,
                                      String componentPropertyPath,
                                      ContainerContext context) {
        this.module = module;
        this.context = context;
        this.propertyType = propertyType;
        this.componentPropertyPath = componentPropertyPath;
    }

    @Override
    public void create(JBTable table) {
        String keyName = Optional.ofNullable(propertyType.getKeyName()).orElse(message("table.header.key.name"));
        String valueName = Optional.ofNullable(propertyType.getValueName()).orElse(message("table.header.value.name"));

        // table editor is lazy loaded to avoid this expensive
        // operation when the tab holding this table is not visible.
        TableDynamicCellEditor cellEditor = new TableDynamicCellEditor(module, componentPropertyPath, context);
        TableDynamicCellRenderer cellRenderer = new TableDynamicCellRenderer(module, componentPropertyPath, context);

        // Column 1 (the map key)
        TableColumn keyColumn = table.getColumnModel().getColumn(0);
        keyColumn.setHeaderValue(keyName);
        keyColumn.setCellRenderer(new StripedRowCellRenderer());

        // Column 2 (the map value)
        TableColumn valueColumn = table.getColumnModel().getColumn(1);
        valueColumn.setHeaderValue(valueName);
        valueColumn.setCellEditor(cellEditor);
        valueColumn.setCellRenderer(cellRenderer);
    }
}
