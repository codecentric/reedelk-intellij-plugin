package de.codecentric.reedelk.plugin.editor.properties.renderer.typelist.custom;

import de.codecentric.reedelk.plugin.editor.properties.commons.*;
import de.codecentric.reedelk.plugin.editor.properties.context.PropertyAccessor;
import com.intellij.openapi.module.Module;
import de.codecentric.reedelk.module.descriptor.model.property.ObjectDescriptor;
import de.codecentric.reedelk.plugin.commons.Sizes;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;


public class ListCustomObjectTableContainer extends DisposablePanel {

    public ListCustomObjectTableContainer(@NotNull Module module,
                                          @NotNull String listDisplayPropertyName,
                                          @NotNull ListCustomObjectAddItemAction onAddAction,
                                          @NotNull TableEditButtonCellEditor.TableCustomEditButtonAction action,
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
