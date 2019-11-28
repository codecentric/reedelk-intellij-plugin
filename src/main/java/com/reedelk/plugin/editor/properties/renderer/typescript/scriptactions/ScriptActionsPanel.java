package com.reedelk.plugin.editor.properties.renderer.typescript.scriptactions;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.service.module.impl.ScriptResource;

public class ScriptActionsPanel extends DisposablePanel {

    private transient final ActionDeleteScript deleteAction;
    private transient final ActionEditScript editAction;
    private transient final ActionAddScript addAction;

    public ScriptActionsPanel(Module module) {
        deleteAction = new ActionDeleteScript(module);
        editAction = new ActionEditScript(module);
        addAction = new ActionAddScript(module);
        add(editAction);
        add(deleteAction);
        add(addAction);
    }

    public void onSelect(ScriptResource value) {
        editAction.onSelect(value);
        deleteAction.onSelect(value);
    }
}
