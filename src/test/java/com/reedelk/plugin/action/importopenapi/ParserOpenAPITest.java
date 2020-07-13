package com.reedelk.plugin.action.importopenapi;

import io.swagger.v3.oas.models.PathItem;
import org.junit.jupiter.api.Test;

import java.util.function.BiConsumer;

class ParserOpenAPITest {

    @Test
    void parse() {
        // Given
        String filePath = "/Users/lorenzo/Desktop/petstore_openapi.yaml";
        ParserOpenAPI parser = new ParserOpenAPI(filePath);

        // When
        parser.parse(new BiConsumer<String, PathItem>() {
            @Override
            public void accept(String pathEntry, PathItem pathItem) {
                // For each entry create new flow ...
                if (pathItem.getGet() != null) {
                    // Add get
                    System.out.println(pathEntry);
                }
                if (pathItem.getPost() != null) {
                    // Add post
                }

                if (pathItem.getHead() != null) {
                    // Add head
                }

                if (pathItem.getPut() != null) {
                    // Add put
                }

                if (pathItem.getDelete() != null) {
                    // Add delete
                }
                if (pathItem.getOptions() != null) {
                    // Add options
                }
            }
        });

        // Then
        System.out.println("Done");
    }
}
