package com.esb.plugin.configuration.widget;

import com.esb.plugin.component.domain.TypeObjectDescriptor;
import com.esb.plugin.service.module.impl.ConfigMetadata;
import com.intellij.openapi.module.Module;
import org.json.JSONObject;

import java.util.UUID;

import static com.esb.plugin.commons.Icons.Config.Add;

public class ActionAddConfiguration extends ActionableCommandButton {

    public ActionAddConfiguration(Module module, TypeObjectDescriptor typeDescriptor) {
        super("Add", Add);
        addListener(metadata -> {
            // We ignore the selected. Create new config object.
            //final String fullyQualifiedName, String id, String title, VirtualFile virtualFile, JSONObject configDefinition
            ConfigMetadata newConfig = new ConfigMetadata(
                    typeDescriptor.getTypeFullyQualifiedName(), UUID.randomUUID().toString(), "New config", null, new JSONObject());
            DialogAddConfiguration dialogAddConfiguration = new DialogAddConfiguration(module, typeDescriptor, newConfig);
            if (dialogAddConfiguration.showAndGet()) {
                dialogAddConfiguration.add();
            }
        });
    }
}
