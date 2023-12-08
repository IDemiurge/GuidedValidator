package logic.kvs.validator;

import com.google.common.base.Predicate;

import java.util.Map;

@SuppressWarnings("all")
public interface ICombinationValidator {

    /**
     *
     * @param selection currently selected combination
     * @return predicate that determines which, if any, Key-Value pairs should be colorized
     */
    Predicate<Map.Entry<String, String>> getColorizePredicate(Map<String, String> selection);

    /**
     * @param selection currently selected combination
     * @return text comment to be displayed about the selected combination
     */
    String getValidationText(Map<String, String> selection);

}
