package com.reedelk.plugin.editor.properties.renderer.typelong;

import com.reedelk.plugin.editor.properties.commons.DynamicValueInputFieldAdapter;
import com.reedelk.plugin.editor.properties.commons.DynamicValueInputFieldBaseAdapter;
import com.reedelk.plugin.editor.properties.renderer.AbstractDynamicPropertyTypeRenderer;

public class DynamicLongPropertyRenderer extends AbstractDynamicPropertyTypeRenderer {

    @Override
    protected DynamicValueInputFieldAdapter inputFieldAdapter(String hint) {
        return new DynamicValueInputFieldBaseAdapter<>(new LongInputField(hint));
    }
}
