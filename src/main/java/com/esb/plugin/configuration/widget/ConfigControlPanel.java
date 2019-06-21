package com.esb.plugin.configuration.widget;

import com.esb.plugin.component.domain.TypeObjectDescriptor;
import com.esb.plugin.service.module.impl.ConfigMetadata;
import com.intellij.openapi.module.Module;
import com.intellij.ui.components.JBPanel;

public class ConfigControlPanel extends JBPanel {

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

    public void setDeleteActionListener(ActionDeleteConfiguration.DeleteCompleteListener completeListener) {
        deleteAction.addListener(completeListener);
    }

    public void onSelect(ConfigMetadata matchingMetadata) {
        editAction.onSelect(matchingMetadata);
        deleteAction.onSelect(matchingMetadata);
    }
}
