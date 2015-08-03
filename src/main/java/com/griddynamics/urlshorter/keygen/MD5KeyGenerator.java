package com.griddynamics.urlshorter.keygen;

import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Created by spider on 02.08.15.
 */

@Service("MD5KeyGenerator")
public class MD5KeyGenerator implements KeyGenerator {
    @Override
    public String generateKey(String url) {
        String key = Base64.getUrlEncoder().encodeToString(calcMD5(url));
        int i = key.indexOf('=');
        if (i < 0)
            return key;
        return key.substring(0, i);
    }

    private static final ThreadLocal<MessageDigest> digest = new ThreadLocal<MessageDigest>(){
        @Override
        protected MessageDigest initialValue() {
            try {
                return MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
    };

    private static byte[] calcMD5(String str) {
        MessageDigest md = digest.get();
        md.reset();
        md.update(str.getBytes());
        return md.digest();
    }
}
