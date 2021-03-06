package de.codecentric.reedelk.plugin.component.type.generic;

import de.codecentric.reedelk.plugin.AbstractGraphTest;
import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;

import static de.codecentric.reedelk.plugin.graph.serializer.AbstractSerializer.UntilNoSuccessors;

abstract class GenericComponentSerializerBaseTest extends AbstractGraphTest {

    private static final UntilNoSuccessors UNTIL_NO_SUCCESSORS = new UntilNoSuccessors();

    private static GenericComponentSerializer serializer;

    @BeforeEach
    public void setUp() {
        super.setUp();
        serializer = new GenericComponentSerializer();
    }

    protected String serialize(GraphNode componentNode) {
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, componentNode);

        JSONArray sequence = new JSONArray();
        serializer.serialize(graph, sequence, componentNode, UNTIL_NO_SUCCESSORS);

        JSONObject serializedObject = sequence.getJSONObject(0);
        return serializedObject.toString(2);
    }
}
