package com.reedelk.plugin.editor.properties.renderer.typescript;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.editor.properties.commons.ClickableLabel;

import java.awt.event.MouseEvent;

import static com.reedelk.plugin.commons.Icons.Config.Edit;
import static com.reedelk.plugin.commons.Icons.Config.EditDisabled;

class ActionEditScript extends ClickableLabel implements ClickableLabel.OnClickAction {

    private final Module module;

    private String selected;

    ActionEditScript(Module module) {
        super("Edit", Edit, EditDisabled);
        this.module = module;
    }

    public void onSelect(String value) {
        this.selected = value;
    }

    @Override
    public void mouseClicked(MouseEvent event) {
        EditScriptDialog dialog = new EditScriptDialog(module, selected);
        dialog.show();
    }

    @Override
    public void onClick() {

    }
}
