package com.esb.plugin.editor.properties.widget.input;

import com.esb.plugin.converter.LongConverter;
import com.esb.plugin.converter.ValueConverter;
import com.esb.plugin.editor.properties.widget.NumericDocumentFilter;

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
        return new LongConverter();
    }

}
