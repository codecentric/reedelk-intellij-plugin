package com.esb.plugin.editor.properties.widget.input;

import com.esb.plugin.converter.StringConverter;
import com.esb.plugin.converter.ValueConverter;

public class StringInputField extends InputField<String> {

    @Override
    protected ValueConverter<String> getConverter() {
        return new StringConverter();
    }

}
