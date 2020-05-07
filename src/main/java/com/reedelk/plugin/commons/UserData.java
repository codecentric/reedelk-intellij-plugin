package com.reedelk.plugin.commons;

import com.intellij.openapi.util.Key;
import com.reedelk.plugin.editor.properties.context.ContainerContext;

public class UserData {

    public static final Key<String> MODULE_NAME = Key.create("com.reedelk.plugin.userdata.module.name");
    public static final Key<String> COMPONENT_PROPERTY_PATH = Key.create("com.reedelk.plugin.userdata.component.property.path");
    public static final Key<ContainerContext> PROPERTY_CONTEXT = Key.create("com.reedelk.plugin.userdata.component.property.context");

    private UserData() {
    }
}
