package com.reedelk.plugin.testutils;

import com.reedelk.runtime.api.annotation.AutoCompleteContributor;
import com.reedelk.runtime.api.annotation.Property;
import com.reedelk.runtime.api.component.ProcessorSync;
import com.reedelk.runtime.api.message.FlowContext;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicString;

public class TestComponentWithAutoCompleteContributor implements ProcessorSync {

    @AutoCompleteContributor(contributions = {""})
    @Property("Property with autocomplete contributor")
    private DynamicString propertyWithAutoCompleteContributor;

    @Override
    public Message apply(Message message, FlowContext flowContext) {
        throw new UnsupportedOperationException("not supposed to be called");
    }

    public void setPropertyWithAutoCompleteContributor(DynamicString propertyWithAutoCompleteContributor) {
        this.propertyWithAutoCompleteContributor = propertyWithAutoCompleteContributor;
    }
}
