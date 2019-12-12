package com.reedelk.plugin.testutils;

import com.reedelk.runtime.api.annotation.Property;
import com.reedelk.runtime.api.annotation.ScriptSignature;
import com.reedelk.runtime.api.component.ProcessorSync;
import com.reedelk.runtime.api.message.FlowContext;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.script.Script;

public class TestComponentWithScriptSignature implements ProcessorSync {

    @ScriptSignature(arguments = {"arg1","arg2","arg3"})
    @Property("Property with script signature annotation")
    private Script scriptPropertyWithScriptSignature;

    @Property("Property without script signature annotation")
    private Script scriptPropertyWithoutScriptSignature;

    @Override
    public Message apply(Message message, FlowContext flowContext) {
        throw new UnsupportedOperationException("not supposed to be called");
    }

    public void setScriptPropertyWithScriptSignature(Script scriptPropertyWithScriptSignature) {
        this.scriptPropertyWithScriptSignature = scriptPropertyWithScriptSignature;
    }

    public void setScriptPropertyWithoutScriptSignature(Script scriptPropertyWithoutScriptSignature) {
        this.scriptPropertyWithoutScriptSignature = scriptPropertyWithoutScriptSignature;
    }
}
