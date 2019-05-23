package com.esb.plugin.service.module.impl.esbcomponent;

public class SourceComponentAnalyzer {

    private final String sourcePath;

    public SourceComponentAnalyzer(final String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public void analyze() {
        /**
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
         */
    }
}
