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
            ScanResult scanResult = new ClassGraph()
                    .overrideClasspath(classPath)
                    .enableSystemJarsAndModules()
                    .enableClassInfo()
                    .enableFieldInfo()
                    .enableAnnotationInfo()
                    .ignoreFieldVisibility()
                    .scan();
            processScanResult(callback, scanResult);
            return null;
        });
    }

    CompletableFuture<Void> scanPackages(Consumer<List<ComponentDescriptor>> callback, String... packages) {
        return CompletableFuture.supplyAsync(() -> {
            ScanResult scanResult = new ClassGraph()
                    .whitelistPackages(packages)
                    .enableClassInfo()
                    .enableFieldInfo()
                    .enableAnnotationInfo()
                    .ignoreFieldVisibility()
                    .scan();
            processScanResult(callback, scanResult);
            return null;
        });
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

}
