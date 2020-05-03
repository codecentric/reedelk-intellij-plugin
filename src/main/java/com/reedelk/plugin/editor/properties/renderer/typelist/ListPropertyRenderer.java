package com.reedelk.plugin.editor.properties.renderer.typelist;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.component.ComponentDataHolder;
import com.reedelk.module.descriptor.model.property.ListDescriptor;
import com.reedelk.module.descriptor.model.property.ObjectDescriptor;
import com.reedelk.module.descriptor.model.property.PropertyDescriptor;
import com.reedelk.plugin.editor.properties.commons.DisposableTabbedPane;
import com.reedelk.plugin.editor.properties.commons.TabLabelHorizontal;
import com.reedelk.plugin.editor.properties.commons.TableCustomObjectDialog;
import com.reedelk.plugin.editor.properties.context.ContainerContext;
import com.reedelk.plugin.editor.properties.context.PropertyAccessor;
import com.reedelk.plugin.editor.properties.renderer.AbstractCollectionAwarePropertyTypeRenderer;
import com.reedelk.plugin.editor.properties.renderer.typelist.custom.ListCustomObjectTableContainer;
import com.reedelk.plugin.editor.properties.renderer.typelist.primitive.ListPrimitiveContainer;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.function.Function;

import static com.reedelk.plugin.editor.properties.commons.TableCustomObjectDialog.DialogType;
import static com.reedelk.plugin.editor.properties.commons.TableEditButtonCellEditor.TableCustomEditButtonAction;
import static com.reedelk.plugin.editor.properties.renderer.typelist.custom.ListCustomObjectTableContainer.ListCustomObjectAddItemAction;
import static com.reedelk.plugin.message.ReedelkBundle.message;
import static com.reedelk.runtime.api.commons.StringUtils.EMPTY;
import static java.util.Optional.ofNullable;

public class ListPropertyRenderer extends AbstractCollectionAwarePropertyTypeRenderer {

    @NotNull
    @Override
    public JComponent render(@NotNull Module module,
                             @NotNull PropertyDescriptor descriptor,
                             @NotNull PropertyAccessor propertyAccessor,
                             @NotNull ContainerContext context) {

        final String propertyDisplayName = descriptor.getDisplayName();
        final ListDescriptor propertyType = descriptor.getType();

        JComponent content = isTypeObjectDescriptor(propertyType) ?
                createCustomObjectContent(module, propertyType, propertyAccessor) :
                createContent(descriptor, propertyAccessor);

        // No tab group
        return ofNullable(propertyType.getTabGroup())
                // Tab group exists
                .map((Function<String, JComponent>) tabGroupName -> {
                    final DisposableTabbedPane tabbedPane = tabbedPaneFrom(descriptor, context);
                    tabbedPane.addTab(propertyDisplayName, content);
                    tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, new TabLabelHorizontal(propertyDisplayName));
                    return tabbedPane;

                    // No tab group
                }).orElse(content);
    }

    @NotNull
    private JComponent createContent(@NotNull PropertyDescriptor descriptor,
                                     @NotNull PropertyAccessor propertyAccessor) {
        return new ListPrimitiveContainer(descriptor, propertyAccessor);
    }

    @NotNull
    private JComponent createCustomObjectContent(@NotNull Module module,
                                                 @NotNull ListDescriptor propertyType,
                                                 @NotNull PropertyAccessor propertyAccessor) {
        final ObjectDescriptor objectDescriptor = (ObjectDescriptor) propertyType.getValueType();
        final String dialogTitle = ofNullable(propertyType.getDialogTitle()).orElse(EMPTY);
        final String listDisplayPropertyName = propertyType.getListDisplayProperty();

        // Edit button action
        TableCustomEditButtonAction action = value -> {
            String editDialogTitle = message("properties.type.map.value.edit", dialogTitle);
            TableCustomObjectDialog dialog =
                    new TableCustomObjectDialog(module, editDialogTitle, objectDescriptor, (ComponentDataHolder) value, DialogType.EDIT);
            dialog.showAndGet();
        };

        // Create new item action
        ListCustomObjectAddItemAction addItemAction = newObject -> {
            String newDialogTitle = message("properties.type.map.value.new", dialogTitle);
            TableCustomObjectDialog dialog =
                    new TableCustomObjectDialog(module, newDialogTitle, objectDescriptor, newObject, DialogType.NEW);
            return dialog.showAndGet();
        };

        return new ListCustomObjectTableContainer(module, listDisplayPropertyName, addItemAction, action, propertyAccessor);
    }

    private boolean isTypeObjectDescriptor(ListDescriptor propertyType) {
        return propertyType.getValueType() instanceof ObjectDescriptor;
    }
}
