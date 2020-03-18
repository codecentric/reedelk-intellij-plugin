package com.reedelk.plugin.editor.properties.commons;

import static com.intellij.icons.AllIcons.Actions.EditSource;
import static com.intellij.icons.AllIcons.General.Add;
import static com.intellij.icons.AllIcons.General.Remove;
import static com.reedelk.plugin.message.ReedelkBundle.message;

public abstract class ComboActionsPanel<T> extends DisposablePanel {

    private final transient ClickableLabel addAction;
    private final transient ClickableLabel editAction;
    private final transient ClickableLabel deleteAction;

    private transient T item;

    public ComboActionsPanel() {
        editAction = new ClickableLabel(message("config.actions.btn.edit"), EditSource, EditSource, this::edit);
        deleteAction = new ClickableLabel(message("config.actions.btn.delete"), Remove, Remove, this::delete);
        addAction = new ClickableLabel(message("config.actions.btn.add"), Add, Add, this::add);
        add(editAction);
        add(addAction);
        add(deleteAction);
    }

    public void select(T selectedItem) {
        this.item = selectedItem;
        onSelect(this.item);
    }

    private void add() {
        onAdd(item);
    }

    protected void edit() {
        onEdit(item);
    }

    protected void delete() {
        onDelete(item);
    }

    protected void enableAdd(boolean enabled) {
        addAction.setEnabled(enabled);
    }

    protected void enableEdit(boolean enabled) {
        editAction.setEnabled(enabled);
    }

    protected void enableDelete(boolean enabled) {
        deleteAction.setEnabled(enabled);
    }

    protected abstract void onSelect(T selectedItem);

    protected abstract void onAdd(T item);

    protected abstract void onEdit(T item);

    protected abstract void onDelete(T item);
}
