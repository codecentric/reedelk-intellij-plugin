package com.reedelk.plugin.service.module.impl.component;

import java.util.Collection;

public interface ComponentListUpdateNotifier {

    void onComponentListUpdate(Collection<ModuleDTO> modules);
}
