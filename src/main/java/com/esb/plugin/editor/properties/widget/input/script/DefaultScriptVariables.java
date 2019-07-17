package com.esb.plugin.editor.properties.widget.input.script;

import java.util.Arrays;
import java.util.List;

public class DefaultScriptVariables {

    public static final List<ScriptContextManager.ContextVariable> ALL = Arrays.asList(
            new ScriptContextManager.ContextVariable("message", "Message"),
            new ScriptContextManager.ContextVariable("inboundProperties", "Map"),
            new ScriptContextManager.ContextVariable("outboundProperties", "Map"));

}
