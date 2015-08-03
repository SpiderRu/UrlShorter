package com.griddynamics.urlshorter.datastore.cassandra;

import com.griddynamics.urlshorter.datastore.ShorterEntry;
import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.mapping.Table;

/**
 * Created by spider on 02.08.15.
 */

@Table("short_links")
public class ShorterEntity implements ShorterEntry {
    @Id
    private String key;

    private String url;

    public ShorterEntity(String key, String url) {
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