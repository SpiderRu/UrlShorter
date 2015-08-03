package com.griddynamics.urlshorter.api;

import com.griddynamics.urlshorter.keygen.KeyGenerator;
import com.griddynamics.urlshorter.datastore.DataStore;
import com.griddynamics.urlshorter.datastore.ShorterEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by spider on 02.08.15.
 */

@Service("UrlShorterAPI")
public class UrlShorterAPI {
    @Autowired
    @Qualifier("ShorterDS")
    private DataStore ds;

    @Autowired
    @Qualifier("MD5KeyGenerator")
    private KeyGenerator keyGen;

    public String createShortUrl(String url) throws URISyntaxException {
        int i = url.indexOf("://");
        if (i < 0)
            url = "http://" + url;
        URI uri = new URI(url);
        url = uri.toASCIIString();
        ShorterEntry entry = ds.create(keyGen.generateKey(url), url);
        if (entry == null)
            return null;
        return entry.getKey();
    }

    public String findUrlByKey(String key) {
        ShorterEntry entry = ds.findByKey(key);
        if (entry == null)
            return null;
        return entry.getUrl();
    }
}
