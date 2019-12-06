package com.reedelk.plugin.editor.properties.renderer.typescript.scriptactions;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.editor.properties.commons.ClickableLabel;
import com.reedelk.plugin.editor.properties.commons.DialogConfirmAction;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.service.module.ScriptService;
import com.reedelk.plugin.service.module.impl.ScriptResource;

import static com.intellij.icons.AllIcons.Actions.EditSource;
import static com.intellij.icons.AllIcons.General.Add;
import static com.intellij.icons.AllIcons.General.Remove;
import static com.reedelk.plugin.message.ReedelkBundle.message;

public class ScriptActionsPanel extends DisposablePanel {

    private transient final Module module;
    private transient final ClickableLabel editAction;
    private transient final ClickableLabel deleteAction;
    private transient ScriptResource selected;

    public ScriptActionsPanel(Module module) {
        this.module = module;
        deleteAction = new ClickableLabel(message("script.actions.btn.delete"), Remove, Remove, this::deleteScript);
        editAction = new ClickableLabel(message("script.actions.btn.edit"), EditSource, EditSource, this::editScript);
        ClickableLabel addAction = new ClickableLabel(message("script.actions.btn.add"), Add, Add, this::addScript);
        add(editAction);
        add(addAction);
        add(deleteAction);
    }

    private void deleteScript() {
        DialogConfirmAction dialogConfirmDelete =
                new DialogConfirmAction(module,
                        message("script.dialog.delete.title"),
                        message("script.dialog.delete.confirm.message"));

        if (dialogConfirmDelete.showAndGet()) {
            ScriptService.getInstance(module).removeScript(selected.getPath());
        }
    }

    private void editScript() {
        if (selected.isEditable()) {
            EditScriptDialog dialog = new EditScriptDialog(module, selected.getPath());
            dialog.show();
        }
    }

    private void addScript() {
        DialogAddScript dialogAddScript = new DialogAddScript(module.getProject());
        if (dialogAddScript.showAndGet()) {
            ScriptService.getInstance(module).addScript(dialogAddScript.getScriptFileNameWithPathToAdd());
        }
    }

    public void onSelect(ScriptResource newSelected) {
        this.selected = newSelected;
        this.editAction.setEnabled(newSelected.isEditable());
        this.deleteAction.setEnabled(newSelected.isRemovable());
    }
}
