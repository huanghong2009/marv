package com.jtframework.datasource.redis;


import com.jtframework.utils.BaseUtils;
import com.jtframework.utils.ClassUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

@Component
@Aspect
@Slf4j
public class RedisCleanListCacheAspect {

    /**
     * 二级缓存
     */
    public static HashMap<String, HashMap<String, Object>> cache = new HashMap<>();

    @Autowired
    private RedisServiceInit redisServiceInit;


    /**
     * 方法用途（切入点表达式可以用&&,||,!来组合使用）:
     * execution: 匹配连接点：execution(* com.example.demo.*(..))--com.example.demo包下所有类的方法
     * within: 某个类里面
     * this: 指定AOP代理类的类型
     * target:指定目标对象的类型
     * args: 指定参数的类型
     * bean:指定特定的bean名称，可以使用通配符（Spring自带的）
     * 清除缓存注解
     *
     * @target： 带有指定注解的类型
     * @args: 指定运行时传的参数带有指定的注解
     * @within: 匹配使用指定注解的类
     * @annotation:指定方法所应用的注解
     */
    @Pointcut("@annotation(com.jtframework.datasource.redis.RedisCleanCollection)")
    public void asRedisCleanCollection() {
    }

    /**
     * 方法用途:
     * 清除redis缓存
     *
     * @Around 环绕增强，相当于MethodInterceptor，对带@AnnotationDemo注解的方法进行切面，并获取到注解的属性值
     * ProceedingJoinPoint: 环绕通知
     * <br/>
     * 操作步骤: TODO<br/>
     * ${tags}
     */
    @AfterReturning(value = "asRedisCleanCollection() && @annotation(redisCleanCollection)", returning = "re")
    public void after(JoinPoint joinPoint, RedisCleanCollection redisCleanCollection, Object re) {
        try {
            MethodSignature signature = ((MethodSignature) joinPoint.getSignature());
            Object[] args = joinPoint.getArgs();
            String[] argNames = signature.getParameterNames();

            String key = redisCleanCollection.collectionKey();
            String group = redisCleanCollection.group();


            if (BaseUtils.isBlank(key)) {
                log.error("{} 方法注解key为空,redis 去缓存不生效...", signature.getName());
                return;
            }

            if (args.length == 0) {
                log.error("{} 方法参数列表为空,redis 去缓存不生效...", signature.getName());
                return;
            }

            if (redisServiceInit.getRedisService() == null) {
                log.error("{} redis 未配置，缓存不生效...", signature.getName());
                return;
            }

            List<String> redisKeys = new ArrayList<>();

            Map<String, Object> argAllFiledsMap = ClassUtils.getObjectFiledValue(args, argNames);


            if (!argAllFiledsMap.containsKey(key)) {
                log.error("{} key标注的参数为空,获取该值未找到参数配置， 去缓存不生效...", signature.getName());
                return;
            }

            Object params = argAllFiledsMap.get(key);

            if (!Collection.class.isAssignableFrom(params.getClass())){
                log.error("{} redis 参数不是Collection 类型，缓存不生效...", signature.getName());
                return;
            }

            Type clazz = (ParameterizedType) params.getClass().getGenericSuperclass();

            ParameterizedType pt = (ParameterizedType)clazz;

            Type listType = pt.getActualTypeArguments()[0];

            if (!String.class.getTypeName().equals(listType.getTypeName()) && Integer.class.getTypeName().equals(listType.getTypeName()) && int.class.getTypeName().equals(listType.getTypeName())){
                log.error("{} redis Collection参数不是String 类型，缓存不生效...", signature.getName());
                return;
            }

            Collection data = (Collection) argAllFiledsMap.get(key);

            for (Object redisKey : data) {
                String redisKeyStr = redisKey.toString();
                if (redisServiceInit.getRedisService().hHasKey(group, redisKeyStr)) {
                    try {
                        redisServiceInit.getRedisService().hdel(group, redisKeyStr);
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error("清理缓存出错：{}-{}:{}", group, redisKeyStr, e.getMessage());
                    }
                }
            }

        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw throwable;
        }
    }





}
