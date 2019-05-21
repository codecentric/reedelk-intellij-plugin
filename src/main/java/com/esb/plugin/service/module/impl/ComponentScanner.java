package com.esb.plugin.service.module.impl;

import com.esb.api.annotation.ESBComponent;
import com.esb.plugin.component.ComponentDescriptor;
import com.intellij.openapi.diagnostic.Logger;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static java.lang.String.format;

class ComponentScanner {

    private static final Logger LOG = Logger.getInstance(ComponentScanner.class);

    CompletableFuture<Void> scan(Consumer<List<ComponentDescriptor>> callback, String classPath) {
        return CompletableFuture.supplyAsync(() -> {
            ScanResult scanResult = instantiateScanner()
                    .overrideClasspath(classPath)
                    .scan();
            processScanResult(callback, scanResult);
            return null;
        });
    }

    public List<ComponentDescriptor> getComponentsFromPackage(String packageName) {
        final List<ComponentDescriptor> descriptors = new ArrayList<>();
        ScanResult scanResult = instantiateScanner()
                .whitelistPackages(packageName)
                .scan();
        processScanResult(descriptors::addAll, scanResult);
        return descriptors;
    }

    private void processScanResult(Consumer<List<ComponentDescriptor>> callback, ScanResult scanResult) {
        List<ComponentDescriptor> componentDescriptors = new ArrayList<>();
        ClassInfoList classInfoList = scanResult.getClassesWithAnnotation(ESBComponent.class.getName());
        for (ClassInfo classInfo : classInfoList) {
            try {
                ComponentAnalyzerContext context = new ComponentAnalyzerContext(scanResult);
                ComponentAnalyzer componentAnalyzer = new ComponentAnalyzer(context);
                ComponentDescriptor descriptor = componentAnalyzer.analyze(classInfo);
                componentDescriptors.add(descriptor);
            } catch (Exception e) {
                LOG.error(format("Error, while processing component " +
                        "definition with qualified name '%s'", classInfo.getName()), e);
            }
        }
        callback.accept(componentDescriptors);
    }

    private ClassGraph instantiateScanner() {
        return new ClassGraph()
                .enableFieldInfo()
                .enableAnnotationInfo()
                .ignoreFieldVisibility();
    }

}
