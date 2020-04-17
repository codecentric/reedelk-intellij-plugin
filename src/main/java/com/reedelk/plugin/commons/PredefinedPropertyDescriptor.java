package com.reedelk.plugin.commons;

import com.reedelk.module.descriptor.model.PropertyDescriptor;
import com.reedelk.runtime.commons.JsonParser;

import static com.reedelk.plugin.message.ReedelkBundle.message;

public class PredefinedPropertyDescriptor {

    public static final PropertyDescriptor FLOW_TITLE = PropertyDescriptor.builder()
            .description(message("flow.title.description"))
            .displayName(message("flow.metadata.title"))
            .hintValue(message("flow.title.hint"))
            .type(TypePrimitiveDescriptors.STRING)
            .name("title")
            .build();

    public static final PropertyDescriptor FLOW_DESCRIPTION = PropertyDescriptor.builder()
            .description(message("flow.description.description"))
            .displayName(message("flow.metadata.description"))
            .hintValue(message("flow.description.hint"))
            .type(TypePrimitiveDescriptors.STRING)
            .name("description")
            .build();

    public static final PropertyDescriptor UNKNOWN_IMPLEMENTOR = PropertyDescriptor.builder()
            .displayName(message("component.unknown.implementor"))
            .type(TypePrimitiveDescriptors.STRING)
            .name(JsonParser.Implementor.name())
            .build();

    public static final PropertyDescriptor CONFIG_FILE_TITLE = PropertyDescriptor.builder()
            .description(message("config.field.title.description"))
            .example(message("config.field.title.example"))
            .hintValue(message("config.field.title.hint"))
            .displayName(message("config.field.title"))
            .type(TypePrimitiveDescriptors.STRING)
            .name("configTitle")
            .build();

    public static final PropertyDescriptor CONFIG_FILE_NAME = PropertyDescriptor.builder()
            .description(message("config.field.file.description"))
            .example(message("config.field.file.example"))
            .hintValue(message("config.field.file.hint"))
            .displayName(message("config.field.file"))
            .type(TypePrimitiveDescriptors.STRING)
            .name("configFile")
            .build();
}
