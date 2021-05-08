package com.jtframework.utils;

import com.alibaba.fastjson.JSONObject;
import org.springframework.cglib.beans.BeanCopier;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

public class ObjectCopyUtils {
    private static Map<String, BeanCopier> beanCopierMap = new Hashtable<>();

    /**
     * copy属性
     *
     * @param dest      目标对象
     * @param orig      源对象
     * @param copyNulls
     * @param ext 排除指定字段
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
                if (!BaseUtils.searchInArray(key, ext)) {
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
        beanCopierMap.remove(beanKey);
    }

    /**
     * cp 非空属性
     * @param dest
     * @param orig
     */
    public static void copyNonNullProperties(Object dest, Object orig) {
        copyProperties(dest, orig, false, (String[]) null);
    }

    /**
     * cp 属性
     * @param dest
     * @param orig
     * @param split
     */
    public static void copyProperties(Object dest, Object orig, String[] split) {
        copyProperties(dest, orig, true, split);
    }

    /**
     * cp 非空属性，排除指定数组属性
     * @param dest
     * @param orig
     * @param ext
     */
    public static void copyNonNullProperties(Object dest, Object orig, String[] ext) {
        copyProperties(dest, orig, false, ext);
    }

    public static void copyProperties(Object dest, Object orig) {
        copyProperties(dest, orig, true, (String[]) null);
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
                if (!BaseUtils.searchInArray(key, ext)) {
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
                if (!BaseUtils.searchInArray(key, ext)) {
                    dest.put(key, ojson.get(key));
                }
            }
        }
    }

}
