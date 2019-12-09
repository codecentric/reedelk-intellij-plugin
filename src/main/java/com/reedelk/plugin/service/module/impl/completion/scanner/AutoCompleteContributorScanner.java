package com.reedelk.plugin.service.module.impl.completion.scanner;

import com.reedelk.plugin.service.module.impl.commons.ScannerUtil;
import com.reedelk.runtime.api.annotation.AutoCompleteContributor;
import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;

import java.util.ArrayList;
import java.util.List;

public class AutoCompleteContributorScanner {

    public List<String> from(String targetPath) {
        ScanResult scanResult = instantiateScanner()
                .overrideClasspath(targetPath)
                .scan();
        return scanClassesWithAutoCompleteContributors(scanResult);
    }

    private List<String> scanClassesWithAutoCompleteContributors(ScanResult scanResult) {
        ClassInfoList classInfoList = scanResult.getClassesWithAnnotation(AutoCompleteContributor.class.getName());
        List<String> contributions = new ArrayList<>();
        classInfoList.forEach(classInfo -> {
            AnnotationInfo annotationInfo = classInfo.getAnnotationInfo(AutoCompleteContributor.class.getName());
            contributions.addAll(ScannerUtil.stringListParameterValueFrom(annotationInfo, "contributions"));
        });
        return contributions;
    }

    private ClassGraph instantiateScanner() {
        return new ClassGraph()
                .enableFieldInfo()
                .enableAnnotationInfo()
                .ignoreFieldVisibility();
    }
}
