package com.reedelk.plugin.editor.properties.renderer.typebiginteger;

import com.reedelk.plugin.converter.ValueConverter;
import com.reedelk.plugin.converter.ValueConverterFactory;
import com.reedelk.plugin.editor.properties.renderer.commons.NumericDocumentFilter;
import com.reedelk.plugin.editor.properties.renderer.commons.NumericInputField;

import javax.swing.text.DocumentFilter;
import java.math.BigInteger;

public class BigIntegerInputField extends NumericInputField<BigInteger> {

    public BigIntegerInputField(String hint) {
        super(hint);
    }

    @Override
    protected DocumentFilter getInputFilter() {
        return new NumericDocumentFilter(value -> {
            try {
                new BigInteger(value);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        });
    }

    @Override
    protected ValueConverter<BigInteger> getConverter() {
        return ValueConverterFactory.forType(BigInteger.class);
    }
}
