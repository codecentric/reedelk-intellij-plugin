package de.codecentric.reedelk.plugin.editor.properties.renderer.typebiginteger;

import de.codecentric.reedelk.plugin.editor.properties.commons.InputField;
import de.codecentric.reedelk.plugin.editor.properties.renderer.AbstractNumericPropertyRenderer;

import java.math.BigInteger;

public class BigIntegerPropertyRenderer extends AbstractNumericPropertyRenderer<BigInteger> {
    @Override
    protected InputField<BigInteger> getInputField(String hint) {
        return new BigIntegerInputField(hint);
    }
}
