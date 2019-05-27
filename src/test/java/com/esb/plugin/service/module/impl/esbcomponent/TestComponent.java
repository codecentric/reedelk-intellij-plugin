package com.esb.plugin.service.module.impl.esbcomponent;

import com.esb.api.annotation.Default;
import com.esb.api.annotation.ESBComponent;
import com.esb.api.annotation.Property;
import com.esb.api.annotation.Required;
import com.esb.api.component.Processor;
import com.esb.api.message.Message;

@ESBComponent("Test Component")
public class TestComponent implements Processor {

    @Property("Property 1")
    @Default("3")
    @Required
    private int property1;

    @Property("Property 2")
    private String property2;

    @Property("Enum Property")
    @Default("VALUE2")
    private TestEnum property3;

    private int notExposedProperty;


    @Override
    public Message apply(Message message) {
        return new Message();
    }

    public void setProperty1(int property1) {
        this.property1 = property1;
    }

    public void setProperty2(String property2) {
        this.property2 = property2;
    }

    public void setProperty3(TestEnum property3) {
        this.property3 = property3;
    }

    public void setNotExposedProperty(int notExposedProperty) {
        this.notExposedProperty = notExposedProperty;
    }
}
