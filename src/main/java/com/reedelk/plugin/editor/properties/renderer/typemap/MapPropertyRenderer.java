package com.reedelk.plugin.editor.properties.renderer.typemap;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.component.ComponentDataHolder;
import com.reedelk.module.descriptor.model.property.MapDescriptor;
import com.reedelk.module.descriptor.model.property.ObjectDescriptor;
import com.reedelk.module.descriptor.model.property.PropertyDescriptor;
import com.reedelk.plugin.editor.properties.commons.*;
import com.reedelk.plugin.editor.properties.context.ContainerContext;
import com.reedelk.plugin.editor.properties.context.ContainerContextDecorator;
import com.reedelk.plugin.editor.properties.context.PropertyAccessor;
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
        final MapDescriptor propertyType = descriptor.getType();
        final MapColumnAndModel columnAndModel = getColumnAndModel(module, propertyAccessor, propertyType, descriptor.getName(), context);

        return ofNullable(propertyType.getTabGroup())
                // Tab group exists
                .map((Function<String, JComponent>) tabGroupName -> {
                    JComponent content = new MapTableTabContainer(module, columnAndModel.model, columnAndModel.columnModelFactory);
                    DisposableTabbedPane tabbedPane = tabbedPaneFrom(descriptor, context);
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
                                                @NotNull MapDescriptor propertyType,
                                                @NotNull String name,
                                                @NotNull ContainerContext context) {
        return isTypeObjectDescriptor(propertyType) ?
                createCustomObjectContent(module, propertyType, propertyAccessor, name, context) :
                createContent(propertyType, propertyAccessor);
    }

    private MapColumnAndModel createContent(@NotNull MapDescriptor propertyType,
                                            @NotNull PropertyAccessor propertyAccessor) {
        MapTableModel tableModel = new MapTableModel(propertyAccessor);
        MapTableColumnModelFactory columnModel = new MapTableColumnModelFactory(propertyType);
        return new MapColumnAndModel(tableModel, columnModel);
    }

    protected MapColumnAndModel createCustomObjectContent(@NotNull Module module,
                                                          @NotNull MapDescriptor propertyType,
                                                          @NotNull PropertyAccessor propertyAccessor,
                                                          @NotNull String propertyName,
                                                          @NotNull ContainerContext context) {

        ObjectDescriptor typeObjectDescriptor = (ObjectDescriptor) propertyType.getValueType();
        String dialogTitle = ofNullable(propertyType.getDialogTitle()).orElse(EMPTY);

        TableCustomEditButtonAction action = value -> {
            String editDialogTitle = message("properties.type.map.value.edit", dialogTitle);
            // We are entering a new object, we need a new context.
            ContainerContext newContext = ContainerContextDecorator.decorateForProperty(propertyName, context);
            TableCustomObjectDialog dialog =
                    new TableCustomObjectDialog(module, newContext, editDialogTitle, typeObjectDescriptor, (ComponentDataHolder) value, DialogType.EDIT);
            dialog.showAndGet();
        };
        MapTableCustomColumnModelFactory columnModel = new MapTableCustomColumnModelFactory(propertyType, action);
        MapTableCustomModel tableModel = new MapTableCustomModel(propertyAccessor, typeObjectDescriptor);
        return new MapColumnAndModel(tableModel, columnModel);
    }

    private boolean isTypeObjectDescriptor(MapDescriptor propertyType) {
        return propertyType.getValueType() instanceof ObjectDescriptor;
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
