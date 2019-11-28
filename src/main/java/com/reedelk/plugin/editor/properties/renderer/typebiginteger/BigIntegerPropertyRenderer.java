package com.reedelk.plugin.editor.properties.renderer.typebiginteger;

import com.reedelk.plugin.editor.properties.renderer.AbstractNumericPropertyRenderer;
import com.reedelk.plugin.editor.properties.renderer.commons.InputField;

import java.math.BigInteger;

public class BigIntegerPropertyRenderer extends AbstractNumericPropertyRenderer<BigInteger> {
    @Override
    protected InputField<BigInteger> getInputField(String hint) {
        return new BigIntegerInputField(hint);
    }
}
