package com.reedelk.plugin.service.module.impl.component;

import com.reedelk.module.descriptor.model.ModuleDescriptor;

import java.util.Collection;

public interface ComponentListUpdateNotifier {

    void onComponentListUpdate(Collection<ModuleDescriptor> components);
}
