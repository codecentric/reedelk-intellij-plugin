package com.reedelk.plugin.action.openapi;

import com.reedelk.plugin.action.openapi.importer.OpenApiImporter;
import com.reedelk.plugin.action.openapi.importer.OpenApiImporterContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OpenApiImporterTest {

    @Mock
    private OpenApiImporterContext context;

    @Test
    void shouldDoSomething() {
        // Given
        OpenApiImporter importer = new OpenApiImporter(context, "/Users/lorenzo/Desktop/petstore_openapi.yaml");

        // When
//        importer.doImport();

        // Then
        //verify(context, times(6))
          //      .createTemplate(any(Template.Buildable.class), anyString(), any(Properties.class));
    }
}
