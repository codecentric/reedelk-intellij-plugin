package com.reedelk.plugin.editor.properties.renderer.typebigdecimal;

import com.reedelk.plugin.converter.ValueConverter;
import com.reedelk.plugin.converter.ValueConverterProvider;
import com.reedelk.plugin.editor.properties.commons.NumericDocumentFilter;
import com.reedelk.plugin.editor.properties.commons.NumericInputField;

import javax.swing.text.DocumentFilter;
import java.math.BigDecimal;

public class BigDecimalInputField extends NumericInputField<BigDecimal> {

    public BigDecimalInputField(String hint) {
        super(hint);
    }

    @Override
    protected DocumentFilter getInputFilter() {
        return new NumericDocumentFilter(value -> {
            try {
                new BigDecimal(value);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        });
    }

    @Override
    protected ValueConverter<BigDecimal> getConverter() {
        return ValueConverterProvider.forType(BigDecimal.class);
    }
}
