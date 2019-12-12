package com.reedelk.plugin.editor.properties.renderer.typescript.scriptactions;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.commons.ScriptFunctionDefinitionBuilder;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.component.domain.ScriptSignatureDefinition;
import com.reedelk.plugin.editor.properties.commons.ClickableLabel;
import com.reedelk.plugin.editor.properties.commons.ContainerContext;
import com.reedelk.plugin.editor.properties.commons.DialogConfirmAction;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.service.module.ScriptService;
import com.reedelk.plugin.service.module.impl.script.ScriptResource;

import static com.intellij.icons.AllIcons.Actions.EditSource;
import static com.intellij.icons.AllIcons.General.Add;
import static com.intellij.icons.AllIcons.General.Remove;
import static com.reedelk.plugin.message.ReedelkBundle.message;

public class ScriptActionsPanel extends DisposablePanel {

    private final transient Module module;
    private final transient ContainerContext context;
    private final transient ClickableLabel editAction;
    private final transient ClickableLabel deleteAction;
    private final ComponentPropertyDescriptor propertyDescriptor;
    private transient ScriptResource selected;

    public ScriptActionsPanel(Module module, ContainerContext context, ComponentPropertyDescriptor propertyDescriptor) {
        this.module = module;
        this.context = context;
        this.propertyDescriptor = propertyDescriptor;
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
            DialogEditScript dialog = new DialogEditScript(module, selected.getPath(), context);
            dialog.show();
        }
    }

    private void addScript() {
        DialogAddScript dialogAddScript = new DialogAddScript(module.getProject());
        if (dialogAddScript.showAndGet()) {
            String scriptFileName = dialogAddScript.getScriptFileNameWithPathToAdd();
            ScriptSignatureDefinition signatureDefinition = propertyDescriptor.getScriptSignatureDefinition().orElse(ScriptSignatureDefinition.DEFAULT);
            String scriptBody = ScriptFunctionDefinitionBuilder.from(scriptFileName, signatureDefinition);
            ScriptService.getInstance(module).addScript(scriptFileName, scriptBody);
        }
    }

    public void onSelect(ScriptResource newSelected) {
        this.selected = newSelected;
        this.editAction.setEnabled(newSelected.isEditable());
        this.deleteAction.setEnabled(newSelected.isRemovable());
    }
}
