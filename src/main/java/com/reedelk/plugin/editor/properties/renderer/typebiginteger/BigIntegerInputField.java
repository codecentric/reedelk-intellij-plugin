package com.reedelk.plugin.editor.properties.renderer.typebiginteger;

import com.reedelk.plugin.commons.PluginValueConverterProvider;
import com.reedelk.plugin.editor.properties.renderer.commons.NumericDocumentFilter;
import com.reedelk.plugin.editor.properties.renderer.commons.NumericInputField;
import com.reedelk.runtime.converter.PluginValueConverter;

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
    protected PluginValueConverter<BigInteger> getConverter() {
        return PluginValueConverterProvider.forType(BigInteger.class);
    }
}
