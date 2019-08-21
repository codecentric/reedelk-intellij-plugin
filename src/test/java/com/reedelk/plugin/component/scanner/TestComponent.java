package com.reedelk.plugin.component.scanner;

import com.reedelk.runtime.api.annotation.Default;
import com.reedelk.runtime.api.annotation.ESBComponent;
import com.reedelk.runtime.api.annotation.Property;
import com.reedelk.runtime.api.component.ProcessorSync;
import com.reedelk.runtime.api.message.Message;


@ESBComponent("Test Component")
public class TestComponent implements ProcessorSync {

    @Property("Property 1")
    @Default("3")
    private int property1;
    @Property("Property 2")
    private String property2;
    @Property("Enum Property")
    @Default("VALUE2")
    private TestEnum property3;
    @Property
    private float propertyWithoutDisplayName;
    @Property("Property with missing default value")
    @Default
    private int propertyWithMissingDefaultValue;


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

    public void setPropertyWithoutDisplayName(float propertyWithoutDisplayName) {
        this.propertyWithoutDisplayName = propertyWithoutDisplayName;
    }

    public void setNotExposedProperty(int notExposedProperty) {
        this.notExposedProperty = notExposedProperty;
    }
}
