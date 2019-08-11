package com.reedelk.plugin.editor.properties.widget.input;

import com.reedelk.plugin.converter.FloatConverter;
import com.reedelk.plugin.converter.ValueConverter;
import com.reedelk.plugin.editor.properties.widget.NumericDocumentFilter;

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
