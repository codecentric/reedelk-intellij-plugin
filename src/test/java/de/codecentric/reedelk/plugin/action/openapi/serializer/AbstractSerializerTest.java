package de.codecentric.reedelk.plugin.action.openapi.serializer;

import de.codecentric.reedelk.plugin.action.openapi.serializer.Serializer;
import de.codecentric.reedelk.openapi.commons.DataFormat;
import de.codecentric.reedelk.openapi.commons.NavigationPath;
import de.codecentric.reedelk.openapi.v3.SerializerContext;
import de.codecentric.reedelk.openapi.v3.serializer.Serializers;
import de.codecentric.reedelk.plugin.action.openapi.OpenApiImporterContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
abstract class AbstractSerializerTest {

    protected NavigationPath navigationPath = NavigationPath.create();

    @Mock
    protected OpenApiImporterContext context;
    protected SerializerContext serializerContext;

    @BeforeEach
    void setUp() {
        lenient().doReturn(DataFormat.YAML).when(context).getSchemaFormat();
        lenient().doCallRealMethod().when(context).exampleFormatOf(anyString());
        Serializers serializers = new Serializers(Serializer.serializers(context));
        serializerContext = new SerializerContext(serializers);
    }
}
