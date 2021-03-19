package de.codecentric.reedelk.plugin.commons;

import de.codecentric.reedelk.runtime.api.annotation.When;
import de.codecentric.reedelk.runtime.api.commons.ScriptUtils;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

import static de.codecentric.reedelk.module.descriptor.model.property.ObjectDescriptor.TypeObject;
import static de.codecentric.reedelk.runtime.api.commons.StringUtils.isBlank;

public class IsConditionSatisfied {

    public static boolean of(String wantedPropertyValue, Object actualPropertyValue) {
        return evaluate(wantedPropertyValue, actualPropertyValue);
    }

    interface ConditionEvaluator extends BiFunction<String, Object, Boolean> {
    }

    private static final ConditionEvaluator EVALUATOR_NULL =
            (wanted, actual) -> Objects.isNull(actual);
    private static final ConditionEvaluator EVALUATOR_BLANK =
            (wanted, actual) -> actual == null || actual instanceof String && isBlank((String) actual);
    private static final ConditionEvaluator EVALUATOR_NOT_BLANK =
            (wanted, actual) -> !EVALUATOR_BLANK.apply(wanted, actual);
    private static final ConditionEvaluator EVALUATOR_DEFAULT =
            (wanted, actual) -> actual != null && actual.toString().equals(wanted);
    private static final ConditionEvaluator EVALUATOR_SCRIPT =
            (wanted, actual) -> ScriptUtils.isScript(actual);
    private static final ConditionEvaluator EVALUATOR_NOT_SCRIPT =
            (wanted, actual) -> !ScriptUtils.isScript(actual);

    private static final ConditionEvaluator EVALUATOR_TYPE_OBJECT = (wantedJson, typeObject) -> {
        TypeObject actualTypeObject = (TypeObject) typeObject;
        JSONObject whenDefinition = new JSONObject(wantedJson);
        return whenDefinition.keySet().stream().allMatch(key -> {
            String expectedValue = whenDefinition.getString(key);
            return evaluate(expectedValue, actualTypeObject.get(key));
        });
    };

    private static final Map<String, ConditionEvaluator> EVALUATOR_MAP;

    static {
        Map<String, ConditionEvaluator> tmp = new HashMap<>();
        tmp.put(When.NULL, EVALUATOR_NULL);
        tmp.put(When.BLANK, EVALUATOR_BLANK);
        tmp.put(When.NOT_BLANK, EVALUATOR_NOT_BLANK);
        tmp.put(When.SCRIPT, EVALUATOR_SCRIPT);
        tmp.put(When.NOT_SCRIPT, EVALUATOR_NOT_SCRIPT);
        EVALUATOR_MAP = tmp;
    }

    private static boolean evaluate(String wanted, Object actual) {
        if (EVALUATOR_MAP.containsKey(wanted)) {
            return EVALUATOR_MAP.get(wanted).apply(wanted, actual);
        } else if (actual instanceof TypeObject) {
            return EVALUATOR_TYPE_OBJECT.apply(wanted, actual);
        } else {
            return EVALUATOR_DEFAULT.apply(wanted, actual);
        }
    }
}
