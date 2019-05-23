package com.esb.plugin.service.module.impl.esbcomponent;

import com.esb.plugin.component.ComponentDescriptor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SourceComponentScannerTest {


    @Test
    void shouldTestSomething() {
        // Given
        String path = "/Users/lorenzo/Desktop/esb-project/esb-intellij-plugin/src/test/java/com/esb/plugin/service/module/impl/esbcomponent";
        SourceComponentScanner analyzer = new SourceComponentScanner(path);

        // When
        long start = System.currentTimeMillis();
        List<ComponentDescriptor> components = analyzer.analyze();
        long delta = System.currentTimeMillis() - start;

        System.err.println(delta);
        // Then
        assertThat(components).hasSize(1);
    }
}
