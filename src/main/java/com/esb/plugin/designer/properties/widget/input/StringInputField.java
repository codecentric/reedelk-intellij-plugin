package com.esb.plugin.designer.properties.widget.input;

import com.esb.plugin.converter.ValueConverter;
import com.esb.plugin.converter.ValueConverterFactory;

import static java.awt.BorderLayout.CENTER;

public class StringInputField extends InputField<String> {

    public StringInputField() {
        super();
        add(inputField, CENTER);
    }

    @Override
    protected ValueConverter<String> getConverter() {
        return ValueConverterFactory.forType(String.class);
    }

}
