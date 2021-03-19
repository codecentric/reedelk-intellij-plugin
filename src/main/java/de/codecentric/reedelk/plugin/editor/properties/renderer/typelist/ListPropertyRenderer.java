package de.codecentric.reedelk.plugin.editor.properties.renderer.typelist;

import de.codecentric.reedelk.plugin.editor.properties.commons.DialogTableCustomObject;
import de.codecentric.reedelk.plugin.editor.properties.commons.DisposableTabbedPane;
import de.codecentric.reedelk.plugin.editor.properties.commons.TabLabelHorizontal;
import de.codecentric.reedelk.plugin.editor.properties.commons.TableEditButtonCellEditor;
import de.codecentric.reedelk.plugin.editor.properties.context.ContainerContext;
import de.codecentric.reedelk.plugin.editor.properties.context.ContainerContextDecorator;
import de.codecentric.reedelk.plugin.editor.properties.context.PropertyAccessor;
import de.codecentric.reedelk.plugin.editor.properties.renderer.AbstractCollectionAwarePropertyTypeRenderer;
import de.codecentric.reedelk.plugin.editor.properties.renderer.typelist.custom.ListCustomObjectTableContainer;
import de.codecentric.reedelk.plugin.editor.properties.renderer.typelist.primitive.ListPrimitiveContainer;
import com.intellij.openapi.module.Module;
import de.codecentric.reedelk.module.descriptor.model.component.ComponentDataHolder;
import de.codecentric.reedelk.module.descriptor.model.property.ListDescriptor;
import de.codecentric.reedelk.module.descriptor.model.property.ObjectDescriptor;
import de.codecentric.reedelk.module.descriptor.model.property.PropertyDescriptor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.function.Function;

import static de.codecentric.reedelk.plugin.message.ReedelkBundle.message;
import static de.codecentric.reedelk.runtime.api.commons.StringUtils.EMPTY;
import static java.util.Optional.ofNullable;

public class ListPropertyRenderer extends AbstractCollectionAwarePropertyTypeRenderer {

    @NotNull
    @Override
    public RenderedComponent render(@NotNull Module module,
                             @NotNull PropertyDescriptor descriptor,
                             @NotNull PropertyAccessor propertyAccessor,
                             @NotNull ContainerContext context) {

        final String propertyDisplayName = descriptor.getDisplayName();
        final ListDescriptor propertyType = descriptor.getType();

        JComponent content = isTypeObjectDescriptor(propertyType) ?
                createCustomObjectContent(module, descriptor, propertyAccessor, context) :
                createContent(descriptor, propertyAccessor);

        // No tab group
        JComponent component = ofNullable(propertyType.getTabGroup())
                // Tab group exists
                .map((Function<String, JComponent>) tabGroupName -> {
                    final DisposableTabbedPane tabbedPane = tabbedPaneFrom(descriptor, context);
                    tabbedPane.addTab(propertyDisplayName, content);
                    tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, new TabLabelHorizontal(propertyDisplayName));
                    return tabbedPane;

                    // No tab group
                }).orElse(content);

        return RenderedComponent.create(component);
    }

    @NotNull
    private JComponent createContent(@NotNull PropertyDescriptor descriptor,
                                     @NotNull PropertyAccessor propertyAccessor) {
        return new ListPrimitiveContainer(descriptor, propertyAccessor);
    }

    @NotNull
    private JComponent createCustomObjectContent(@NotNull Module module,
                                                 @NotNull PropertyDescriptor propertyDescriptor,
                                                 @NotNull PropertyAccessor propertyAccessor,
                                                 @NotNull ContainerContext context) {
        final ListDescriptor propertyType = propertyDescriptor.getType();
        final ObjectDescriptor objectDescriptor = (ObjectDescriptor) propertyType.getValueType();
        final String dialogTitle = ofNullable(propertyType.getDialogTitle()).orElse(EMPTY);
        final String listDisplayPropertyName = propertyType.getListDisplayProperty();

        // Edit button action
        TableEditButtonCellEditor.TableCustomEditButtonAction action = value -> {
            // We are entering a new object, we need a new context.
            ContainerContext newContext = ContainerContextDecorator.decorateForProperty(propertyDescriptor.getName(), context);

            String editDialogTitle = message("properties.type.map.value.edit", dialogTitle);
            DialogTableCustomObject dialog =
                    new DialogTableCustomObject(module, newContext, editDialogTitle, objectDescriptor, (ComponentDataHolder) value, DialogTableCustomObject.DialogType.EDIT);
            dialog.showAndGet();
        };

        // Create new item action
        ListCustomObjectTableContainer.ListCustomObjectAddItemAction addItemAction = newObject -> {
            // We are entering a new object, we need a new context.
            ContainerContext newContext = ContainerContextDecorator.decorateForProperty(propertyDescriptor.getName(), context);

            String newDialogTitle = message("properties.type.map.value.new", dialogTitle);
            DialogTableCustomObject dialog =
                    new DialogTableCustomObject(module, newContext, newDialogTitle, objectDescriptor, newObject, DialogTableCustomObject.DialogType.NEW);
            return dialog.showAndGet();
        };

        return new ListCustomObjectTableContainer(module, listDisplayPropertyName, addItemAction, action, propertyAccessor);
    }

    private boolean isTypeObjectDescriptor(ListDescriptor propertyType) {
        return propertyType.getValueType() instanceof ObjectDescriptor;
    }
}
