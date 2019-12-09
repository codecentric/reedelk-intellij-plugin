package com.reedelk.plugin.service.module.impl.component.scanner;

import com.intellij.openapi.module.Module;

public interface ComponentListUpdateNotifier {

    void onComponentListUpdate(Module module);

}
