package ru.itmo.java;

import java.util.Map;

public class HashTable {
    private Entry[] entries;
    private boolean[] deleted;
    private int cnt;
    private int keyCnt;
    private int step;
    private double loadFactor;

    public HashTable(int startSize, double loadFactor) {
        cnt = 0;
        keyCnt = 0;
        step = 3;
        entries = new Entry[startSize + (startSize % step) ^ 1];
        deleted = new boolean[startSize + (startSize % step) ^ 1];

        this.loadFactor = loadFactor;
    }

    public HashTable(int startSize) {
        this(startSize, 0.5f);
    }

    public Object put(Object key, Object value) {
        if (key == null) return null;
        int i = (key.hashCode() * (key.hashCode() < 0 ? -1 : 1)) % entries.length;
        while (true) {
            if (entries[i] == null || deleted[i] && entries[i].keyEquals(key)) {
                keyCnt += entries[i] == null ? 1 : 0;
                entries[i] = new Entry(key, value);
                deleted[i] = false;
                cnt++;
                if (keyCnt >= entries.length * loadFactor) resize();
                return null;
            }
            if (!deleted[i] && entries[i].keyEquals(key)) {
                return entries[i].setValue(value);
            }
            i = (i + step) % entries.length;
        }
    }

    public Object get(Object key) {
        if (key == null) return null;
        int i = (key.hashCode() * (key.hashCode() < 0 ? -1 : 1)) % entries.length;
        while (true) {
            if (entries[i] == null || deleted[i] && entries[i].keyEquals(key)) return null;
            if (!deleted[i] && entries[i].keyEquals(key))
                return entries[i].getValue();
            i = (i + step) % entries.length;
        }
    }

    public Object remove(Object key) {
        if (key == null) return null;

        int hc = (key.hashCode() * (key.hashCode() < 0 ? -1 : 1)) % entries.length;
        int i = hc;
        while (true) {
            if (entries[i] == null || deleted[i] && entries[i].keyEquals(key)) return null;
            if (!deleted[i] && entries[i].keyEquals(key)) {
                Object val = entries[i].getValue();
                deleted[i] = true;
                cnt--;
                return val;
            }
            i = (i + step) % entries.length;
            if (i == hc) return null;
        }
    }

    public int size() {
        return cnt;
    }

    private void resize() {
        Entry[] newEntries = new Entry[entries.length * 2];
        keyCnt = 0;
        for (int i = 0; i < entries.length; i++) {
            if (entries[i] != null && !deleted[i]) {
                keyCnt++;
                int j = (entries[i].getHash() * (entries[i].getHash() < 0 ? -1 : 1)) % newEntries.length;
                while (true) {
                    if (newEntries[j] == null) {
                        newEntries[j] = entries[i];
                        break;
                    } else {
                        j = (j + step) % newEntries.length;
                    }
                }
            }
        }
        deleted = new boolean[entries.length * 2];
        entries = newEntries;

    }


    private class Entry {
        private Object key;
        private Object value;

        public Entry(Object key, Object value) {
            this.key = key;
            this.value = value;
        }

        public Object getValue() {
            return value;
        }

        public boolean keyEquals(Object key) {
            if (key.hashCode() == this.key.hashCode())
                return this.key.equals(key);
            return false;
        }

        public int getHash() {
            return key.hashCode();
        }

        public Object setValue(Object value) {
            Object val = this.value;
            this.value = value;
            return val;
        }
    }
}
