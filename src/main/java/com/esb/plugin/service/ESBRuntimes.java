package com.esb.plugin.service;

import com.intellij.util.xmlb.annotations.Property;
import com.intellij.util.xmlb.annotations.XCollection;

import java.util.ArrayList;
import java.util.List;

public class ESBRuntimes {
    @Property(surroundWithTag = false)
    @XCollection
    public List<ESBRuntime> runtimes = new ArrayList<>();
}
