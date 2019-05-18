package com.esb.plugin.designer.properties.widget.input;

import com.esb.plugin.converter.ValueConverter;
import com.esb.plugin.converter.ValueConverterFactory;

public class StringInputField extends InputField<String> {

    @Override
    protected ValueConverter<String> getConverter() {
        return ValueConverterFactory.forType(String.class);
    }

}
