package de.codecentric.reedelk.plugin.assertion.component;

import de.codecentric.reedelk.plugin.component.type.router.RouterConditionRoutePair;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;

import java.util.List;

public class ComponentDataValueMatchers {

    public interface ComponentDataValueMatcher {
        boolean matches(Object actual);
    }

    public static ComponentDataValueMatcher ofRouterConditionPairs(List<RouterConditionRoutePair> expectedRouterConditionRoutePairs) {
        return given -> {
            if (given instanceof List) {
                List<RouterConditionRoutePair> actualRouterConditionRoutePairs = (List<RouterConditionRoutePair>) given;
                int expectedSize = expectedRouterConditionRoutePairs.size();
                int actualSize = actualRouterConditionRoutePairs.size();
                if (expectedSize != actualSize) return false;
                for (RouterConditionRoutePair expectedPair : expectedRouterConditionRoutePairs) {
                    if (!findMatchingRouterConditionRoutePair(expectedPair, actualRouterConditionRoutePairs)) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        };
    }

    private static boolean findMatchingRouterConditionRoutePair(RouterConditionRoutePair expectedPair, List<RouterConditionRoutePair> actualRouterConditionRoutePairs) {
        for (RouterConditionRoutePair current : actualRouterConditionRoutePairs) {
            String condition = current.getCondition();
            GraphNode next = current.getNext();
            boolean sameCondition = expectedPair.getCondition().equals(condition);
            boolean sameNode = next == expectedPair.getNext();
            if (sameNode && sameCondition) return true;
        }
        return false;
    }
}
