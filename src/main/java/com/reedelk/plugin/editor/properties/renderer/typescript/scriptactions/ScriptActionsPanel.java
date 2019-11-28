package com.reedelk.plugin.editor.properties.renderer.typescript.scriptactions;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;

public class ScriptActionsPanel extends DisposablePanel {

    private transient final ActionDeleteScript deleteAction;
    private transient final ActionEditScript editAction;
    private transient final ActionAddScript addAction;

    public ScriptActionsPanel(Module module) {
        deleteAction = new ActionDeleteScript();
        editAction = new ActionEditScript(module);
        addAction = new ActionAddScript();
        add(editAction);
        add(deleteAction);
        add(addAction);
    }

    public void onSelect(String value) {
        editAction.onSelect(value);
    }
}
