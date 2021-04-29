package com.jtframework.utils;

import cn.hutool.core.util.ReflectUtil;
import com.alibaba.fastjson.JSONObject;
import com.jtframework.base.dao.ServerModel;
import org.eclipse.jetty.util.StringUtil;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author huanghong E-mail:767980702@qq.com
 * @version 创建时间：2017/12/21
 */
public final class BaseUtils {
    private static Map<String, BeanCopier> beanCopierMap = new Hashtable<>();

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
     * 获取对象属性
     *
     * @param object
     * @param propertyName
     * @return
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     */
    public static Object getPrivateProperty(Object object, String propertyName) throws IllegalAccessException, NoSuchFieldException {
        Field field = object.getClass().getDeclaredField(propertyName);
        field.setAccessible(true);
        return field.get(object);
    }

    /**
     * 设置对象属性
     *
     * @param object
     * @param propertyName
     * @return
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     */
    public static void setPrivateProperty(Object object, String propertyName, Object newValue) throws IllegalAccessException, NoSuchFieldException {
        Field field = object.getClass().getDeclaredField(propertyName);
        field.setAccessible(true);
        field.set(object, newValue);
    }


    /**
     * 反射调用指定类的一个私有方法
     *
     * @param object
     * @param methodName
     * @param params
     * @return
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static Object invokePrivateMethod(Object object, String methodName, Object[] params) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Class[] types = new Class[params.length];

        for (int i = 0; i < params.length; ++i) {
            types[i] = params[i].getClass();
        }

        Method method = object.getClass().getDeclaredMethod(methodName, types);
        method.setAccessible(true);
        return method.invoke(object, params);
    }

    /**
     * 反射调用指定类的一个私有方法
     *
     * @param object
     * @param methodName
     * @param param
     * @return
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static Object invokePrivateMethod(Object object, String methodName, Object param) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return invokePrivateMethod(object, methodName, new Object[]{param});
    }

    /**
     * 反射调用指定类的一个方法
     *
     * @param obj
     * @param methodName
     * @param parameterTypes
     * @param args
     * @return
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static Object invokeMethod(Object obj, String methodName, Class<?>[] parameterTypes, Object[] args) {
        Method method = getAccessibleMethod(obj, methodName, parameterTypes);
        if (method == null) {
            throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + obj + "]");
        } else {
            try {
                return method.invoke(obj, args);
            } catch (Exception var6) {
                if (!(var6 instanceof IllegalAccessException) && !(var6 instanceof IllegalArgumentException) && !(var6 instanceof NoSuchMethodException)) {
                    if (var6 instanceof InvocationTargetException) {
                        return new RuntimeException("Reflection Exception.", ((InvocationTargetException) var6).getTargetException());
                    } else {
                        return var6 instanceof RuntimeException ? (RuntimeException) var6 : new RuntimeException("Unexpected Checked Exception.", var6);
                    }
                } else {
                    return new IllegalArgumentException("Reflection Exception.", var6);
                }
            }
        }
    }

    private static Method getAccessibleMethod(Object obj, String methodName, Class<?>[] parameterTypes) {
        Class superClass = obj.getClass();

        while (superClass != Object.class) {
            try {
                Method method = superClass.getDeclaredMethod(methodName, parameterTypes);
                method.setAccessible(true);
                return method;
            } catch (NoSuchMethodException var5) {
                superClass = superClass.getSuperclass();
            }
        }

        return null;
    }

    public static void copyProperties(Object dest, Object orig) {
        copyProperties(dest, orig, true, (String[]) null);
    }

    public static void copyNonNullProperties(Object dest, Object orig) {
        copyProperties(dest, orig, false, (String[]) null);
    }

    public static void copyProperties(Object dest, Object orig, String[] split) {
        copyProperties(dest, orig, true, split);
    }

    public static void copyNonNullProperties(Object dest, Object orig, String[] split) {
        copyProperties(dest, orig, false, split);
    }

    public static void copyMap(Map dest, Object orig) {
        copyMap(dest, orig, true, (String[]) null);
    }

    public static void copyNonNullMap(Map dest, Object orig) {
        copyMap(dest, orig, false, (String[]) null);
    }

    public static void copyMap(Map dest, Object orig, String[] ext) {
        copyMap(dest, orig, true, ext);
    }

    public static void copyNonNullMap(Map dest, Object orig, String[] ext) {
        copyMap(dest, orig, false, ext);
    }

    /**
     * copy map 除去指定值
     *
     * @param dest
     * @param orig
     * @param copyNulls
     * @param ext
     */
    private static void copyMap(Map dest, Object orig, boolean copyNulls, String[] ext) {
        if (dest == null) {
            throw new IllegalArgumentException("No destination bean specified");
        } else if (orig == null) {
            throw new IllegalArgumentException("No origin bean specified");
        } else {
            JSONObject ojson = JSONObject.parseObject(JSONObject.toJSONString(orig));

            Iterator iterator = ojson.keySet().iterator();
            String key;
            while (iterator.hasNext()) {
                key = String.valueOf(iterator.next());
                if (ojson.get(key) == null && !copyNulls) {
                    continue;
                }
                if (!searchInArray(key, ext)) {
                    dest.put(key, ojson.get(key));
                }
            }
        }
    }

    public static void copyJSONObject(JSONObject dest, Object orig) {
        copyJSONObject(dest, orig, true, (String[]) null);
    }

    public static void copyNonNullJSONObject(JSONObject dest, Object orig) {
        copyJSONObject(dest, orig, false, (String[]) null);
    }

    public static void copyJSONObject(JSONObject dest, Object orig, String[] ext) {
        copyJSONObject(dest, orig, true, ext);
    }

    public static void copyNonNullJSONObject(JSONObject dest, Object orig, String[] ext) {
        copyJSONObject(dest, orig, false, ext);
    }

    private static void copyJSONObject(JSONObject dest, Object orig, boolean copyNulls, String[] ext) {
        if (dest == null) {
            throw new IllegalArgumentException("No destination bean specified");
        } else if (orig == null) {
            throw new IllegalArgumentException("No origin bean specified");
        } else {
            JSONObject ojson = JSONObject.parseObject(JSONObject.toJSONString(orig));
            Iterator iterator = ojson.keySet().iterator();
            String key;

            while (iterator.hasNext()) {
                key = (String) iterator.next();
                if (ojson.get(key) == null && !copyNulls) {
                    continue;
                }
                if (!searchInArray(key, ext)) {
                    dest.put(key, ojson.get(key));
                }
            }
        }
    }

    /**
     * copy属性
     *
     * @param dest      目标对象
     * @param orig      源对象
     * @param copyNulls
     * @param ext
     */
    private static void copyProperties(Object dest, Object orig, boolean copyNulls, String[] ext) {
        if (dest == null) {
            throw new IllegalArgumentException("No destination bean specified");
        } else if (orig == null) {
            throw new IllegalArgumentException("No origin bean specified");
        } else if (!(dest instanceof Map) && !(dest instanceof JSONObject)) {
            JSONObject jsonDest = JSONObject.parseObject(JSONObject.toJSONString(dest));

            JSONObject jsonOrig = JSONObject.parseObject(JSONObject.toJSONString(orig));

            Iterator iterator = jsonOrig.keySet().iterator();
            String key;

            while (iterator.hasNext()) {
                key = (String) iterator.next();
                if (jsonOrig.get(key) == null && !copyNulls) {
                    continue;
                }
                if (!searchInArray(key, ext)) {
                    jsonDest.put(key, jsonOrig.get(key));
                }
            }



            fastCopy(dest, jsonDest.toJavaObject(dest.getClass()));

        } else {
            throw new IllegalArgumentException("origin bean not be Map or JSONObject");
        }
    }

    public static void fastCopy(Object dest, Object orig) {
        String beanKey = orig.getClass() + "_" + dest.getClass();
        BeanCopier copier = null;
        if (!beanCopierMap.containsKey(beanKey)) {
            copier = BeanCopier.create(orig.getClass(), dest.getClass(), false);
            beanCopierMap.put(beanKey, copier);
        } else {
            copier = beanCopierMap.get(beanKey);
        }
        copier.copy(orig, dest, null);
    }

    private static boolean searchInArray(String key, String[] arry) {
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

    public static boolean isMobileNo(String mobileNo) {
        String reg = "(^13[0-9]\\d{8}$)|(^17[0,1,6,7,8]\\d{8}$)|(^15[0,1,2，5，6,7,8,9]\\d{8}$)|(^18[0-9]\\d{8}$)|(^14[5,7]\\d{8}$)|(^[1-9]{2}\\d{6}$)";
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(mobileNo);
        return m.find();
    }


    public static String urlEncode(String input) {
        try {
            return URLEncoder.encode(input, "UTF-8");
        } catch (UnsupportedEncodingException var2) {
            throw new IllegalArgumentException("Unsupported Encoding Exception", var2);
        }
    }

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
     * 日期转cron表达式
     *
     * @param time
     * @return
     */
    public static String getCron(String time) throws ParseException {
        String dateFormat = "ss mm HH dd MM ? yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formatTimeStr = null;
        if (time != null) {
            try {
                formatTimeStr = sdf.format(sdf2.parse(time));
            } catch (ParseException e) {
                throw e;
            }
        }
        return formatTimeStr;
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
     * 获取利用反射获取类里面的值和名称
     *
     * @param obj
     * @return
     * @throws IllegalAccessException
     */
    public static Map<String, Object> objectToMap(Object obj) throws IllegalAccessException {
        Map<String, Object> map = new HashMap<>();
        Class<?> clazz = obj.getClass();
        System.out.println(clazz);
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            String fieldName = field.getName();
            Object value = field.get(obj);
            if (null != value) {
                map.put(fieldName, value);
            }
        }
        return map;
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

    public static String getServeModelDesc(Class cls) {
        ServerModel serverModel = (ServerModel) cls.getAnnotation(ServerModel.class);
        return serverModel != null ? serverModel.desc() : cls.getName();
    }


    public static String getServeModelValue(Class cls) {
        ServerModel serverModel = (ServerModel) cls.getAnnotation(ServerModel.class);
        return serverModel != null ? serverModel.value() : cls.getName();
    }

    /**
     * 获取对象的属性，最多递归2层
     *
     * @param obj
     * @return
     */
    public static Map<String, Object> getObjectFiledValue(Object obj) {
        return getObjectFiledValue(null, obj, new HashMap<String, Object>(), 0);
    }

    /**
     * 获取对象的属性，最多递归2层
     *
     * @param obj
     * @param result
     * @param num
     * @return
     */
    private static Map<String, Object> getObjectFiledValue(String objName, Object obj, Map<String, Object> result, int num) {
        if (num >= 2) {
            return result;
        }


        Field[] fields = ReflectUtil.getFields(obj.getClass());

        for (Field field : fields) {
            Object value = ReflectUtil.getFieldValue(obj, field);
            Class type = field.getType();
            if (value == null) {
                result.put(ReflectUtil.getFieldName(field), value);
            } else {
                /**
                 * 判断是不是常用类型
                 */
                if (type.isPrimitive() || type.isArray() || type.isEnum() ||
                        type.isAssignableFrom(Date.class) || type.isAssignableFrom(BigDecimal.class) ||
                        type.isAssignableFrom(String.class)) {
                    if (BaseUtils.isBlank(objName)) {
                        result.put(ReflectUtil.getFieldName(field), value);
                    } else {
                        result.put(objName + "." + ReflectUtil.getFieldName(field), value);
                    }
                } else if (type.isAssignableFrom(List.class) || type.isAssignableFrom(Map.class) || type.isAssignableFrom(Set.class)) {
                    continue;
                } else {
                    getObjectFiledValue(ReflectUtil.getFieldName(field), value, result, num + 1);
                }
            }
        }
        return result;
    }


    /**
     * 实体类转Map
     *
     * @param object
     * @return
     */
    public static Map<String, Object> entityToMap(Object object) {
        Map<String, Object> map = new HashMap();
        for (Field field : ReflectUtil.getFields(object.getClass())) {
            try {
                boolean flag = field.isAccessible();
                field.setAccessible(true);
                Object o = field.get(object);
                map.put(field.getName(), o);
                field.setAccessible(flag);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    /**
     * Map转实体类
     *
     * @param map    需要初始化的数据，key字段必须与实体类的成员名字一样，否则赋值为空
     * @param entity 需要转化成的实体类
     * @return
     */
    public static <T> T mapToEntity(Map<String, Object> map, Class<T> entity) {
        T t = null;
        try {
            t = entity.newInstance();
            for (Field field : ReflectUtil.getFields(entity.getClass())) {
                if (map.containsKey(field.getName())) {
                    boolean flag = field.isAccessible();
                    field.setAccessible(true);
                    Object object = map.get(field.getName());
                    if (object != null && field.getType().isAssignableFrom(object.getClass())) {
                        field.set(t, object);
                    }
                    field.setAccessible(flag);
                }
            }
            return t;
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return t;
    }

    /**
     * 获得今天剩余秒数
     *
     * @return
     */
    public static int getToDayLastSeconds() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        // 得到今天 晚上的最后一刻 最后时间
        String last = sdf2.format(new Date()) + " 23:59:59";

        try {
            // 转换为今天
            Date latDate = sdf.parse(last);
            // 得到的毫秒 除以1000转换 为秒
            return (int) (latDate.getTime() - System.currentTimeMillis()) / 1000;
        } catch (ParseException e) {
            e.printStackTrace();
            throw new Exception("获取剩余秒数异常");
        }
    }


    /**
     * Object转成指定的类型
     *
     * @param obj
     * @param type
     * @param <T>
     * @return
     */
    public static <T> T convert(Object obj, Class<T> type) {
        if (obj != null && BaseUtils.isNotBlank(obj.toString())) {
            if (type.equals(Integer.class) || type.equals(int.class)) {
                return (T) new Integer(obj.toString());
            } else if (type.equals(Long.class) || type.equals(long.class)) {
                return (T) new Long(obj.toString());
            } else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
                return (T) new Boolean(obj.toString());
            } else if (type.equals(Short.class) || type.equals(short.class)) {
                return (T) new Short(obj.toString());
            } else if (type.equals(Float.class) || type.equals(float.class)) {
                return (T) new Float(obj.toString());
            } else if (type.equals(Double.class) || type.equals(double.class)) {
                return (T) new Double(obj.toString());
            } else if (type.equals(Byte.class) || type.equals(byte.class)) {
                return (T) new Byte(obj.toString());
            } else if (type.equals(Character.class) || type.equals(char.class)) {
                return (T) new Character(obj.toString().charAt(0));
            } else if (type.equals(String.class)) {
                return (T) obj;
            } else if (type.equals(BigDecimal.class)) {
                return (T) new BigDecimal(obj.toString());
            } else if (type.equals(LocalDateTime.class)) {
                //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                return (T) LocalDateTime.parse(obj.toString());
            } else if (type.equals(Date.class)) {
                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                    return (T) formatter.parse(obj.toString());
                } catch (ParseException e) {
                    throw new RuntimeException(e.getMessage());
                }

            } else {
                return null;
            }
        } else {
            if (type.equals(int.class)) {
                return (T) new Integer(0);
            } else if (type.equals(long.class)) {
                return (T) new Long(0L);
            } else if (type.equals(boolean.class)) {
                return (T) new Boolean(false);
            } else if (type.equals(short.class)) {
                return (T) new Short("0");
            } else if (type.equals(float.class)) {
                return (T) new Float(0.0);
            } else if (type.equals(double.class)) {
                return (T) new Double(0.0);
            } else if (type.equals(byte.class)) {
                return (T) new Byte("0");
            } else if (type.equals(char.class)) {
                return (T) new Character('\u0000');
            } else {
                return null;
            }
        }
    }
}
