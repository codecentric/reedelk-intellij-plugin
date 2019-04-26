package com.esb.plugin.designer.graph.deserializer;


import com.esb.plugin.designer.graph.drawable.Drawable;
import org.json.JSONObject;

public interface Builder {

    Drawable build(Drawable parent, JSONObject componentDefinition);

}
