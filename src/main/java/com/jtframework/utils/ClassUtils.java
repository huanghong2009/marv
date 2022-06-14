package com.jtframework.utils;

import cn.hutool.core.util.ReflectUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.*;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ClassUtils {


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
     * 获取对象的属性，最多递归max层
     *
     * @param obj
     * @param result
     * @param num
     * @return
     */
    public static Map<String, Object> getObjectFiledValue(String objName, Object obj, Map<String, Object> result, int num,int max) {
        if (num >= max || obj == null) {
            return result;
        }

        Field[] fields = ReflectUtil.getFields(obj.getClass());

        for (Field field : fields) {
            Object value = ReflectUtil.getFieldValue(obj, field);
            Class type = field.getType();
            if (value == null) {
                if (BaseUtils.isBlank(objName)) {
                    result.put(ReflectUtil.getFieldName(field), value);
                } else {
                    result.put(objName + "." +ReflectUtil.getFieldName(field), value);
                }

            } else {
                /**
                 * 判断是不是常用类型
                 */
                if (type.isPrimitive() || type.isArray() || type.isEnum() || type.getName().equals(Date.class.getTypeName()) ||
                        Number.class.isAssignableFrom(type) | Boolean.class.isAssignableFrom(type) || Character.class.isAssignableFrom(type)
                        || type.getName().equals(String.class.getTypeName())

                ) {
                    if (BaseUtils.isBlank(objName)) {
                        result.put(ReflectUtil.getFieldName(field), value);
                    } else {
                        result.put(objName + "." + ReflectUtil.getFieldName(field), value);
                    }
                } else if (Collection.class.isAssignableFrom(type)) {
                    if (BaseUtils.isBlank(objName)) {
                        result.put(ReflectUtil.getFieldName(field), value);
                    } else {
                        result.put(objName + "." + ReflectUtil.getFieldName(field), value);
                    }
                } else if (Map.class.isAssignableFrom(type)) {
                    continue;
                } else {
                    if (BaseUtils.isBlank(objName)) {
                        getObjectFiledValue(ReflectUtil.getFieldName(field), value, result, num + 1,max);
                    } else {
                        getObjectFiledValue(objName + "." + ReflectUtil.getFieldName(field), value, result, num + 1,max);
                    }

                }
            }
        }
        return result;
    }

    /**
     * 获取对象的属性，最多递归2层
     *
     * @param args
     * @return
     */
    public static Map<String, Object> getObjectFiledValue(Object[] args, String[] argNames) {
        Map<String, Object> argAllFiledsMap = new HashMap<>();
        /**
         * 获取全部字段 和属性
         */
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof String || args[i] instanceof Integer ||
                    args[i] instanceof Double ||
                    args[i] instanceof Boolean || args[i] instanceof Long
                    || args[i] instanceof BigDecimal || args[i] instanceof Date
                    || args[i] instanceof LocalDate) {
                argAllFiledsMap.put(argNames[i], args[i]);
            } else {
                return ClassUtils.getObjectFiledValue(args[i], argNames[i]);
            }
        }

        return argAllFiledsMap;
    }


    /**
     * 获取对象的属性，最多递归3层
     *
     * @param obj
     * @return
     */
    public static Map<String, Object> getObjectFiledValue(Object obj, String name) {
        return getObjectFiledValue(name, obj, new HashMap<String, Object>(), 0,3);
    }
    /**
     * 获取对象的属性，最多递归max层
     *
     * @param obj
     * @return
     */
    public static Map<String, Object> getObjectFiledValue(Object obj, String name,int max) {
        return getObjectFiledValue(name, obj, new HashMap<String, Object>(), 0,max);
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
     * 获取对象属性
     *
     * @param object
     * @param propertyName
     * @return
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     */
    public static Object getPrivateProperty(Object object, String propertyName) {
        return ReflectUtil.getFieldValue(object, propertyName);
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
    /**
     * 获取泛型类型
     *
     * @param obj
     * @return
     */
    public static Class getTClass(Object obj) {
        if (obj == null) {
            return null;
        }
        Type genericSuperClass = obj.getClass().getGenericSuperclass();
        ParameterizedType parametrizedType = null;
        while (parametrizedType == null) {
            if ((genericSuperClass instanceof ParameterizedType)) {
                parametrizedType = (ParameterizedType) genericSuperClass;
            } else {
                genericSuperClass = ((Class<?>) genericSuperClass).getGenericSuperclass();
            }
        }

        return (Class<?>)parametrizedType.getActualTypeArguments()[0];
    }

}
