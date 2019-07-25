package com.esb.plugin.configuration.widget;

import com.esb.plugin.component.domain.TypeObjectDescriptor;
import com.esb.plugin.editor.properties.PropertiesBasePanel;
import com.esb.plugin.service.module.impl.ConfigMetadata;
import com.intellij.openapi.module.Module;

public class ConfigControlPanel extends PropertiesBasePanel {

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

    public void setAddActionListener(ActionAddConfiguration.AddCompleteListener completeListener) {
        addAction.addListener(completeListener);
    }

    public void setEditActionListener(ActionEditConfiguration.EditCompleteListener completeListener) {
        editAction.addListener(completeListener);
    }

    public void setDeleteActionListener(ActionDeleteConfiguration.DeleteCompleteListener completeListener) {
        deleteAction.addListener(completeListener);
    }

    public void onSelect(ConfigMetadata matchingMetadata) {
        editAction.onSelect(matchingMetadata);
        deleteAction.onSelect(matchingMetadata);
    }
}
