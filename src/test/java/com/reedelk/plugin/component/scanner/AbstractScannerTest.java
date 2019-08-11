package com.reedelk.plugin.component.scanner;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import org.junit.jupiter.api.BeforeEach;

public abstract class AbstractScannerTest {

    private ComponentAnalyzerContext context;
    private ClassInfo targetComponentClassInfo;

    @BeforeEach
    void setUp() {
        ScanResult scanResult = new ClassGraph()
                .whitelistPackages(targetComponentClazz().getPackage().getName())
                .enableFieldInfo()
                .enableAnnotationInfo()
                .ignoreFieldVisibility()
                .scan();

        this.context = new ComponentAnalyzerContext(scanResult);
        this.targetComponentClassInfo = scanResult.getClassInfo(targetComponentClazz().getName());
    }


    protected abstract Class targetComponentClazz();

    protected ClassInfo getTargetComponentClassInfo() {
        return targetComponentClassInfo;
    }

    protected ComponentAnalyzerContext context() {
        return context;
    }
}
