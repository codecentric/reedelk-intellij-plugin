package com.reedelk.plugin.component.type.router.functions;

import com.reedelk.plugin.component.domain.ComponentData;
import com.reedelk.plugin.component.type.router.RouterConditionRoutePair;
import com.reedelk.plugin.component.type.router.RouterNode;

import java.util.ArrayList;
import java.util.List;

public class ListConditionRoutePairs {

    private ListConditionRoutePairs() {
    }

    public static List<RouterConditionRoutePair> of(ComponentData componentData) {
        List<RouterConditionRoutePair> conditionRoutePair =
                componentData.get(RouterNode.DATA_CONDITION_ROUTE_PAIRS);
        if (conditionRoutePair == null) {
            conditionRoutePair = new ArrayList<>();
            componentData.set(RouterNode.DATA_CONDITION_ROUTE_PAIRS, conditionRoutePair);
        }
        return conditionRoutePair;
    }
}
