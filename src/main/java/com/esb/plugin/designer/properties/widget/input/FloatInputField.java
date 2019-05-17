package com.esb.plugin.designer.properties.widget.input;

import com.esb.plugin.converter.ValueConverter;
import com.esb.plugin.converter.ValueConverterFactory;
import com.esb.plugin.designer.properties.widget.NumericDocumentFilter;

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
        return ValueConverterFactory.forType(Float.class);
    }
}
