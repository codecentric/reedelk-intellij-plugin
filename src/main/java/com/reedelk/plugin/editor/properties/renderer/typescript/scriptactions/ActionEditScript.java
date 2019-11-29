package com.reedelk.plugin.editor.properties.renderer.typescript.scriptactions;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.editor.properties.commons.ClickableLabel;
import com.reedelk.plugin.service.module.impl.ScriptResource;

import java.awt.event.MouseEvent;

import static com.reedelk.plugin.commons.Icons.Config.Edit;
import static com.reedelk.plugin.commons.Icons.Config.EditDisabled;

class ActionEditScript extends ClickableLabel {

    private final Module module;

    private ScriptResource selected;

    ActionEditScript(Module module) {
        super("Edit", Edit, EditDisabled);
        this.module = module;
    }

    @Override
    public void mouseClicked(MouseEvent event) {
        if (selected.isEditable()) {
            EditScriptDialog dialog = new EditScriptDialog(module, selected.getPath());
            dialog.show();
        }
    }

    void onSelect(ScriptResource value) {
        setEnabled(value.isEditable());
        this.selected = value;
    }
}
