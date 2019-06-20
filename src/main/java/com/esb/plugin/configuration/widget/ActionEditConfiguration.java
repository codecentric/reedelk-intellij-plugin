package com.esb.plugin.configuration.widget;

import com.esb.plugin.component.domain.TypeObjectDescriptor;
import com.intellij.openapi.module.Module;

import static com.esb.plugin.commons.Icons.Config.Edit;

public class ActionEditConfiguration extends ActionableCommandButton {

    public ActionEditConfiguration(Module module, TypeObjectDescriptor typeDescriptor) {
        super("Edit", Edit);
        addListener(metadata -> {
            DialogEditConfiguration dialogEditConfiguration = new DialogEditConfiguration(module, typeDescriptor, metadata);
            if (dialogEditConfiguration.showAndGet()) {
                dialogEditConfiguration.save();
            }
        });
    }
}
