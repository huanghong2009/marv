package com.jtframework.utils;

import com.jtframework.base.dao.ServerModel;
import org.apache.commons.collections4.CollectionUtils;
import org.eclipse.jetty.util.StringUtil;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author huanghong E-mail:767980702@qq.com
 * @version 创建时间：2017/12/21
 */
public final class BaseUtils {
    /**
     * 克隆流
     * @param input
     * @return
     */
    public static ByteArrayOutputStream cloneInputStream(InputStream input) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = input.read(buffer)) > -1) {
                baos.write(buffer, 0, len);
            }
            baos.flush();
            return baos;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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
     * 将List<String>集合 转化为String
     * 如{"aaa","bbb"} To 'aaa','bbb'
     */
    public static String convertListToString(List<String> strlist) {
        StringBuffer sb = new StringBuffer();
        if (CollectionUtils.isNotEmpty(strlist)) {
            for (int i = 0; i < strlist.size(); i++) {
                if (i == 0) {
                    sb.append("'").append(strlist.get(i)).append("'");
                } else {
                    sb.append(",").append("'").append(strlist.get(i)).append("'");
                }
            }
        }
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
     * BigDecimal 计算
     *
     * @param v1
     * @param v2
     * @return
     */
    public static double mathAdd(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2).doubleValue();
    }

    public static double mathAdd(String v1, String v2) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.add(b2).doubleValue();
    }

    public static double mathSub(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2).doubleValue();
    }

    public static double mathSub(String v1, String v2) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.subtract(b2).doubleValue();
    }

    public static double mathMul(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2).doubleValue();
    }

    public static double mathMul(String v1, String v2) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.multiply(b2).doubleValue();
    }

    public static double mathDiv(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        } else {
            BigDecimal b1 = new BigDecimal(Double.toString(v1));
            BigDecimal b2 = new BigDecimal(Double.toString(v2));
            return b1.divide(b2, scale, 4).doubleValue();
        }
    }

    public static double mathDiv(String v1, String v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        } else {
            BigDecimal b1 = new BigDecimal(v1);
            BigDecimal b2 = new BigDecimal(v2);
            return b1.divide(b2, scale, 4).doubleValue();
        }
    }


    /**
     * 保存图片
     *
     * @param instreams
     * @param fileName
     * @return
     */
    public static File readInpuStreamToFile(InputStream instreams, String fileName) throws Exception {
        if (instreams == null || BaseUtils.isBlank(fileName)) {
            throw new Exception("缺少必要参数");
        }

        return readInpuStreamToFile(instreams, new File(fileName));
    }


    /**
     * 保存图片
     *
     * @param instreams
     * @param file
     * @return
     */
    public static File readInpuStreamToFile(InputStream instreams, File file) throws Exception {
        if (instreams == null || null == file) {
            throw new Exception("缺少必要参数");
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            byte[] b = new byte[1024];
            int nRead = 0;
            while ((nRead = instreams.read(b)) != -1) {
                fos.write(b, 0, nRead);
            }
            fos.flush();
            fos.close();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("读取流失败:" + e.getMessage() + "...");
        }

    }


    /**
     * 判断在不在arry list 里面
     *
     * @param key
     * @param arry
     * @return
     */
    public static boolean searchInArray(String key, String[] arry) {
        if (arry != null && arry.length > 0) {
            String[] var2 = arry;
            for (String str : arry) {
                if (key.equals(str)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 批量替换
     *
     * @param source
     * @param keywords
     * @param target
     * @return
     */
    public static String replace(String source, String[] keywords, String target) {
        if (!BaseUtils.isBlank(source) && !BaseUtils.isBlank(target) && keywords != null && keywords.length != 0) {
            String result = source;
            for (String keyword : keywords) {
                if (!BaseUtils.isBlank(keyword)) {
                    result = StringUtils.replace(result, keyword, target);
                }
            }
            return result;
        } else {
            return null;
        }
    }

    public static boolean isNotBlank(String str) {
        return StringUtil.isNotBlank(str);
    }

    public static boolean isBlank(String str) {
        return StringUtil.isBlank(str);
    }

    /***
     * 判断是不是手机号
     * @param mobileNo
     * @return
     */
    public static boolean isMobileNo(String mobileNo) {
        String reg = "(^13[0-9]\\d{8}$)|(^17[0,1,6,7,8]\\d{8}$)|(^15[0,1,2，5，6,7,8,9]\\d{8}$)|(^18[0-9]\\d{8}$)|(^14[5,7]\\d{8}$)|(^[1-9]{2}\\d{6}$)";
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(mobileNo);
        return m.find();
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
     * 获取异常栈字符串
     *
     * @param e
     * @return
     */
    public static String getStackTrace(Exception e) {
        StringWriter sw = null;
        PrintWriter pw = null;
        try {
            sw = new StringWriter();
            pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            pw.flush();
            sw.flush();
        } finally {
            if (sw != null) {
                try {
                    sw.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (pw != null) {
                pw.close();
            }
        }
        return sw.toString();
    }


    /**
     * 获得一个随机数
     *
     * @param min
     * @param max
     * @return
     */
    public static int getRandom(int min, int max) {
        Random rand = new Random();
        return rand.nextInt(max - min) + min;
    }

    /**
     * get请求参数拼接
     *
     * @param ip     ip地址
     * @param parmas
     * @return
     */
    public static String httpGetParamsSplic(String ip, Map<String, String> parmas) {
        Set<String> keys = parmas.keySet();
        if (keys.size() == 0) {
            return ip;
        }
        ip += "?";
        for (String key : keys) {
            ip = ip + key + "=" + parmas.get(key) + "&";
        }
        ip = ip.substring(0, ip.length() - 1);

        return ip;
    }

    /**
     * 获取ip地址
     *
     * @param request
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            if (ip.equals("127.0.0.1")) {
                //根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ip = inet.getHostAddress();
            }
        }
        // 多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ip != null && ip.length() > 15) {
            if (ip.indexOf(",") > 0) {
                ip = ip.substring(0, ip.indexOf(","));
            }
        }
        return ip;
    }


    /**
     * bute合并
     * @param data1
     * @param data2
     * @return
     */
    public static byte[] addBytes(byte[] data1, byte[] data2) {

        byte[] data3 = new byte[data1.length + data2.length];

        System.arraycopy(data1, 0, data3, 0, data1.length);

        System.arraycopy(data2, 0, data3, data1.length, data2.length);

        return data3;
    }

    /**
     * 密码加盐
     *
     * @param password 用户密码
     * @param salt     盐
     * @return passwordBuf 加盐后的密码
     * @throws null
     */
    public static String addSalt(String password, String salt) {
        StringBuffer passwordBuf = new StringBuffer(password);
        List<String> saltBranch = new ArrayList<>();
        int part = salt.length() / SHA256.SALT_PART_NUMBER;
        for (int i = 0, k = 0; ; i = i + part, k++) {
            if (k != SHA256.SALT_PART_NUMBER - 1) {
                saltBranch.add(salt.substring(i, part + i));
            } else {
                saltBranch.add(salt.substring(i, salt.length()));
                break;
            }
        }

        boolean bool = true;
        int l = 0;
        while (bool) {
            if (passwordBuf.length() < 16) {
                passwordBuf.append(saltBranch.get(l));
                l++;
            } else {
                for (int p = 0; l < 5; l++) {
                    passwordBuf.insert(p, saltBranch.get(l));
                    p = saltBranch.get(l).length() + p + 3;
                }
                bool = false;
            }
        }

        return passwordBuf.toString();
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

    /**
     * 校验是不是手机号
     *
     * @param mobiles
     * @return
     */
    public static boolean isMobileNO(String mobiles) {
        Pattern p = Pattern.compile("^1(3|5|7|8|4)\\d{9}");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }


    /**
     * 读取一个文本 一行一行读取
     *
     * @param path
     * @return
     * @throws IOException
     */
    public static List<String> readTxtFile(String path) throws IOException {
        // 使用一个字符串集合来存储文本中的路径 ，也可用String []数组
        List<String> list = new ArrayList<String>();
        FileInputStream fis = new FileInputStream(path);
        // 防止路径乱码   如果utf-8 乱码  改GBK     eclipse里创建的txt  用UTF-8，在电脑上自己创建的txt  用GBK
        InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        String line = "";
        while ((line = br.readLine()) != null) {
            // 如果 t x t文件里的路径 不包含---字符串       这里是对里面的内容进行一个筛选
            if (line.lastIndexOf("---") < 0) {
                list.add(line);
            }
        }
        br.close();
        isr.close();
        fis.close();
        return list;
    }


    /**
     * 切分list
     *
     * @param sourceList
     * @param groupSize  每组定长
     * @return
     */
    public static List<List> splitList(List sourceList, int groupSize) {
        int length = sourceList.size();
        // 计算可以分成多少组
        int num = (length + groupSize - 1) / groupSize;
        List<List> newList = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            // 开始位置
            int fromIndex = i * groupSize;
            // 结束位置
            int toIndex = (i + 1) * groupSize < length ? (i + 1) * groupSize : length;
            newList.add(sourceList.subList(fromIndex, toIndex));
        }
        return newList;
    }


    /**
     * 获取类的ServerModel 值
     *
     * @param cls
     * @return
     */
    public static String getServeModelValue(Class cls) {
        ServerModel serverModel = (ServerModel) cls.getAnnotation(ServerModel.class);
        return serverModel != null ? serverModel.value() : cls.getName();
    }

    /**
     * 获取类的ServerModel 描述
     *
     * @param cls
     * @return
     */
    public static String getServeModelDesc(Class cls) {
        ServerModel serverModel = (ServerModel) cls.getAnnotation(ServerModel.class);
        return serverModel != null ? serverModel.desc() : cls.getName();
    }
}
