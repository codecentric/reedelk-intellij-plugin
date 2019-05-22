package com.esb.plugin.service.module.impl.esbcomponent;

import com.esb.api.annotation.ESBComponent;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtType;

import java.util.Collection;

@ESBComponent("HelloTest Spoon")
public class TestSpoon {

    @Test
    void shouldDoSomething() {
        // Given
        SpoonAPI spoon = new Launcher();
        spoon.addInputResource("/Users/lorenzo/Desktop/esb-project/esb-intellij-plugin/src/test/java/com/esb/plugin/service/module/impl/esbcomponent/TestSpoon.java");

        CtModel ctModel = spoon.buildModel();

        // When
        Collection<CtType<?>> allTypes = ctModel.getAllTypes();
        allTypes.forEach(ctType -> {
            boolean annotation = ctType.hasAnnotation(ESBComponent.class);
            if (annotation) {
                String displayName = ctType.getAnnotation(ESBComponent.class).value();
                Assertions.assertThat(displayName).isEqualTo("HelloTest Spoon");
            }
        });

        Assertions.assertThat(ctModel).isNotNull();

        // Then

    }
}
