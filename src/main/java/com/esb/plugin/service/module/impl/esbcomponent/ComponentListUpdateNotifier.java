package com.esb.plugin.service.module.impl.esbcomponent;

import com.intellij.util.messages.Topic;

public interface ComponentListUpdateNotifier {

    Topic<ComponentListUpdateNotifier> COMPONENT_LIST_UPDATE_TOPIC = Topic.create("Component Update", ComponentListUpdateNotifier.class);

    void onComponentListUpdate();

}
