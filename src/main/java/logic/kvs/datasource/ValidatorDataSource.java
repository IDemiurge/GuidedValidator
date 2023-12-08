package logic.kvs.datasource;

import java.util.Map;
import java.util.Set;

public interface ValidatorDataSource {
    /**
     * @return Set of valid combinations as {@link Map}s of Strings
     */
    Set<Map<String, String>> initValidatorData();
}
