package com.scrumplateform.kante.service.utility;

import org.apache.commons.codec.binary.Base64;

public class EncryptionUtil {

    public static String encode(String data) {
        Base64 base64 = new Base64();
        String encodedString = new String(base64.encode(data.getBytes()));
        return encodedString;
    }

    public static String decode(String encodedData) {
        Base64 base64 = new Base64();
        String decodedString = new String(base64.decode(encodedData.getBytes()));
        return decodedString;
    }
}
