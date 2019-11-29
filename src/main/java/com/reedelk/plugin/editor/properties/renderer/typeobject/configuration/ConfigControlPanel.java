package com.reedelk.plugin.editor.properties.renderer.typeobject.configuration;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.component.domain.TypeObjectDescriptor;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.service.module.impl.ConfigMetadata;

public class ConfigControlPanel extends DisposablePanel {

    private final ActionDeleteConfiguration deleteAction;
    private final ActionEditConfiguration editAction;
    private final ActionAddConfiguration addAction;

    public ConfigControlPanel(Module module, TypeObjectDescriptor typeDescriptor) {
        deleteAction = new ActionDeleteConfiguration(module);
        editAction = new ActionEditConfiguration(module, typeDescriptor);
        addAction = new ActionAddConfiguration(module, typeDescriptor);
        add(editAction);
        add(deleteAction);
        add(addAction);
    }

    public void onSelect(ConfigMetadata matchingMetadata) {
        editAction.onSelect(matchingMetadata);
        deleteAction.onSelect(matchingMetadata);
    }
}
