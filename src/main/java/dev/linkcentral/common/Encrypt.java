package dev.linkcentral.common;

import org.springframework.context.annotation.Configuration;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * 양방향 암호화
 */
@Configuration
public class Encrypt {

    public static String encryptAes(String str, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encPassword = cipher.doFinal(str.getBytes("UTF-8"));
        String result = Base64.getEncoder().encodeToString(encPassword);
        return result;
    }

    /*
     * password = AES 방식으로 암호화된 암호문
     * key = 암호화시 사용했던 키워드
     */
    public static String decryptAes(String str, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");

        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        byte[] decPassword = cipher.doFinal(Base64.getDecoder().decode(str));
        String result = new String(decPassword, "UTF-8");

        return result;
    }
}