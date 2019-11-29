package com.reedelk.plugin.editor.properties.renderer.typeobject.configuration;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.component.domain.TypeObjectDescriptor;
import com.reedelk.plugin.editor.properties.commons.ClickableLabel;
import com.reedelk.plugin.service.module.ConfigService;
import com.reedelk.plugin.service.module.impl.ConfigMetadata;
import com.reedelk.plugin.service.module.impl.NewConfigMetadata;
import org.jetbrains.annotations.NotNull;

import java.awt.event.MouseEvent;
import java.util.UUID;

import static com.reedelk.plugin.commons.Icons.Config.Add;
import static com.reedelk.plugin.message.ReedelkBundle.message;
import static com.reedelk.runtime.commons.JsonParser.Config;

public class ActionAddConfiguration extends ClickableLabel {

    private final TypeObjectDescriptor typeDescriptor;
    private final Module module;

    ActionAddConfiguration(@NotNull Module module, @NotNull TypeObjectDescriptor typeDescriptor) {
        super("Add", Add, Add);
        this.typeDescriptor = typeDescriptor;
        this.module = module;
    }

    @Override
    public void mouseClicked(MouseEvent event) {
        // We ignore the selected. Create new config object.
        TypeObjectDescriptor.TypeObject configTypeObject = new TypeObjectDescriptor.TypeObject(typeDescriptor.getTypeFullyQualifiedName());
        configTypeObject.set(Config.id(), UUID.randomUUID().toString());
        configTypeObject.set(Config.title(), message("config.field.title.default"));

        ConfigMetadata newConfig = new NewConfigMetadata(message("config.field.file.default"), configTypeObject, typeDescriptor);
        DialogAddConfiguration dialogAddConfiguration = new DialogAddConfiguration(module, typeDescriptor, newConfig);

        if (dialogAddConfiguration.showAndGet()) {
            ConfigService.getInstance(module).addConfig(newConfig);
        }
    }
}
