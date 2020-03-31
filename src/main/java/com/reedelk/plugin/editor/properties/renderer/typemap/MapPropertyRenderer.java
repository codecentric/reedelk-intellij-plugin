package com.reedelk.plugin.editor.properties.renderer.typemap;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.ComponentDataHolder;
import com.reedelk.module.descriptor.model.PropertyDescriptor;
import com.reedelk.module.descriptor.model.TypeMapDescriptor;
import com.reedelk.module.descriptor.model.TypeObjectDescriptor;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.commons.*;
import com.reedelk.plugin.editor.properties.renderer.AbstractCollectionAwarePropertyTypeRenderer;
import com.reedelk.plugin.editor.properties.renderer.typemap.custom.MapTableCustomColumnModelFactory;
import com.reedelk.plugin.editor.properties.renderer.typemap.custom.MapTableCustomModel;
import com.reedelk.plugin.editor.properties.renderer.typemap.primitive.MapTableColumnModelFactory;
import com.reedelk.plugin.editor.properties.renderer.typemap.primitive.MapTableModel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.function.Function;

import static com.reedelk.plugin.editor.properties.commons.TableCustomObjectDialog.DialogType;
import static com.reedelk.plugin.editor.properties.commons.TableEditButtonCellEditor.TableCustomEditButtonAction;
import static com.reedelk.plugin.message.ReedelkBundle.message;
import static com.reedelk.runtime.api.commons.StringUtils.EMPTY;
import static java.util.Optional.ofNullable;

public class MapPropertyRenderer extends AbstractCollectionAwarePropertyTypeRenderer {

    @NotNull
    @Override
    public JComponent render(@NotNull Module module,
                             @NotNull PropertyDescriptor descriptor,
                             @NotNull PropertyAccessor propertyAccessor,
                             @NotNull ContainerContext context) {

        final String propertyDisplayName = descriptor.getDisplayName();
        final TypeMapDescriptor propertyType = descriptor.getType();
        final MapColumnAndModel columnAndModel = getColumnAndModel(module, propertyAccessor, propertyType);

        return ofNullable(propertyType.getTabGroup())
                // Tab group exists
                .map((Function<String, JComponent>) tabGroupName -> {
                    final JComponent content = new MapTableTabContainer(module, columnAndModel.model, columnAndModel.columnModelFactory);
                    final DisposableTabbedPane tabbedPane = tabbedPaneFrom(descriptor, context);
                    tabbedPane.addTab(propertyDisplayName, content);
                    tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, new TabLabelHorizontal(propertyDisplayName));
                    return tabbedPane;

                }).orElseGet(() -> {
                    // No tab group
                    return new MapTableContainer(module, columnAndModel.model, columnAndModel.columnModelFactory);
                });
    }

    private MapColumnAndModel getColumnAndModel(@NotNull Module module,
                                                @NotNull PropertyAccessor propertyAccessor,
                                                @NotNull TypeMapDescriptor propertyType) {
        return isTypeObjectDescriptor(propertyType) ?
                createCustomObjectContent(module, propertyType, propertyAccessor) :
                createContent(propertyType, propertyAccessor);
    }

    private MapColumnAndModel createContent(@NotNull TypeMapDescriptor propertyType,
                                            @NotNull PropertyAccessor propertyAccessor) {
        MapTableModel tableModel = new MapTableModel(propertyAccessor);
        MapTableColumnModelFactory columnModel = new MapTableColumnModelFactory(propertyType);
        return new MapColumnAndModel(tableModel, columnModel);
    }

    protected MapColumnAndModel createCustomObjectContent(@NotNull Module module,
                                                          @NotNull TypeMapDescriptor propertyType,
                                                          @NotNull PropertyAccessor propertyAccessor) {

        final TypeObjectDescriptor typeObjectDescriptor = (TypeObjectDescriptor) propertyType.getValueType();
        final String dialogTitle = ofNullable(propertyType.getDialogTitle()).orElse(EMPTY);
        TableCustomEditButtonAction action = value -> {
            String editDialogTitle = message("properties.type.map.value.edit", dialogTitle);
            TableCustomObjectDialog dialog =
                    new TableCustomObjectDialog(module, editDialogTitle, typeObjectDescriptor, (ComponentDataHolder) value, DialogType.EDIT);
            dialog.showAndGet();
        };
        MapTableCustomColumnModelFactory columnModel = new MapTableCustomColumnModelFactory(propertyType, action);
        MapTableCustomModel tableModel = new MapTableCustomModel(propertyAccessor, typeObjectDescriptor);
        return new MapColumnAndModel(tableModel, columnModel);
    }

    private boolean isTypeObjectDescriptor(TypeMapDescriptor propertyType) {
        return propertyType.getValueType() instanceof TypeObjectDescriptor;
    }

    static class MapColumnAndModel {

        DisposableTableModel model;
        DisposableTableColumnModelFactory columnModelFactory;

        MapColumnAndModel(DisposableTableModel model, DisposableTableColumnModelFactory columnModelFactory) {
            this.model = model;
            this.columnModelFactory = columnModelFactory;
        }
    }
}
