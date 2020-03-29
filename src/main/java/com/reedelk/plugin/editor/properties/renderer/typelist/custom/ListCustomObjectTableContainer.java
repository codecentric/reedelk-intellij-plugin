package com.reedelk.plugin.editor.properties.renderer.typelist.custom;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.TypeObjectDescriptor;
import com.reedelk.plugin.commons.Sizes;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.commons.*;
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

        DisposableTable disposablePropertyTable =
                new DisposableTable(module.getProject(), Sizes.Table.DEFAULT, disposableTableModel, factory);

        ClickableLabel.OnClickAction removeAction = disposablePropertyTable::removeSelectedRow;
        ClickableLabel.OnClickAction addAction = () -> {
            TypeObjectDescriptor.TypeObject newObject = TypeObjectDescriptor.newInstance();
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
        boolean onAdd(TypeObjectDescriptor.TypeObject newObject);
    }
}
