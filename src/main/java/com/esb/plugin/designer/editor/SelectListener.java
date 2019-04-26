package com.esb.plugin.designer.editor;

import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.drawable.Drawable;

public interface SelectListener {

    void onSelect(FlowGraph graph, Drawable drawable);

    void onUnselect(FlowGraph graph, Drawable drawable);
}
