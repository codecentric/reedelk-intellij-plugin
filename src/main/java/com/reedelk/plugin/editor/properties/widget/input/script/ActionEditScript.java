package com.reedelk.plugin.editor.properties.widget.input.script;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.editor.properties.widget.ClickableLabel;

import java.awt.event.MouseEvent;

import static com.reedelk.plugin.commons.Icons.Config.Edit;
import static com.reedelk.plugin.commons.Icons.Config.EditDisabled;

class ActionEditScript extends ClickableLabel implements ClickableLabel.OnClickAction {

    private final Module module;
    private final ScriptContextManager contextManager;

    private String selected;

    ActionEditScript(Module module, ScriptContextManager contextManager) {
        super("Edit", Edit, EditDisabled);
        this.module = module;
        this.contextManager = contextManager;
    }

    public void onSelect(String value) {
        this.selected = value;
    }

    @Override
    public void mouseClicked(MouseEvent event) {
        EditScriptDialog dialog = new EditScriptDialog(module, contextManager, selected);
        dialog.show();
    }

    @Override
    public void onClick() {

    }
}
