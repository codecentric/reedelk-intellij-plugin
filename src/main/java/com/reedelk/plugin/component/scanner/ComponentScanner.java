package com.reedelk.plugin.component.scanner;

import com.intellij.openapi.diagnostic.Logger;
import com.reedelk.plugin.commons.PackageToPath;
import com.reedelk.plugin.component.domain.ComponentDescriptor;
import com.reedelk.runtime.api.annotation.ESBComponent;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.reedelk.plugin.component.domain.ComponentClass.UNKNOWN;
import static java.lang.String.format;


public class ComponentScanner {

    private final Logger LOG = Logger.getInstance(ComponentScanner.class);

    public List<ComponentDescriptor> from(Package targetPackage) {
        ScanResult scanResult = instantiateScanner()
                .whitelistPaths(PackageToPath.convert(targetPackage.getName()))
                .scan();

        List<ComponentDescriptor> packageComponentDescriptors =
                processScanResult(scanResult);

        // Unknown components are filtered out
        return filterOutUnknownClassComponents(packageComponentDescriptors);
    }

    public List<ComponentDescriptor> from(String targetPath) {
        ScanResult scanResult = instantiateScanner()
                .overrideClasspath(targetPath)
                .scan();

        List<ComponentDescriptor> targetPathComponentDescriptors =
                processScanResult(scanResult);

        // Unknown components are filtered out
        return filterOutUnknownClassComponents(targetPathComponentDescriptors);
    }

    private List<ComponentDescriptor> processScanResult(ScanResult scanResult) {
        List<ComponentDescriptor> componentDescriptors = new ArrayList<>();
        ClassInfoList classInfoList = scanResult.getClassesWithAnnotation(ESBComponent.class.getName());
        for (ClassInfo classInfo : classInfoList) {
            try {
                ComponentAnalyzer componentAnalyzer = ComponentAnalyzerFactory.get(scanResult);
                ComponentDescriptor descriptor = componentAnalyzer.analyze(classInfo);
                componentDescriptors.add(descriptor);
            } catch (Exception e) {
                LOG.error(format("Error, while processing component " +
                        "definition with qualified name '%s'", classInfo.getName()), e);
            }
        }
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
                .filter(descriptor -> !UNKNOWN.equals(descriptor.getComponentClass()))
                .collect(Collectors.toList());
    }
}
