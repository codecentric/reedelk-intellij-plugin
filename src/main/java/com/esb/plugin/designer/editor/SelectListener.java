package com.esb.plugin.designer.editor;

import com.esb.plugin.designer.graph.drawable.Drawable;

public interface SelectListener {

    void onSelect(Drawable drawable);

    void onUnselect(Drawable drawable);
}
