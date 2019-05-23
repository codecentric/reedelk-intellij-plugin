package com.esb.plugin.editor.properties.renderer.type;

import com.esb.plugin.editor.properties.widget.input.BigIntegerInputField;
import com.esb.plugin.editor.properties.widget.input.InputField;

import java.math.BigInteger;

public class BigIntegerPropertyRenderer extends NumericPropertyRenderer<BigInteger> {

    @Override
    protected InputField<BigInteger> getInputField() {
        return new BigIntegerInputField();
    }
}
