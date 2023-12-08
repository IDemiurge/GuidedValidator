package logic.kvs.validator;

import com.google.common.base.Predicate;
import logic.kvs.KeyValueSelectionSet;

import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Map.Entry;

public class CombinationValidator implements ICombinationValidator {

    private final Set<Map<String, String>> validCombinations;
    private final KeyValueSelectionSet kvs;

    public CombinationValidator(Set<Map<String, String>> validCombinations, KeyValueSelectionSet kvs) {
        this.validCombinations = validCombinations;
        this.kvs = kvs;
    }

    @Override
    public String getValidationText(Map<String, String> selection) {
        if (selection.isEmpty()) {
            return "Select a combination";
        }
        if (isValid(selection)) {
            return "Valid combination";
        } else {
            return "Invalid combination";
        }
    }

    /**
     * @param combination given Key-Value combination
     * @return whether this combination is valid (i.e. contained in the
     */
    private boolean isValid(Map<String, String> combination) {
        return validCombinations.contains(combination);
    }

    @SuppressWarnings("all")
    @Override
    public Predicate<Entry<String, String>> getColorizePredicate(Map<String, String> selection) {
        Set<Entry<String, String>> impossible = getImpossibleKeyValuePairsForCurrentSelection(selection);
        return entry -> impossible.contains(entry);
    }

    /**
     * Algorithm:
     * <ol>
     * <li> If Selection is empty, return empty {@link HashSet}  </li>
     * <li> Iterate over all possible key-value combinations  </li>
     * <li> For each key match Selection with {@link CombinationValidator#validCombinations} to get all combinations that can work with this selection </li>
     * <li> Key-value pairs that are not contained in any of these combinations are added to the set of 'impossible' ones </li>
     * </ol>
     * @param selection currently selected Key-Value pairs
     * @return set of Key-Value pairs that will make valid combination impossible with given selection
     */
    private Set<Entry<String, String>> getImpossibleKeyValuePairsForCurrentSelection(Map<String, String> selection) {
        if (selection.isEmpty())
            return new HashSet<>();

        Set<Entry<String, String>> impossible = new HashSet<>();
        for (String key : kvs.getKeys()) {
            if (selection.size() == 1 && selection.containsKey(key)) {
                continue;
            }
            Set<Map<String, String>> validWithSelected = validCombinations.stream()
                    .filter(map -> selection.keySet().stream().allMatch(
                            key1 -> (selection.get(key1).equals(map.get(key1))) || (key1.equals(key)) // do not clash with itself
                    )).collect(Collectors.toSet());

            for (String value : kvs.getValues(key)) {
                boolean possible = false;
                for (Map<String, String> map : validWithSelected) {
                    if ((map.get(key) != null) && (map.get(key).equals(value))) {
                        possible = true;
                    }
                }
                if (!possible) {
                    impossible.add(new AbstractMap.SimpleImmutableEntry<>(key, value));
                }

            }
        }
        return impossible;
    }
}
