package org.urlMonitor.util;

import java.security.MessageDigest;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
public class HashUtil {

    public static String md5Hex(String message) {
        return DigestUtils.md5Hex(message);
    }

    public static String generateHash(String key) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.reset();
            return new String(Hex.encodeHex(md5.digest(key.getBytes("UTF-8"))));
        } catch (Exception exc) {
            throw new RuntimeException(exc);
        }
    }

}
