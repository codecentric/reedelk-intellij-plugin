package com.reedelk.plugin.service.module.impl.component.scanner;

import com.intellij.openapi.module.Module;

public interface ComponentListUpdateNotifier {

    // TODO: This is wrong, don't pass the module, just pass the name
    //  of the module.
    void onComponentListUpdate(Module module);

}
