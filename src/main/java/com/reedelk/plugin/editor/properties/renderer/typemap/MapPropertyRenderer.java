package com.reedelk.plugin.editor.properties.renderer.typemap;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.ComponentDataHolder;
import com.reedelk.module.descriptor.model.PropertyDescriptor;
import com.reedelk.module.descriptor.model.TypeMapDescriptor;
import com.reedelk.module.descriptor.model.TypeObjectDescriptor;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.commons.ContainerContext;
import com.reedelk.plugin.editor.properties.commons.DisposableTabbedPane;
import com.reedelk.plugin.editor.properties.commons.DisposableTableColumnModelFactory;
import com.reedelk.plugin.editor.properties.commons.DisposableTableModel;
import com.reedelk.plugin.editor.properties.renderer.typemap.custom.MapTableCustomColumnModel;
import com.reedelk.plugin.editor.properties.renderer.typemap.custom.MapTableCustomColumnModelFactory;
import com.reedelk.plugin.editor.properties.renderer.typemap.custom.MapTableCustomEditButtonAction;
import com.reedelk.plugin.editor.properties.renderer.typemap.custom.MapTableCustomObjectDialog;
import com.reedelk.plugin.editor.properties.renderer.typemap.primitive.MapTableColumnModelFactory;
import com.reedelk.plugin.editor.properties.renderer.typemap.primitive.MapTableModel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Optional;
import java.util.function.Function;

import static com.reedelk.plugin.message.ReedelkBundle.message;

public class MapPropertyRenderer extends BaseMapPropertyRenderer {

    @NotNull
    @Override
    public JComponent render(@NotNull Module module,
                             @NotNull PropertyDescriptor descriptor,
                             @NotNull PropertyAccessor propertyAccessor,
                             @NotNull ContainerContext context) {

        final String propertyDisplayName = descriptor.getDisplayName();
        final TypeMapDescriptor propertyType = descriptor.getType();
        final MapColumnAndModel columnAndModel = getColumnAndModel(module, propertyAccessor, propertyType);

        return Optional.ofNullable(propertyType.getTabGroup())
                // Tab group exists
                .map((Function<String, JComponent>) tabGroupName -> {
                    final JComponent content = new MapTableTabContainer(module, columnAndModel.model, columnAndModel.columnModelFactory);
                    final DisposableTabbedPane tabbedPane = tabbedPaneFrom(descriptor, context, propertyType);
                    tabbedPane.addTab(propertyDisplayName, content);
                    return tabbedPane;

                }).orElseGet(() -> {
                    // No tab group
                    return new MapTableContainer(descriptor, module, columnAndModel.model, columnAndModel.columnModelFactory);
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

        TypeObjectDescriptor typeObjectDescriptor = (TypeObjectDescriptor) propertyType.getValueType();
        MapTableCustomColumnModel tableModel = new MapTableCustomColumnModel(propertyAccessor);
        MapTableCustomEditButtonAction action = value -> {
            MapTableCustomObjectDialog dialog =
                    new MapTableCustomObjectDialog(module, message("properties.type.map.value.edit"), typeObjectDescriptor, (ComponentDataHolder) value);
            dialog.showAndGet();
        };
        MapTableCustomColumnModelFactory columnModel = new MapTableCustomColumnModelFactory(propertyType, action);
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
