package com.reedelk.plugin.editor.properties.widget.input;

import com.reedelk.plugin.converter.ValueConverter;
import com.reedelk.plugin.converter.ValueConverterFactory;
import com.reedelk.plugin.editor.properties.widget.NumericDocumentFilter;

import javax.swing.text.DocumentFilter;

public class LongInputField extends NumericInputField<Long> {

    public LongInputField(String hint) {
        super(hint);
    }

    @Override
    protected DocumentFilter getInputFilter() {
        return new NumericDocumentFilter(value -> {
            try {
                Long.parseLong(value);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        });
    }

    @Override
    protected ValueConverter<Long> getConverter() {
        return ValueConverterFactory.forType(Long.class);
    }

}
