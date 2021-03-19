package de.codecentric.reedelk.plugin.editor.properties.commons;

import de.codecentric.reedelk.plugin.converter.ValueConverter;
import de.codecentric.reedelk.plugin.converter.ValueConverterProvider;

public class StringInputField extends InputField<String> {

    public StringInputField(String hint) {
        super(hint);
    }

    public StringInputField(String hint, int columns) {
        super(hint, columns);
    }

    @Override
    protected ValueConverter<String> getConverter() {
        return ValueConverterProvider.forType(String.class);
    }
}
