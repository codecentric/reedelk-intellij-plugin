package com.esb.plugin.designer.graph.builder;

import com.esb.plugin.designer.graph.Drawable;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.drawable.StopDrawable;
import org.json.JSONObject;

public class FlowReferenceDrawableBuilder implements Builder<StopDrawable> {
    @Override
    public StopDrawable build(Drawable parent, JSONObject componentDefinition, FlowGraph graph) {
        return null;
    }
}
