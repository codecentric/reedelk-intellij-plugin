package com.reedelk.plugin.editor.properties.renderer.typeinteger;

import com.reedelk.plugin.converter.ValueConverter;
import com.reedelk.plugin.converter.ValueConverterFactory;
import com.reedelk.plugin.editor.properties.renderer.commons.NumericInputField;
import com.reedelk.plugin.editor.properties.widget.NumericDocumentFilter;

import javax.swing.text.DocumentFilter;

public class IntegerInputField extends NumericInputField<Integer> {

    public IntegerInputField(String hint) {
        super(hint);
    }

    @Override
    protected DocumentFilter getInputFilter() {
        return new NumericDocumentFilter(value -> {
            try {
                Integer.parseInt(value);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        });
    }

    @Override
    protected ValueConverter<Integer> getConverter() {
        return ValueConverterFactory.forType(Integer.class);
    }
}
