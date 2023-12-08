package logic.kvs.datasource;

import util.MapUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PresetValidatorDataSource implements ValidatorDataSource {

    /**
     * {@inheritDoc}
     * A simple implementations relying on manual construction of valid combination set
     */
    @Override
    public Set<Map<String, String>> initValidatorData() {
        Set<Map<String, String>> valid = new HashSet<>();
        valid.add(MapUtils.newHashMap(MapUtils.newEntry("Selection mode", "Bits"), MapUtils.newEntry("Bits", "0")));
        valid.add(MapUtils.newHashMap(MapUtils.newEntry("Selection mode", "Bits"), MapUtils.newEntry("Bits", "1")));
        valid.add(MapUtils.newHashMap(MapUtils.newEntry("Selection mode", "Pieces"), MapUtils.newEntry("Pieces", "ces")));
        valid.add(MapUtils.newHashMap(MapUtils.newEntry("Selection mode", "Bits & Pieces"), MapUtils.newEntry("Bits", "1"), MapUtils.newEntry("Pieces", "Pie")));
        return valid;
    }

}
