package com.griddynamics.urlshorter.datastore.simple;

import com.griddynamics.urlshorter.datastore.DataStore;
import com.griddynamics.urlshorter.datastore.ShorterEntry;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by spider on 02.08.15.
 */

@Service("ShorterDS")
@Profile("!cassandra")
public class MapDataStore implements DataStore {
    private static class Entry implements ShorterEntry {
        final String key, url;

        Entry(String key, String url) {
            this.key = key;
            this.url = url;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public String getUrl() {
            return url;
        }
    }

    private final Map<String, Entry> map = new ConcurrentHashMap<>();

    @Override
    public ShorterEntry findByKey(final String key) {
        return map.get(key);
    }

    @Override
    public ShorterEntry create(String key, String url) {
        Entry en = new Entry(key, url);
        map.putIfAbsent(key, en);
        return en;
    }
}
