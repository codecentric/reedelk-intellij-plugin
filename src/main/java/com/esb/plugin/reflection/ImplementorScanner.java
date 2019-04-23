package com.esb.plugin.reflection;

import com.esb.api.component.Implementor;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;

public class ImplementorScanner {

    public void doSomething() {
        ClassGraph graph = new ClassGraph();
        ScanResult scan = graph.verbose()
                .enableAllInfo()
                .scan();
        ClassInfoList allClasses = scan.getClassesImplementing(Implementor.class.getName());
        System.out.println(allClasses);

    }
}
