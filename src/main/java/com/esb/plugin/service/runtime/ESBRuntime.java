package com.esb.plugin.service.runtime;

import com.intellij.util.xmlb.annotations.Attribute;
import com.intellij.util.xmlb.annotations.Tag;

@Tag("esb-runtime")
public class ESBRuntime {
    @Attribute("name")
    public String name;
    @Attribute("path")
    public String path;
}
