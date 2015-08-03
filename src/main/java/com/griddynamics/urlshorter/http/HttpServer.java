package com.griddynamics.urlshorter.http;

import com.griddynamics.urlshorter.api.UrlShorterAPI;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

/**
 * Created by spider on 02.08.15.
 */

@Controller
@Service("HttpServer")
@RequestMapping("/")
public class HttpServer {
    private static final Logger log = Logger.getLogger(HttpServer.class);

    @Autowired
    UrlShorterAPI api;

    private final Map<String, byte[]> staticContent;

    public HttpServer() {
        this.staticContent = loadResources();
    }

    @RequestMapping(value="/create", method = RequestMethod.POST)
    public ResponseEntity<String> create(@RequestBody String url) {
        String key = null;
        HttpHeaders responseHeaders = new HttpHeaders();

        try {
            key = api.createShortUrl(url);
        } catch (URISyntaxException e) {
            return new ResponseEntity<>("Can't create short entry for url: " + url, responseHeaders, HttpStatus.BAD_REQUEST);
        }
        catch (Throwable t) {
            log.error("Create short link", t);
        }

        if (key == null)
            return new ResponseEntity<>("Can't create short entry for url: " + url, responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>("/r/" + key, responseHeaders, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/r/{key}")
    public ResponseEntity redirect(@PathVariable("key") String key) throws MalformedURLException, URISyntaxException {
        HttpHeaders responseHeaders = new HttpHeaders();
        String url = api.findUrlByKey(key);
        if (url == null)
            return new ResponseEntity<>("Can't find short entry for key: " + key, responseHeaders, HttpStatus.NOT_FOUND);
        responseHeaders.add("Location", url);

        return new ResponseEntity<>(responseHeaders, HttpStatus.TEMPORARY_REDIRECT);
    }

    private ResponseEntity<byte[]> getStaticContent(String url) {
        byte[] content = staticContent.get(url);
        if (content == null)
            return null;
        return new ResponseEntity<>(content, HttpStatus.OK);
    }

    @RequestMapping("**")
    public ResponseEntity<byte[]> defaultMethod(HttpServletRequest request) throws URISyntaxException {
        String uri = request.getRequestURI();
        if (uri == null || uri.isEmpty() || "/".equals(uri))
            uri = "/index.html";
        ResponseEntity<byte[]> content = getStaticContent(uri);
        if (content == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return content;
    }

    private Collection<String> listResources() {
        ArrayList<String> result = new ArrayList<>();
        InputStream in = getClass().getResourceAsStream("/web/file.list");

        if (in != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            try {
                String str;
                while ((str = reader.readLine()) != null) {
                    str = str.trim();
                    if (!str.isEmpty())
                        result.add(str);
                }
            } catch (IOException e) {
                log.error("List resources", e);
            } finally {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }
        else
            log.warn("Resources index not found");
        return result;
   }

    private Map<String, byte[]> loadResources() {
        final byte[] buffer = new byte[65536];
        Collection<String> resources = listResources();
        Map<String, byte[]> result = new HashMap<>(resources.size());
        for (String resource : resources) {
            final String name = "/" + resource;
            final InputStream in = getClass().getResourceAsStream("/web/" + resource);
            if (in != null) {
                final ByteArrayOutputStream out = new ByteArrayOutputStream();
                try {
                    int len;
                    while ((len = in.read(buffer)) >= 0)
                        out.write(buffer, 0, len);
                    out.flush();
                    result.put(name, out.toByteArray());
                } catch (IOException e) {
                    log.error("Read resource error: " + resource, e);
                } finally {
                    try {
                        in.close();
                    } catch (IOException e) {
                        log.warn("", e);
                    }
                    try {
                        out.close();
                    } catch (IOException e) {
                        log.warn("", e);
                    }
                }
            }
        }
        return result;
    }

}
