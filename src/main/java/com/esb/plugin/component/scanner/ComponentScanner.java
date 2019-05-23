package com.esb.plugin.component.scanner;

import com.esb.api.annotation.ESBComponent;
import com.esb.plugin.commons.PackageToPath;
import com.esb.plugin.component.domain.ComponentDescriptor;
import com.intellij.openapi.diagnostic.Logger;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

public class ComponentScanner {

    private static final Logger LOG = Logger.getInstance(ComponentScanner.class);

    public static List<ComponentDescriptor> scan(String moduleJarFilePath) {
        ScanResult scanResult = instantiateScanner()
                .overrideClasspath(moduleJarFilePath)
                .scan();
        return processScanResult(scanResult);
    }

    public static List<ComponentDescriptor> getComponentsFromPackage(String packageName) {
        ScanResult scanResult = instantiateScanner()
                .whitelistPackages(packageName)
                .whitelistPaths(PackageToPath.convert(packageName))
                .scan();
        return processScanResult(scanResult);
    }

    private static List<ComponentDescriptor> processScanResult(ScanResult scanResult) {
        ComponentIconsAnalyzer extractor = new ComponentIconsAnalyzer(scanResult);

        List<ComponentDescriptor> componentDescriptors = new ArrayList<>();
        ClassInfoList classInfoList = scanResult.getClassesWithAnnotation(ESBComponent.class.getName());
        for (ClassInfo classInfo : classInfoList) {
            try {
                ComponentAnalyzerContext context = new ComponentAnalyzerContext(scanResult, extractor);
                ComponentAnalyzer componentAnalyzer = new ComponentAnalyzer(context);
                ComponentDescriptor descriptor = componentAnalyzer.analyze(classInfo);
                componentDescriptors.add(descriptor);
            } catch (Exception e) {
                LOG.error(format("Error, while processing component " +
                        "definition with qualified name '%s'", classInfo.getName()), e);
            }
        }
        return componentDescriptors;
    }

    private static ClassGraph instantiateScanner() {
        return new ClassGraph()
                .enableFieldInfo()
                .enableAnnotationInfo()
                .ignoreFieldVisibility();
    }

}
