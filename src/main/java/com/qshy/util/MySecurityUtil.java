package com.qshy.util;


import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @Classname SecurityUtil
 * @Description TODO
 * @Date 2023/2/14 12:25
 * @Created by senorisky
 */
public class MySecurityUtil {
    private static final String KEY = "AUDVYWO123124iYz";
    private static final String IV = "RIVFWi1234124biu";



    /**
     * 解密方法
     *
     * @param data 要解密的数据
     * @return 解密的结果
     */
    public static String desEncrypt(String data) {
        try {
            byte[] encrypted1 = new Base64().decode(data);
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(IV.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            byte[] original = cipher.doFinal(encrypted1);
            return new String(original).trim();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
