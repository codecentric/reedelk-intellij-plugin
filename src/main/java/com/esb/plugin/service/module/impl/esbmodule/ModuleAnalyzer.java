package com.esb.plugin.service.module.impl.esbmodule;

import com.esb.plugin.commons.ESBModuleInfo;
import com.esb.plugin.component.ComponentDescriptor;
import com.esb.plugin.service.module.impl.esbcomponent.ComponentScanner;

import java.util.List;


public class ModuleAnalyzer {

    public ModuleDescriptor analyze(String jarFilePath) {
        List<ComponentDescriptor> components = ComponentScanner.scan(jarFilePath);
        String moduleName = ESBModuleInfo.GetESBModuleName(jarFilePath);
        return new ModuleDescriptor(moduleName, components);
    }
}
