package com.griddynamics.urlshorter.datastore;

/**
 * Created by spider on 02.08.15.
 */
public interface DataStore {
    ShorterEntry findByKey(String key);

    ShorterEntry create(String key, String url);
}
