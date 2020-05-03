package com.reedelk.plugin.editor.properties.renderer.typelist.custom;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.property.ObjectDescriptor;
import com.reedelk.plugin.commons.Sizes;
import com.reedelk.plugin.editor.properties.commons.*;
import com.reedelk.plugin.editor.properties.context.PropertyAccessor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

import static com.reedelk.plugin.editor.properties.commons.TableEditButtonCellEditor.TableCustomEditButtonAction;


public class ListCustomObjectTableContainer extends DisposablePanel {

    public ListCustomObjectTableContainer(@NotNull Module module,
                                          @NotNull String listDisplayPropertyName,
                                          @NotNull ListCustomObjectAddItemAction onAddAction,
                                          @NotNull TableCustomEditButtonAction action,
                                          @NotNull PropertyAccessor propertyAccessor) {

        ListTableCustomColumnModelFactory factory = new ListTableCustomColumnModelFactory(action, listDisplayPropertyName);
        DisposableTableModel disposableTableModel = new ListTableCustomModel(propertyAccessor);

        DisposableScrollableTable disposablePropertyTable =
                new DisposableScrollableTable(module.getProject(), Sizes.Table.DEFAULT, disposableTableModel, factory);

        ClickableLabel.OnClickAction removeAction = disposablePropertyTable::removeSelectedRow;
        ClickableLabel.OnClickAction addAction = () -> {
            ObjectDescriptor.TypeObject newObject = ObjectDescriptor.newInstance();
            boolean added = onAddAction.onAdd(newObject);
            if (added) {
                disposableTableModel.addRow(new Object[]{newObject});
            }
        };

        JPanel actionPanel = new TableActionPanel(addAction, removeAction);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(actionPanel);
        add(disposablePropertyTable);
    }

    public interface ListCustomObjectAddItemAction {
        boolean onAdd(ObjectDescriptor.TypeObject newObject);
    }
}
