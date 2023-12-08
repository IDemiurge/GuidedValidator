package logic.kvs;

import java.util.List;

/**
 * An interface representing a set of keys and their associated values.
 */
public interface KeyValueSelectionSet {

    /**
     * @return A {@link List} of all keys
     */
    List<String> getKeys();

    /**
     * @param key The key for which a {@link List} of possible values is to be returned
     * @return A {@link List} of all possible values of the given key
     */
    List<String> getValues(String key);

}
