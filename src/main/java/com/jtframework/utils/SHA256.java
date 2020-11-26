package com.jtframework.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author 作者：huanghoong E-mail:邮件 huanghong@mobcolor.com
 * @version 创建时间：2016/09/29 13:14:35
 *     SAH-256加密
 *
 *
 *  SAH-256加密
 *       方法摘要
 *           Encrypt:加密
 *           bytes2Hex:2进制转16进制
 */
public class SHA256 {
    public static final int SALT_PART_NUMBER = 5;


    public static String Encrypt(String strSrc) {
        MessageDigest md;
        String strDes;
        String encName = "SHA-256";
        byte[] bt = strSrc.getBytes();
        try {
            md = MessageDigest.getInstance(encName);
            md.update(bt);
            strDes = bytes2Hex(md.digest()); // to HexString
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        return strDes;
    }

    public static String bytes2Hex(byte[] bts) {
        String des = "";
        String tmp;
        for (byte bt : bts) {
            tmp = (Integer.toHexString(bt & 0xFF));
            if (tmp.length() == 1) {
                des += "0";
            }
            des += tmp;
        }
        return des;
    }
    
}
