package com.reedelk.plugin.editor.properties.renderer;

import com.reedelk.plugin.editor.properties.widget.input.BigIntegerInputField;
import com.reedelk.plugin.editor.properties.widget.input.InputField;

import java.math.BigInteger;

public class BigIntegerPropertyRenderer extends NumericPropertyRenderer<BigInteger> {
    @Override
    protected InputField<BigInteger> getInputField() {
        return new BigIntegerInputField();
    }
}
