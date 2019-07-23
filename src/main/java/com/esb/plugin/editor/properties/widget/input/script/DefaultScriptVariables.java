package com.esb.plugin.editor.properties.widget.input.script;

import com.esb.plugin.javascript.Type;

import java.util.Arrays;
import java.util.List;

public class DefaultScriptVariables {

    public static final List<ScriptContextManager.ContextVariable> ALL = Arrays.asList(
            new ScriptContextManager.ContextVariable("message", Type.MESSAGE.displayName()),
            new ScriptContextManager.ContextVariable("inboundProperties", Type.MAP.displayName()),
            new ScriptContextManager.ContextVariable("outboundProperties", Type.MAP.displayName()));

}
