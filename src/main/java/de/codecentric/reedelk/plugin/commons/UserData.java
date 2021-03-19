package de.codecentric.reedelk.plugin.commons;

import com.intellij.openapi.util.Key;
import de.codecentric.reedelk.plugin.service.module.impl.component.ComponentContext;

public class UserData {

    public static final Key<String> MODULE_NAME = Key.create("com.reedelk.plugin.userdata.module.name");
    public static final Key<String> COMPONENT_PROPERTY_PATH = Key.create("com.reedelk.plugin.userdata.component.property.path");
    public static final Key<ComponentContext> COMPONENT_CONTEXT = Key.create("com.reedelk.plugin.userdata.component.context");

    private UserData() {
    }
}
