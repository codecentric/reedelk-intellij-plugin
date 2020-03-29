package com.reedelk.plugin.editor.properties.renderer.typelist;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.ComponentDataHolder;
import com.reedelk.module.descriptor.model.PropertyDescriptor;
import com.reedelk.module.descriptor.model.TypeListDescriptor;
import com.reedelk.module.descriptor.model.TypeObjectDescriptor;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.commons.ContainerContext;
import com.reedelk.plugin.editor.properties.commons.DisposableTabbedPane;
import com.reedelk.plugin.editor.properties.commons.TabLabelHorizontal;
import com.reedelk.plugin.editor.properties.commons.TableCustomObjectDialog;
import com.reedelk.plugin.editor.properties.renderer.AbstractCollectionAwarePropertyTypeRenderer;
import com.reedelk.plugin.editor.properties.renderer.typelist.custom.ListCustomObjectTableContainer;
import com.reedelk.plugin.editor.properties.renderer.typelist.primitive.ListPrimitiveContainer;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.function.Function;

import static com.reedelk.plugin.editor.properties.commons.TableEditButtonCellEditor.TableCustomEditButtonAction;
import static com.reedelk.plugin.editor.properties.renderer.typelist.custom.ListCustomObjectTableContainer.ListCustomObjectAddItemAction;
import static java.util.Optional.ofNullable;

public class ListPropertyRenderer extends AbstractCollectionAwarePropertyTypeRenderer {

    @NotNull
    @Override
    public JComponent render(@NotNull Module module,
                             @NotNull PropertyDescriptor descriptor,
                             @NotNull PropertyAccessor propertyAccessor,
                             @NotNull ContainerContext context) {

        final String propertyDisplayName = descriptor.getDisplayName();
        final TypeListDescriptor propertyType = descriptor.getType();

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
                                                 @NotNull TypeListDescriptor propertyType,
                                                 @NotNull PropertyAccessor propertyAccessor) {
        final TypeObjectDescriptor objectDescriptor = (TypeObjectDescriptor) propertyType.getValueType();
        String dialogTitle = propertyType.getDialogTitle();
        String listDisplayPropertyName = propertyType.getListDisplayProperty();

        // Edit button action
        TableCustomEditButtonAction action = value -> {
            String editDialogTitle = "Edit " + dialogTitle;
            TableCustomObjectDialog dialog =
                    new TableCustomObjectDialog(module, editDialogTitle, objectDescriptor, (ComponentDataHolder) value);
            dialog.showAndGet();
        };

        // Create new item action
        ListCustomObjectAddItemAction addItemAction = newObject -> {
            String newDialogTitle = "New " + dialogTitle;
            TableCustomObjectDialog dialog =
                    new TableCustomObjectDialog(module, newDialogTitle, objectDescriptor, newObject);
            return dialog.showAndGet();
        };

        return new ListCustomObjectTableContainer(module, listDisplayPropertyName, addItemAction, action, propertyAccessor);
    }

    private boolean isTypeObjectDescriptor(TypeListDescriptor propertyType) {
        return propertyType.getValueType() instanceof TypeObjectDescriptor;
    }
}
