package com.reedelk.plugin.editor.properties.renderer.typescript;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.editor.properties.widget.DisposablePanel;

public class ScriptControlPanel extends DisposablePanel {

    private final ActionDeleteScript deleteAction;
    private final ActionEditScript editAction;
    private final ActionAddScript addAction;

    public ScriptControlPanel(Module module) {
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
