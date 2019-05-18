package com.esb.plugin.designer.properties.renderer.type;

import com.esb.plugin.designer.properties.widget.input.BigIntegerInputField;
import com.esb.plugin.designer.properties.widget.input.InputField;

import java.math.BigInteger;

public class BigIntegerPropertyRenderer extends NumericPropertyRenderer<BigInteger> {

    @Override
    protected InputField<BigInteger> getInputField() {
        return new BigIntegerInputField();
    }
}
