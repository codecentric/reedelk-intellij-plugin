package com.reedelk.plugin.service.module.impl.component;

import com.reedelk.module.descriptor.ModuleDescriptor;

import java.util.Collection;

public interface ComponentListUpdateNotifier {

    void onComponentListUpdate(Collection<ModuleDescriptor> components);
}
