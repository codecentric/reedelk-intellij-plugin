package com.reedelk.plugin.service.module.impl.component.scanner;

import com.reedelk.plugin.service.module.impl.commons.ScannerUtil;
import com.reedelk.runtime.api.annotation.AutoCompleteContributor;
import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;

import java.util.ArrayList;
import java.util.List;

public class AutoCompleteContributorScanner {

    public List<String> from(ScanResult scanResult) {
        ClassInfoList classInfoList = scanResult.getClassesWithAnnotation(AutoCompleteContributor.class.getName());
        List<String> contributions = new ArrayList<>();
        classInfoList.forEach(classInfo -> {
            AnnotationInfo annotationInfo = classInfo.getAnnotationInfo(AutoCompleteContributor.class.getName());
            contributions.addAll(ScannerUtil.stringListParameterValueFrom(annotationInfo, "contributions"));
        });
        return contributions;
    }
}
