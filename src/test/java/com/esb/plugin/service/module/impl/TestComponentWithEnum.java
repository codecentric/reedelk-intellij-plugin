package com.esb.plugin.service.module.impl;

import com.esb.api.annotation.ESBComponent;
import com.esb.api.annotation.Property;
import com.esb.api.component.Processor;
import com.esb.api.message.Message;

@ESBComponent("Test Component With Enum")
public class TestComponentWithEnum implements Processor {

    @Property
    private TestEnum testEnum;

    @Override
    public Message apply(Message message) {
        return new Message();
    }

    public void setTestEnum(TestEnum testEnum) {
        this.testEnum = testEnum;
    }
}
