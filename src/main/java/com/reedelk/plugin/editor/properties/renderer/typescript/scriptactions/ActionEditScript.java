package com.reedelk.plugin.editor.properties.renderer.typescript.scriptactions;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.editor.properties.commons.ClickableLabel;
import com.reedelk.plugin.service.module.impl.ScriptResource;

import java.awt.event.MouseEvent;

import static com.reedelk.plugin.commons.Icons.Config.Edit;
import static com.reedelk.plugin.commons.Icons.Config.EditDisabled;

class ActionEditScript extends ClickableLabel implements ClickableLabel.OnClickAction {

    private final Module module;

    private ScriptResource selected;

    ActionEditScript(Module module) {
        super("Edit", Edit, EditDisabled);
        this.module = module;
    }

    @Override
    public void mouseClicked(MouseEvent event) {
        // We only show the dialog if the button is enabled.
        // Otherwise it means that nothing has been selected or that
        // the current selected item is a script not found in the
        // project's resources folder.
        if (selected.isEditable()) {
            EditScriptDialog dialog = new EditScriptDialog(module, selected.getPath());
            dialog.show();
        }
    }

    @Override
    public void onClick() {

    }

    public void onSelect(ScriptResource value) {
        setEnabled(value.isEditable());
        this.selected = value;
    }
}
