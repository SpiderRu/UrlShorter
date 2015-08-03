package com.griddynamics.urlshorter.datastore.cassandra;

import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.griddynamics.urlshorter.datastore.DataStore;
import com.griddynamics.urlshorter.datastore.ShorterEntry;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.stereotype.Service;

/**
 * Created by spider on 02.08.15.
 */

@Service("ShorterDS")
@ComponentScan("com.griddynamics.urlshorter.cassandra")
@Profile("cassandra")
public class CassandraDataStore implements DataStore {
    private static final Logger log = Logger.getLogger(CassandraDataStore.class);

    @Autowired
    private CassandraOperations cassandraOperations;

    @Override
    public ShorterEntry findByKey(String key) {
        Select s = QueryBuilder.select().from("short_links");
        s.where(QueryBuilder.eq("key", key));
        return cassandraOperations.selectOne(s, ShorterEntity.class);
    }

    @Override
    public ShorterEntry create(String key, String url) {
        ShorterEntity en = cassandraOperations.insert(new ShorterEntity(key, url));
        return cassandraOperations.insert(en);
    }
}
