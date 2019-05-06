package com.esb.plugin.graph;

import org.jetbrains.annotations.NotNull;

public interface SnapshotListener {

    void onDataChange(@NotNull FlowGraph graph);

    void onStructureChange(@NotNull FlowGraph graph);

}
