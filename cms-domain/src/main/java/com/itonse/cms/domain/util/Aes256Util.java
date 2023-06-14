package com.itonse.cms.domain.util;

import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class Aes256Util {
    public static String alg = "AES/CBC/PKCS5Padding";
    private static final String KEY = "ZEROBASEKEYISZEROBASEKEY";
    private static final String IV = KEY.substring(0, 16);

    public static String encrypt(String text) {    // 주어진 텍스트를 AES-256 알고리즘을 사용하여 암호화
        try {
            Cipher cipher = Cipher.getInstance(alg);
            SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(), "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(IV.getBytes(StandardCharsets.UTF_8));
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParameterSpec);
            byte[] encrypted = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeBase64String(encrypted);
        } catch (Exception e) {
            return null;
        }
    }

    public static String decrypt(String cipherText) {    // 주어진 암호문(cipherText)을 AES-256 알고리즘을 사용하여 복호화
        try {
            Cipher cipher = Cipher.getInstance(alg);
            SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(), "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(IV.getBytes(StandardCharsets.UTF_8));
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParameterSpec);

            byte[] decodedBytes = Base64.decodeBase64(cipherText);
            byte[] decrypted = cipher.doFinal(decodedBytes);
            return new String(decrypted, StandardCharsets.UTF_8);

        } catch (Exception e) {
            return null;
        }
    }

}
