package de.codecentric.reedelk.plugin.editor.properties.renderer.typebiginteger;

import de.codecentric.reedelk.plugin.converter.ValueConverter;
import de.codecentric.reedelk.plugin.converter.ValueConverterProvider;
import de.codecentric.reedelk.plugin.editor.properties.commons.NumericDocumentFilter;
import de.codecentric.reedelk.plugin.editor.properties.commons.NumericInputField;

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
        return ValueConverterProvider.forType(BigInteger.class);
    }
}
