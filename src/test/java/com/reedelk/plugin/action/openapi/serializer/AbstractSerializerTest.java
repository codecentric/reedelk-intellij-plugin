package com.reedelk.plugin.action.openapi.serializer;

import com.reedelk.openapi.commons.DataFormats;
import com.reedelk.openapi.commons.NavigationPath;
import com.reedelk.openapi.v3.SerializerContext;
import com.reedelk.openapi.v3.serializer.Serializers;
import com.reedelk.plugin.action.openapi.OpenApiImporterContext;
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
        lenient().doReturn(DataFormats.YAML).when(context).getSchemaFormat();
        lenient().doCallRealMethod().when(context).exampleFormatOf(anyString());
        Serializers serializers = new Serializers(Serializer.serializers(context));
        serializerContext = new SerializerContext(serializers);
    }
}
