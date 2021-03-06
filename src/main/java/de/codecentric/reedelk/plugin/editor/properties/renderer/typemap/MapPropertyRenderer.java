package de.codecentric.reedelk.plugin.editor.properties.renderer.typemap;

import de.codecentric.reedelk.plugin.editor.properties.commons.*;
import de.codecentric.reedelk.plugin.editor.properties.context.ContainerContext;
import de.codecentric.reedelk.plugin.editor.properties.context.ContainerContextDecorator;
import de.codecentric.reedelk.plugin.editor.properties.context.PropertyAccessor;
import de.codecentric.reedelk.plugin.editor.properties.renderer.AbstractCollectionAwarePropertyTypeRenderer;
import de.codecentric.reedelk.plugin.editor.properties.renderer.typemap.custom.MapTableCustomColumnModelFactory;
import de.codecentric.reedelk.plugin.editor.properties.renderer.typemap.custom.MapTableCustomModel;
import com.intellij.openapi.module.Module;
import de.codecentric.reedelk.module.descriptor.model.component.ComponentDataHolder;
import de.codecentric.reedelk.module.descriptor.model.property.MapDescriptor;
import de.codecentric.reedelk.module.descriptor.model.property.ObjectDescriptor;
import de.codecentric.reedelk.module.descriptor.model.property.PropertyDescriptor;
import de.codecentric.reedelk.plugin.editor.properties.renderer.typemap.primitive.MapTableColumnModelFactory;
import de.codecentric.reedelk.plugin.editor.properties.renderer.typemap.primitive.MapTableModel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.function.Function;

import static de.codecentric.reedelk.plugin.message.ReedelkBundle.message;
import static de.codecentric.reedelk.runtime.api.commons.StringUtils.EMPTY;
import static java.util.Optional.ofNullable;

public class MapPropertyRenderer extends AbstractCollectionAwarePropertyTypeRenderer {

    @NotNull
    @Override
    public RenderedComponent render(@NotNull Module module,
                             @NotNull PropertyDescriptor descriptor,
                             @NotNull PropertyAccessor propertyAccessor,
                             @NotNull ContainerContext context) {

        final String propertyDisplayName = descriptor.getDisplayName();
        final MapDescriptor propertyType = descriptor.getType();
        final MapColumnAndModel columnAndModel = getColumnAndModel(module, propertyAccessor, propertyType, descriptor.getName(), context);

        JComponent component = ofNullable(propertyType.getTabGroup())
                // Tab group exists
                .map((Function<String, JComponent>) tabGroupName -> {
                    JComponent content = new MapTableTabContainer(module, columnAndModel.model, columnAndModel.columnModelFactory);
                    DisposableTabbedPane tabbedPane = tabbedPaneFrom(descriptor, context);
                    tabbedPane.addTab(propertyDisplayName, content);
                    tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, new TabLabelHorizontal(propertyDisplayName));
                    return tabbedPane;

                }).orElseGet(() ->
                        // No tab group
                        new MapTableContainer(module, columnAndModel.model, columnAndModel.columnModelFactory));

        return RenderedComponent.create(component);
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

        TableEditButtonCellEditor.TableCustomEditButtonAction action = value -> {
            String editDialogTitle = message("properties.type.map.value.edit", dialogTitle);
            // We are entering a new object, we need a new context.
            ContainerContext newContext = ContainerContextDecorator.decorateForProperty(propertyName, context);
            DialogTableCustomObject dialog =
                    new DialogTableCustomObject(module, newContext, editDialogTitle, typeObjectDescriptor, (ComponentDataHolder) value, DialogTableCustomObject.DialogType.EDIT);
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
