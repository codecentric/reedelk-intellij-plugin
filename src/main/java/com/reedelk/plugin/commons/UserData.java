package com.reedelk.plugin.commons;

import com.intellij.openapi.util.Key;

public class UserData {

    public static final Key<String> MODULE_NAME = Key.create("com.reedelk.plugin.userdata.module.name");
    public static final Key<String> COMPONENT_PROPERTY_PATH = Key.create("com.reedelk.plugin.userdata.component.property.path");

    private UserData() {
    }
}
