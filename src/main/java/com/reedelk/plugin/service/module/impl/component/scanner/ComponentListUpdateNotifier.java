package com.reedelk.plugin.service.module.impl.component.scanner;

import com.reedelk.plugin.service.module.impl.component.ModuleComponents;

import java.util.Collection;

public interface ComponentListUpdateNotifier {

    void onComponentListUpdate(Collection<ModuleComponents> components);

}
