package util;

import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Map.Entry;

/**
 * Utility functions for {@link Map}
 */
public class MapUtils {

    /**
     * @param entries The {@link Entry}s from which to create a {@link java.util.HashMap}
     * @return A {@link java.util.HashMap} from the given entries
     */
    @SafeVarargs
    public static <K, V> Map<K, V> newHashMap(Entry<K, V>... entries) {
        Map<K, V> map = Maps.newHashMapWithExpectedSize(entries.length);
        putAll(map, entries);
        return map;
    }

    /**
     * @param entries The {@link Entry}s from which to create a {@link java.util.HashMap}
     * @return A {@link java.util.HashMap} from the given entries
     */
    public static <K, V> Map<K, V> newHashMap(Iterable<Entry<K, V>> entries) {
        Map<K, V> map = Maps.newHashMapWithExpectedSize(Iterables.size(entries));
        putAll(map, entries);
        return map;
    }

    /**
     * @param entries The {@link Entry}s from which to create a {@link java.util.LinkedHashMap}
     * @return A {@link java.util.LinkedHashMap} from the given entries
     */
    @SafeVarargs
    public static <K, V> Map<K, V> newLinkedHashMap(Entry<K, V>... entries) {
        Map<K, V> map = Maps.newLinkedHashMap();
        putAll(map, entries);
        return map;
    }

    /**
     * @param entries The {@link Entry}s from which to create a {@link java.util.LinkedHashMap}
     * @return A {@link java.util.LinkedHashMap} from the given entries
     */
    public static <K, V> Map<K, V> newLinkedHashMap(Iterable<Entry<K, V>> entries) {
        Map<K, V> map = Maps.newLinkedHashMap();
        putAll(map, entries);
        return map;
    }

    @SafeVarargs
    private static <K, V> void putAll(Map<K, V> map, Entry<K, V>... entries) {
        if (entries != null) {
            for (Entry<K, V> entry : entries) {
                map.put(entry.getKey(), entry.getValue());
            }
        }
    }

    private static <K, V> void putAll(Map<K, V> map, Iterable<Entry<K, V>> entries) {
        if (entries != null) {
            for (Entry<K, V> entry : entries) {
                map.put(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * Creates a new {@link Entry} from the given inputs
     *
     * @param key   The key of the {@link Entry} that is to be created
     * @param value The value of the {@link Entry} that is to be created
     * @return A {@link Entry} with the given key and value.
     */
    public static <K, V> Entry<K, V> newEntry(K key, V value) {
        return new MutableEntry<K, V>(key, value);
    }

    /**
     * A generic implementation of {@link Entry#equals(Object)}
     *
     * @param _this The {@link Entry} on which {@link Entry#equals(Object)} is called
     * @param o     The object to compare _this with
     * @return True or false according to the contract specified by {@link Entry#equals(Object)}
     */
    @SuppressWarnings("rawtypes")
    public static boolean equals(Entry _this, Object o) {
        if (o == null) {
            return false;
        }
        if (_this == o) {
            return true;
        }
        if (!(o instanceof Entry)) {
            return false;
        }

        return (
                Objects.equal(_this.getKey(), ((Entry) o).getKey()) &&
                        Objects.equal(_this.getValue(), ((Entry) o).getValue())
        );
    }

    /**
     * A generic implementation of {@link Entry#hashCode()}
     *
     * @param _this The {@link Entry} on which {@link Entry#hashCode} is called
     * @return A hash code according to the contract specified by {@link Entry#equals(Object)}
     */
    @SuppressWarnings("rawtypes")
    public static int hashCode(Entry _this) {
        return (
                (_this.getKey() == null ? 0 : _this.getKey().hashCode()) ^
                        (_this.getValue() == null ? 0 : _this.getValue().hashCode())
        );
    }

    /**
     * A straightforward implementation of {@link Entry}
     */
    private static final class MutableEntry<K, V> implements Entry<K, V> {

        private K key;
        private V value;

        public MutableEntry(K key, V value) {
            this.key = key;
            this.setValue(value);
        }

        @Override
        public K getKey() {
            return this.key;
        }

        @Override
        public V getValue() {
            return this.value;
        }

        @Override
        public V setValue(V value) {
            return this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            return MapUtils.equals(this, o);
        }

        @Override
        public int hashCode() {
            return MapUtils.hashCode(this);
        }

        public String toString() {
            return (Objects.toStringHelper(this)
                    .add("key", getKey())
                    .add("value", getValue())
                    .toString()
            );
        }

    }

}
