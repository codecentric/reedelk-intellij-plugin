package com.reedelk.plugin.action.openapi;

import com.reedelk.openapi.v3.model.InfoObject;
import com.reedelk.openapi.v3.model.OpenApiObject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OpenApiUtilsTest {

    @Test
    void shouldReturnCorrectConfigTitle() {
        // Given
        InfoObject infoObject = new InfoObject();
        infoObject.setTitle("Test API");
        OpenApiObject openApiObject = new OpenApiObject();
        openApiObject.setInfo(infoObject);

        // When
        String actual = OpenApiUtils.restListenerConfigTitleOf(openApiObject);

        // Then
        String expected = "Test API REST Listener";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldReturnDefaultConfigTitle() {
        // Given
        OpenApiObject openApiObject = new OpenApiObject();

        // When
        String actual = OpenApiUtils.restListenerConfigTitleOf(openApiObject);

        // Then
        String expected = "My Api REST Listener";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldReturnCorrectConfigFileName() {
        // Given
        InfoObject infoObject = new InfoObject();
        infoObject.setTitle("Test API");
        OpenApiObject openApiObject = new OpenApiObject();
        openApiObject.setInfo(infoObject);

        // When
        String actual = OpenApiUtils.restListenerConfigFileNameOf(openApiObject);

        // Then
        String expected = "TestAPIRESTListener.fconfig";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldReturnDefaultConfigFileName() {
        // Given
        OpenApiObject openApiObject = new OpenApiObject();

        // When
        String actual = OpenApiUtils.restListenerConfigFileNameOf(openApiObject);

        // Then
        String expected = "MyApiRESTListener.fconfig";
        assertThat(actual).isEqualTo(expected);
    }
}
