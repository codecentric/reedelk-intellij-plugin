package com.reedelk.plugin.editor.properties.renderer.typedouble;

import com.reedelk.plugin.converter.ValueConverter;
import com.reedelk.plugin.converter.ValueConverterFactory;
import com.reedelk.plugin.editor.properties.renderer.commons.NumericDocumentFilter;
import com.reedelk.plugin.editor.properties.renderer.commons.NumericInputField;

import javax.swing.text.DocumentFilter;

public class DoubleInputField extends NumericInputField<Double> {

    public DoubleInputField(String hint) {
        super(hint);
    }

    @Override
    protected DocumentFilter getInputFilter() {
        return new NumericDocumentFilter(value -> {
            try {
                Double.parseDouble(value);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        });
    }

    @Override
    protected ValueConverter<Double> getConverter() {
        return ValueConverterFactory.forType(Double.class);
    }
}
