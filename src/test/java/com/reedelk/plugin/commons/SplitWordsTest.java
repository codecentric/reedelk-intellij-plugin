package com.reedelk.plugin.commons;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SplitWordsTest {

    @Test
    void shouldCorrectlyReturnTitleFromFileNameWithUpperAndLowerCaseLetters() {
        // Given
        String fileName = "GETOrdersFromDatabase";

        // When
        String titleFromFile = SplitWords.from(fileName);

        // Then
        String expected = "GET Orders From Database";
        assertThat(titleFromFile).isEqualTo(expected);
    }

    @Test
    void shouldCorrectlyReturnTitleFromFileNameWithDashes() {
        // Given
        String fileName = "My_super_flow";

        // When
        String titleFromFile = SplitWords.from(fileName);

        // Then
        String expected = "My super flow";
        assertThat(titleFromFile).isEqualTo(expected);
    }
}