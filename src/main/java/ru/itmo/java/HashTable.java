package ru.itmo.java;

public class HashTable {
    private static final double DEFAULT_LOAD_FACTOR = 0.5;
    private static final int DEFAULT_RESIZE_FACTOR = 2;
    private static final int DEFAULT_STEP = 3;
    private Entry[] entries;
    private int size;
    private int step;
    private double loadFactor;

    public HashTable(int startSize, double loadFactor) {
        size = 0;
        step = DEFAULT_STEP;
        entries = new Entry[startSize + (startSize % step) ^ 1];
        this.loadFactor = loadFactor;
    }

    public HashTable(int startSize) {
        this(startSize, DEFAULT_LOAD_FACTOR);
    }

    private Integer findPosition(Object key) {
        int hc = Math.abs(key.hashCode()) % entries.length;
        int i = hc;
        while (true) {
            if (entries[i] == null)
                return null;
            if (!entries[i].deleted() && entries[i].keyEquals(key))
                return i;
            i = (i + step) % entries.length;
            if (i == hc) return null;
        }
    }

    public Object put(Object key, Object value) {
        Integer positionOfKey = findPosition(key);
        if (positionOfKey == null) {
            size++;
            if (size > entries.length * loadFactor)
                resize();
            int i = Math.abs(key.hashCode()) % entries.length;
            while (true) {
                if (entries[i] == null || entries[i].deleted) {
                    entries[i] = new Entry(key, value);
                    return null;
                }
                i = (i + step) % entries.length;
            }
        } else {
            Object previousValue = entries[positionOfKey].getValue();
            entries[positionOfKey] = new Entry(key, value);
            return previousValue;
        }
    }

    public Object get(Object key) {
        Integer positionOfKey = findPosition(key);
        return positionOfKey == null ? null : entries[positionOfKey].getValue();
    }

    public Object remove(Object key) {
        Integer positionOfKey = findPosition(key);
        if (positionOfKey == null) return null;
        size--;
        return entries[positionOfKey].delete();
    }

    public int size() {
        return size;
    }

    private void resize() {
        Entry[] newEntries = new Entry[entries.length * DEFAULT_RESIZE_FACTOR];
        for (int i = 0; i < entries.length; i++) {
            if (entries[i] != null && !entries[i].deleted()) {
                int j = Math.abs(entries[i].getKey().hashCode()) % newEntries.length;
                while (true) {
                    if (newEntries[j] == null) {
                        newEntries[j] = entries[i];
                        break;
                    }
                    j = (j + step) % newEntries.length;
                }
            }
        }
        entries = newEntries;
    }


    private class Entry {
        private final Object key;
        private final Object value;
        private boolean deleted;

        public Entry(Object key, Object value) {
            this.key = key;
            this.value = value;
            deleted = false;
        }

        public Object getValue() {
            return value;
        }

        public Object getKey() {
            return key;
        }

        public boolean keyEquals(Object key) {
            if (key.hashCode() == this.key.hashCode())
                return this.key.equals(key);
            return false;
        }

        public Object delete() {
            deleted = true;
            return value;
        }

        public boolean deleted() {
            return deleted;
        }


    }
}
