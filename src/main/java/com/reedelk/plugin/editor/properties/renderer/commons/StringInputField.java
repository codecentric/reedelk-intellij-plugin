package com.reedelk.plugin.editor.properties.renderer.commons;

import com.reedelk.plugin.converter.ValueConverter;
import com.reedelk.plugin.converter.ValueConverterFactory;

public class StringInputField extends InputField<String> {

    public StringInputField(String hint) {
        super(hint);
    }

    @Override
    protected ValueConverter<String> getConverter() {
        return ValueConverterFactory.forType(String.class);
    }
}
