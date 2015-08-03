package com.griddynamics.urlshorter.http.rest;

import com.griddynamics.urlshorter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by spider on 31.07.15.
 */

@RestController
@Service("RESTEntryPoint")
@RequestMapping("/api")
public class MainPoint {
    @Autowired
    UrlShorterAPI api;

    @RequestMapping(method = RequestMethod.POST, value = "/create")
    public ResponseEntity<String> create(@RequestBody String url) {
        HttpHeaders responseHeaders = new HttpHeaders();
        String key = null;
        try {
            key = api.createShortUrl(url);
        } catch (URISyntaxException e) {
            return new ResponseEntity<>("Can't create short entry for url: " + url,
                    responseHeaders, HttpStatus.BAD_REQUEST);
        }
        if (key == null)
            return new ResponseEntity<>("Can't create short entry for url: " + url,
                                                    responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(key, responseHeaders, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/get/{key}")
    public ResponseEntity<String> get(@PathVariable("key") String key) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.TEXT_PLAIN);
        String url = api.findUrlByKey(key);
        if (url == null)
            return new ResponseEntity<>("Can't find short entry for key: " + key, responseHeaders, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(url, responseHeaders, HttpStatus.OK);
    }

}
