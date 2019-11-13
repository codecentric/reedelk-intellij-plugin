package com.reedelk.plugin.editor.designer.action.remove.strategy;

import com.reedelk.plugin.component.type.placeholder.PlaceholderNode;

import java.util.Optional;

public interface PlaceholderProvider {

    Optional<PlaceholderNode> get();

    Optional<PlaceholderNode> get(String description);

}
