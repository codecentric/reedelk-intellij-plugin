package com.esb.plugin.designer.properties.widget.input;

import com.esb.plugin.converter.ValueConverter;
import com.esb.plugin.converter.ValueConverterFactory;
import com.esb.plugin.designer.properties.widget.NumericDocumentFilter;

import javax.swing.text.DocumentFilter;

public class LongInputField extends NumericInputField<Long> {

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
