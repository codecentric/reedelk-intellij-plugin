package com.reedelk.plugin.service.module.impl.completion.scanner;

import com.intellij.openapi.diagnostic.Logger;
import com.reedelk.runtime.api.annotation.AutoCompleteContributor;
import io.github.classgraph.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AutoCompleteContributorScanner {

    private final Logger LOG = Logger.getInstance(AutoCompleteContributorScanner.class);

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
            contributions.addAll(getStringListParameterValue(annotationInfo, "contributions"));
        });
        return contributions;
    }

    private List<String> scanComponentsPropertiesWithAutoCompleteContributors(ScanResult scanResult) {
        return new ArrayList<>();
    }

    private List<String> getStringListParameterValue(AnnotationInfo info, String parameterName) {
        AnnotationParameterValueList parameterValues = info.getParameterValues();
        AnnotationParameterValue parameterValue = parameterValues.get(parameterName);
        Object[] array = (Object[]) parameterValue.getValue();
        return Arrays.stream(array).map(o -> (String) o).collect(Collectors.toList());
    }


    private ClassGraph instantiateScanner() {
        return new ClassGraph()
                .enableFieldInfo()
                .enableAnnotationInfo()
                .ignoreFieldVisibility();
    }
}
