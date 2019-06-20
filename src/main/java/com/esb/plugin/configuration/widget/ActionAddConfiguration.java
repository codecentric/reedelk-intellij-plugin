package com.esb.plugin.configuration.widget;

import com.esb.plugin.component.domain.TypeObjectDescriptor;
import com.esb.plugin.graph.serializer.JsonObjectFactory;
import com.esb.plugin.service.module.impl.ConfigMetadata;
import com.esb.plugin.service.module.impl.NewConfigMetadata;
import com.intellij.openapi.module.Module;
import org.json.JSONObject;

import java.util.UUID;

import static com.esb.internal.commons.JsonParser.Config;
import static com.esb.internal.commons.JsonParser.Implementor;
import static com.esb.plugin.commons.Icons.Config.Add;

public class ActionAddConfiguration extends ActionableCommandButton {

    public ActionAddConfiguration(Module module, TypeObjectDescriptor typeDescriptor) {
        super("Add", Add);
        addListener(metadata -> {

            // We ignore the selected. Create new config object.
            JSONObject newConfigJsonObject = JsonObjectFactory.newJSONObject();

            Implementor.name(typeDescriptor.getTypeFullyQualifiedName(), newConfigJsonObject);

            Config.id(UUID.randomUUID().toString(), newConfigJsonObject);

            Config.title("New configuration", newConfigJsonObject);

            String defaultConfigFileName = "test_config.fconfig";

            ConfigMetadata newConfig = new NewConfigMetadata(defaultConfigFileName, newConfigJsonObject);

            DialogAddConfiguration dialogAddConfiguration = new DialogAddConfiguration(module, typeDescriptor, newConfig);

            if (dialogAddConfiguration.showAndGet()) {
                dialogAddConfiguration.add();
            }
        });
    }


}
