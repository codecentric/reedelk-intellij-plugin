package com.reedelk.plugin.service.module.impl.component.scanner;

import com.intellij.openapi.module.Module;
import com.intellij.util.messages.Topic;

public interface ComponentListUpdateNotifier {

    Topic<ComponentListUpdateNotifier> COMPONENT_LIST_UPDATE_TOPIC = Topic.create("Component Update", ComponentListUpdateNotifier.class);

    void onComponentListUpdate(Module module);

}
