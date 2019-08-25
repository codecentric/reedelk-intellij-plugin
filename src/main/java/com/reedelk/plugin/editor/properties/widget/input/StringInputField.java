package com.reedelk.plugin.editor.properties.widget.input;

import com.reedelk.plugin.converter.StringConverter;
import com.reedelk.plugin.converter.ValueConverter;

public class StringInputField extends InputField<String> {

    public StringInputField(String hint) {
        super(hint);
    }

    @Override
    protected ValueConverter<String> getConverter() {
        return new StringConverter();
    }
}
