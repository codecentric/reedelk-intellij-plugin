package com.esb.plugin.service.module.impl;

import com.intellij.util.messages.Topic;

public interface ComponentListUpdateNotifier {

    Topic<ComponentListUpdateNotifier> TOPIC = Topic.create("Component Update", ComponentListUpdateNotifier.class);

    void onComponentListUpdate();

}
