package com.esb.plugin.designer.graph.action;

import com.esb.plugin.designer.graph.drawable.Drawable;

public interface AddStrategy {

    void execute(Drawable closestPrecedingDrawable);

}
