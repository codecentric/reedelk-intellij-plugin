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
        String actual = OpenApiUtils.configTitleOf(openApiObject);

        // Then
        String expected = "Test API REST Listener";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void shouldReturnDefaultConfigTitle() {
        // Given
        OpenApiObject openApiObject = new OpenApiObject();

        // When
        String actual = OpenApiUtils.configTitleOf(openApiObject);

        // Then
        String expected = "My API REST Listener";
        assertThat(actual).isEqualTo(expected);
    }
}
