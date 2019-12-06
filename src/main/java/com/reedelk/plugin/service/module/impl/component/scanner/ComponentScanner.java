package com.reedelk.plugin.service.module.impl.component.scanner;

import com.intellij.openapi.diagnostic.Logger;
import com.reedelk.plugin.commons.PackageToPath;
import com.reedelk.plugin.component.domain.ComponentDescriptor;
import com.reedelk.runtime.api.annotation.ESBComponent;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.reedelk.plugin.component.domain.ComponentType.UNKNOWN;
import static com.reedelk.plugin.message.ReedelkBundle.message;


public class ComponentScanner {

    private final Logger LOG = Logger.getInstance(ComponentScanner.class);

    public List<ComponentDescriptor> from(Package targetPackage) {
        ScanResult scanResult = instantiateScanner()
                .whitelistPaths(PackageToPath.convert(targetPackage.getName()))
                .scan();
        List<ComponentDescriptor> descriptors = componentDescriptorsFrom(scanResult);
        // Unknown components are filtered out
        return filterOutUnknownClassComponents(descriptors);
    }

    public List<ComponentDescriptor> from(String targetPath) {
        ScanResult scanResult = instantiateScanner()
                .overrideClasspath(targetPath)
                .scan();
        List<ComponentDescriptor> descriptors = componentDescriptorsFrom(scanResult);
        // Unknown components are filtered out
        return filterOutUnknownClassComponents(descriptors);
    }

    private List<ComponentDescriptor> componentDescriptorsFrom(ScanResult scanResult) {
        ClassInfoList classInfoList = scanResult.getClassesWithAnnotation(ESBComponent.class.getName());
        List<ComponentDescriptor> componentDescriptors = new ArrayList<>();
        classInfoList.forEach(classInfo -> {
            try {
                ComponentAnalyzer componentAnalyzer = ComponentAnalyzerFactory.get(scanResult);
                ComponentDescriptor descriptor = componentAnalyzer.analyze(classInfo);
                componentDescriptors.add(descriptor);
            } catch (Exception exception) {
                String message = message("component.scanner.error.scan.component", classInfo.getName(), exception.getMessage());
                LOG.error(message, exception);
            }
        });
        return componentDescriptors;
    }

    private ClassGraph instantiateScanner() {
        return new ClassGraph()
                .enableFieldInfo()
                .enableAnnotationInfo()
                .ignoreFieldVisibility();
    }

    private static List<ComponentDescriptor> filterOutUnknownClassComponents(List<ComponentDescriptor> componentDescriptorList) {
        return componentDescriptorList.stream()
                .filter(descriptor -> !UNKNOWN.equals(descriptor.getComponentType()))
                .collect(Collectors.toList());
    }
}
