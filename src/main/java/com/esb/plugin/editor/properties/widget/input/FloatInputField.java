package com.esb.plugin.editor.properties.widget.input;

import com.esb.plugin.converter.FloatConverter;
import com.esb.plugin.converter.ValueConverter;
import com.esb.plugin.editor.properties.widget.NumericDocumentFilter;

import javax.swing.text.DocumentFilter;

public class FloatInputField extends NumericInputField<Float> {
    @Override
    protected DocumentFilter getInputFilter() {
        return new NumericDocumentFilter(value -> {
            try {
                Float.parseFloat(value);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        });
    }

    @Override
    protected ValueConverter<Float> getConverter() {
        return new FloatConverter();
    }
}