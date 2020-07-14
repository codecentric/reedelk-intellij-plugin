package com.reedelk.plugin.action.importopenapi;

import com.reedelk.plugin.template.Template;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Properties;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImporterOpenAPITest {

    @Mock
    private ImporterOpenAPIContext context;

    @Test
    void shouldDoSomething() {

        ImporterOpenAPI importer = new ImporterOpenAPI(context, "/Users/lorenzo/Desktop/petstore_openapi.yaml");
        importer.doImport();

        // Verify
        verify(context, times(6)).createTemplate(any(Template.Buildable.class), anyString(), any(Properties.class));
    }
}
