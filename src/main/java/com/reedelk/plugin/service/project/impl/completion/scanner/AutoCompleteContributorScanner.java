package com.reedelk.plugin.service.project.impl.completion.scanner;

import com.intellij.openapi.diagnostic.Logger;
import com.reedelk.runtime.api.annotation.AutoCompleteContributor;
import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;

public class AutoCompleteContributorScanner {

    private final Logger LOG = Logger.getInstance(AutoCompleteContributorScanner.class);

    public void from(String targetPath) {
        ScanResult scanResult = instantiateScanner()
                .overrideClasspath(targetPath)
                .scan();


    }

    private void scanAutoCompleteContributors(ScanResult scanResult) {
        ClassInfoList classInfoList = scanResult.getClassesWithAnnotation(AutoCompleteContributor.class.getName());
        classInfoList.forEach(classInfo -> {
            AnnotationInfo annotationInfo = classInfo.getAnnotationInfo(AutoCompleteContributor.class.getName());
            // Collect all the
        });
    }

    private ClassGraph instantiateScanner() {
        return new ClassGraph()
                .enableFieldInfo()
                .enableAnnotationInfo()
                .ignoreFieldVisibility();
    }
}
