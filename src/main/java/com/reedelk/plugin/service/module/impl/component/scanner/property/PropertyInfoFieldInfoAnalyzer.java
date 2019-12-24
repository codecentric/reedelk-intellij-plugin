package com.reedelk.plugin.service.module.impl.component.scanner.property;

import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.service.module.impl.component.scanner.ComponentAnalyzerContext;
import com.reedelk.plugin.service.module.impl.component.scanner.ScannerUtil;
import com.reedelk.runtime.api.annotation.PropertyInfo;
import io.github.classgraph.FieldInfo;

public class PropertyInfoFieldInfoAnalyzer implements FieldInfoAnalyzer {

    @Override
    public void handle(FieldInfo propertyInfo, ComponentPropertyDescriptor.Builder builder, ComponentAnalyzerContext context) {
        String propertyToolTipInfo =
                ScannerUtil.annotationValueOrDefaultFrom(propertyInfo, PropertyInfo.class, null);
        builder.propertyInfo(propertyToolTipInfo);
    }
}
