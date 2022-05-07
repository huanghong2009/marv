/*
 *  Copyright 2019-2020 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.jtframework.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.*;
import java.security.SecureRandom;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * @author Zheng Jie
 * 字符串工具类, 继承org.apache.commons.lang3.StringUtils类
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {

    private static final Logger log = LoggerFactory.getLogger(StringUtils.class);
    private static boolean ipLocal = false;
    private static File file = null;

    private static final char SEPARATOR = '_';
    private static final String UNKNOWN = "unknown";


    /**
     * 驼峰命名法工具
     *
     * @return toCamelCase(" hello_world ") == "helloWorld"
     * toCapitalizeCamelCase("hello_world") == "HelloWorld"
     * toUnderScoreCase("helloWorld") = "hello_world"
     */
    public static String toCamelCase(String s) {
        if (s == null) {
            return null;
        }

        s = s.toLowerCase();

        StringBuilder sb = new StringBuilder(s.length());
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == SEPARATOR) {
                upperCase = true;
            } else if (upperCase) {
                sb.append(Character.toUpperCase(c));
                upperCase = false;
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    /**
     * 驼峰命名法工具
     *
     * @return toCamelCase(" hello_world ") == "helloWorld"
     * toCapitalizeCamelCase("hello_world") == "HelloWorld"
     * toUnderScoreCase("helloWorld") = "hello_world"
     */
    public static String toCapitalizeCamelCase(String s) {
        if (s == null) {
            return null;
        }
        s = toCamelCase(s);
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    /**
     * 驼峰命名法工具
     *
     * @return toCamelCase(" hello_world ") == "helloWorld"
     * toCapitalizeCamelCase("hello_world") == "HelloWorld"
     * toUnderScoreCase("helloWorld") = "hello_world"
     */
    static String toUnderScoreCase(String s) {
        if (s == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            boolean nextUpperCase = true;

            if (i < (s.length() - 1)) {
                nextUpperCase = Character.isUpperCase(s.charAt(i + 1));
            }

            if ((i > 0) && Character.isUpperCase(c)) {
                if (!upperCase || !nextUpperCase) {
                    sb.append(SEPARATOR);
                }
                upperCase = true;
            } else {
                upperCase = false;
            }

            sb.append(Character.toLowerCase(c));
        }

        return sb.toString();
    }

    /**
     * 把下划线加小写转成驼峰格式
     *
     * @param str
     * @return
     */
    public static String changeUnderToUpperLetter(String str) {
        String regExp = "(_)([a-z]{1})";
        Pattern pattern = Pattern.compile(regExp);
        Matcher matcher = pattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            // 把_小写 格式 改成大写，即驼峰命名，匹配后进行把下划线替换成空，然后转换成大写的
            matcher.appendReplacement(sb, matcher.group().replaceAll("_", "").toUpperCase());
        }
        matcher.appendTail(sb);
        System.out.println("changeUnderToUpper: " + sb.toString());
        return sb.toString();
    }



    /**
     * 驼峰格式转换为下划线小写格式
     *
     * @param str
     * @return
     */
    public static String changeUpperToUnderLetter(String str) {
        String regExp = "([A-Z]{1})"; // 匹配单个字符
        Pattern pattern = Pattern.compile(regExp);
        Matcher matcher = pattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            // 把大写的 改成 _小写的内容。匹配大写后改成小写的，前面加一个下划线
            matcher.appendReplacement(sb, "_" + matcher.group().toLowerCase());
        }
        matcher.appendTail(sb);
        System.out.println("changeUpperLetter: " + sb.toString());
        return sb.toString();
    }


    /**
     * 字符串批量替换
     *
     * @param source 源字符串
     * @param keywords 需要匹配的 挂念次
     * @param target 替换的 词
     * @return
     */
    public static String replace(String source, String[] keywords, String target) {
        if (!BaseUtils.isBlank(source) && !BaseUtils.isBlank(target) && keywords != null && keywords.length != 0) {
            String result = source;
            for (String keyword : keywords) {
                if (!BaseUtils.isBlank(keyword)) {
                    result = org.springframework.util.StringUtils.replace(result, keyword, target);
                }
            }
            return result;
        } else {
            return null;
        }
    }

    /**
     * 判断是不是手机号
     * 正则表达
     * 手机号码由11位数字组成，
     * 匹配格式：前三位固定格式+后8位任意数
     * 此方法中前三位格式有：
     * 13+任意数
     * 15+除4的任意数
     * 18+除1和4的任意数
     * 17+除9的任意数
     * 147
     */
    public static boolean isMobile(String str) {
        String regExp = "^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-8])|(147))\\d{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
    }





    /**
     * 字符串url encode
     *
     * @param input
     * @return
     */
    public static String urlEncode(String input) {
        try {
            return URLEncoder.encode(input, "UTF-8");
        } catch (UnsupportedEncodingException var2) {
            throw new IllegalArgumentException("Unsupported Encoding Exception", var2);
        }
    }

    /**
     * 字符串url decode
     *
     * @param input
     * @return
     */
    public static String urlDecode(String input) {
        try {
            return URLDecoder.decode(input, "UTF-8");
        } catch (UnsupportedEncodingException var2) {
            throw new IllegalArgumentException("Unsupported Encoding Exception", var2);
        }
    }

    /**
     * 获取中文变量命名(第一个首字母小写，第二个是首字母大写的)
     *
     * @return
     * @throws Exception
     */
    public static String[] getZhWordName(String word) throws Exception {
        word = word.replace("'s", "");
        String[] words = word.split(" ");
        ArrayList<String> wordList = new ArrayList<>();
        for (String str : words) {
            if (!str.toLowerCase(Locale.ROOT).equals("the") && !str.toLowerCase(Locale.ROOT).equals("of") && !str.toLowerCase(Locale.ROOT).equals("table")) {
                wordList.add(str);
            }
        }

        word = String.join("_", wordList);
        String[] result = new String[2];
        result[0] = StringUtils.toCamelCase(word);
        result[1] = StringUtils.toCapitalizeCamelCase(word);
        return result;
    }

    /**
     * 判断是不是数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        // 该正则表达式可以匹配所有的数字 包括负数
        Pattern pattern = Pattern.compile("-?[0-9]+(\\.[0-9]+)?");
        String bigStr;
        try {
            bigStr = new BigDecimal(str).toString();
        } catch (Exception e) {
            return false;//异常 说明包含非数字。
        }

        Matcher isNum = pattern.matcher(bigStr); // matcher是全匹配
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }


    /**
     * 生成盐值
     *
     * @return String salt 盐值
     * @throws null 26
     */
    public static String creatSalt() {
        SecureRandom random = new SecureRandom();
        String salt = new BigInteger(130, random).toString(32);
        return salt;
    }


    /**
     * 创建指定数量的随机字符串
     *
     * @param length
     * @return
     */
    public static String getRandCode(int length) {
        return getRandCode(true, length);
    }

    /**
     * 创建指定数量的随机字符串
     *
     * @param numberFlag 是否是数字
     * @param length
     * @return
     */
    public static String getRandCode(boolean numberFlag, int length) {
        String retStr = "";
        String strTable = numberFlag ? "1234567890" : "1234567890abcdefghijkmnpqrstuvwxyz";
        int len = strTable.length();
        boolean bDone = true;
        do {
            retStr = "";
            int count = 0;
            for (int i = 0; i < length; i++) {
                double dblR = Math.random() * len;
                int intR = (int) Math.floor(dblR);
                char c = strTable.charAt(intR);
                if (('0' <= c) && (c <= '9')) {
                    count++;
                }
                retStr += strTable.charAt(intR);
            }
            if (count >= 2) {
                bDone = false;
            }
        } while (bDone);
        return retStr;
    }


}
