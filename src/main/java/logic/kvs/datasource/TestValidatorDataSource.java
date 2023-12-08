package logic.kvs.datasource;

import util.MapUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class TestValidatorDataSource implements ValidatorDataSource {

    //String constants for transforming raw string data into set of combinations
    private static final String COMBINATION_SEPARATOR = ";";
    private static final String ITEM_SEPARATOR = ",";
    private static final String PAIR_SEPARATOR = "=";

    /**
     * {@inheritDoc}
     * An implementations relying on parsing of valid combination set from a string which could be read from a datasource
     */
    @Override
    public Set<Map<String, String>> initValidatorData() {
        Set<Map<String, String>> valid = new HashSet<>();
        String rawData = getRawData();

        Set<String[]> array = toSetArray(rawData);
        for (String[] strings : array) {
            Set<Map.Entry<String, String>> set = Arrays.stream(strings).map(string -> MapUtils.newEntry(string.split(PAIR_SEPARATOR)[0], string.split(PAIR_SEPARATOR)[1])).collect(Collectors.toSet());
            valid.add(MapUtils.newHashMap(set));
        }
        return valid;
    }

    /**
     * simulates heavy file IO/Database work, then returns a literal string
     * @return raw String data to be parsed into a combination set
     */
    private String getRawData() {
        try {

            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return """
                Mode=Skirmish,Seals=0;
                Mode=Boss,Seals=1;
                Mode=Boss,Path=Shadow;
                Mode=Campaign,Seals=1,Path=Radiant
               """;
    }

    /**
     * Intermediary transformation step
     * @param rawData raw String data to be parsed into a combination set
     * @return Set of String arrays parsed from raw String data
     */
    private Set<String[]> toSetArray(String rawData) {
        String[] split = rawData.split(COMBINATION_SEPARATOR);
        return Arrays.stream(split).map(s -> s.trim().split(ITEM_SEPARATOR)).collect(Collectors.toSet());
    }
}
