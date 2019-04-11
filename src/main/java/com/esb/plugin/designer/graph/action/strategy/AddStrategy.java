package com.esb.plugin.designer.graph.action.strategy;

import com.esb.plugin.designer.graph.drawable.Drawable;

public interface AddStrategy {

    void execute(Drawable closestPrecedingDrawable);

}
