package com.esb.plugin.component.scanner;

import com.esb.plugin.commons.ESBModuleInfo;
import com.esb.plugin.component.domain.ComponentDescriptor;
import com.esb.plugin.component.domain.ComponentsDescriptor;

import java.util.List;


public class ComponentsScanner {

    public ComponentsDescriptor analyze(String jarFilePath) {
        List<ComponentDescriptor> components = ComponentScanner.scan(jarFilePath);
        String moduleName = ESBModuleInfo.GetESBModuleName(jarFilePath);
        return new ComponentsDescriptor(moduleName, components);
    }
}
