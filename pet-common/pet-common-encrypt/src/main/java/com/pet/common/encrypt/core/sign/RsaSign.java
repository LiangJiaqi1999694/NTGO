package com.pet.common.encrypt.core.sign;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Base64Utils;

import java.util.Base64;

import java.security.KeyFactory;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

@Slf4j
public class RsaSign {

    /** 验签
     * 从字符串中加载公钥
     *
     * @param publicKeyStr 公钥数据字符串
     * @throws Exception 加载公钥时产生的异常
     */
    public static boolean loadPublicKey(String publicKeyStr,String text,String sign) throws Exception {
        Base64.Decoder base64Decoder = Base64.getDecoder();
        byte[] buffer = base64Decoder.decode(publicKeyStr);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
        RSAPublicKey rsaPublicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(rsaPublicKey);
        signature.update(text.getBytes());
        boolean flag = signature.verify(sign.getBytes());
        log.info("SHA256withRSA解密:" + flag);
        return flag;

    }


    /**
     * * 签名
     * @param privateKeyStr  私钥
     * @param text 需要加密字符串
     * @return 加密结果
     * @throws Exception
     */
    public static String loadPrivateKey(String privateKeyStr,String text) throws Exception {
        Base64.Decoder base64Decoder = Base64.getDecoder();
        byte[] buffer = base64Decoder.decode(privateKeyStr);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        RSAPrivateKey rsaPrivateKey =  (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(rsaPrivateKey);
        signature.update(text.getBytes());
        byte[] result = signature.sign();
        log.info("SHA256withRSA加密:" + Base64Utils.encodeToString(result));
        return Base64Utils.encodeToString(result);
    }
}
