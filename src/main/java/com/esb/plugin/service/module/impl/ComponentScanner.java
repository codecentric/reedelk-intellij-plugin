package com.esb.plugin.service.module.impl;

import com.esb.api.annotation.EsbComponent;
import com.esb.plugin.component.ComponentDescriptor;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ComponentScanner {

    public CompletableFuture<Void> scan(Consumer<List<ComponentDescriptor>> callback, String classPath) {
        return CompletableFuture.supplyAsync(() -> {

            ScanResult scanResult = new ClassGraph().overrideClasspath(classPath)
                    .enableSystemJarsAndModules()
                    .enableAllInfo() // TODO: Enable all info must be fixed
                    .scan();

            processScanResult(callback, scanResult);

            return null;
        });
    }

    public CompletableFuture<Void> scanPackages(Consumer<List<ComponentDescriptor>> callback, String... packages) {
        return CompletableFuture.supplyAsync(() -> {

            ScanResult scanResult = new ClassGraph()
                    .whitelistPackages(packages)
                    .enableAllInfo() // TODO: Enable all info must be fixed
                    .scan();

            processScanResult(callback, scanResult);

            return null;
        });
    }

    private void processScanResult(Consumer<List<ComponentDescriptor>> callback, ScanResult scanResult) {
        List<ComponentDescriptor> componentDescriptors = new ArrayList<>();
        ClassInfoList classInfoList = scanResult.getClassesWithAnnotation(EsbComponent.class.getName());
        for (ClassInfo classInfo : classInfoList) {
            ComponentAnalyzer componentAnalyzer = new ComponentAnalyzer();
            ComponentDescriptor descriptor = componentAnalyzer.analyze(classInfo);
            componentDescriptors.add(descriptor);
        }

        callback.accept(componentDescriptors);
    }

}
