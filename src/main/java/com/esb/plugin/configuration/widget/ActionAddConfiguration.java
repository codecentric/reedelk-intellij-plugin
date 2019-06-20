package com.esb.plugin.configuration.widget;

import com.esb.plugin.component.domain.TypeObjectDescriptor;
import com.intellij.openapi.module.Module;

import static com.esb.plugin.commons.Icons.Config.Add;

public class ActionAddConfiguration extends ActionableCommandButton {

    public ActionAddConfiguration(Module module, TypeObjectDescriptor typeDescriptor) {
        super("Add", Add);
        addListener(metadata -> {
            DialogAddConfiguration dialogAddConfiguration = new DialogAddConfiguration(module, typeDescriptor);
            if (dialogAddConfiguration.showAndGet()) {
                dialogAddConfiguration.save();
            }
        });
    }
}
