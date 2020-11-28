package com.jtframework.datasource.redis;


import com.alibaba.fastjson.JSONObject;
import com.jtframework.base.rest.ServerResponse;
import com.jtframework.base.system.ApplicationContextProvider;
import com.jtframework.utils.BaseUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Aspect
@Slf4j
public class RedisCacheAspect {
    public static final String[] redisCleanAspectType = new String[]{"key", "hash"};

    private static String getRedisKey(String key, Object[] args) {
        if (BaseUtils.isBlank(key)) {
            log.warn("ResdisClean keysAndType 不符合规范: ---");
            return null;
        }

        if (!key.matches("^[0-9]\\d*$")) {
            log.warn("ResdisClean keysAndType 不符合规范: ---");
            return null;
        }

        int keyIndex = Integer.parseInt(key);

        if (keyIndex > args.length - 1) {
            log.warn("ResdisClean keyIndex 越界: ---");
            return null;
        }
        if (args[keyIndex] instanceof String || args[keyIndex] instanceof Integer) {
            key = String.valueOf(args[keyIndex]);
        } else {
            key = String.valueOf(args[keyIndex].hashCode());
        }

        return key;
    }

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
    @Pointcut("@annotation(com.jtframework.datasource.redis.ResdisClean)")
    public void asAnnotation() {
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
    @Around("asAnnotation() && @annotation(resdisClean)")
    public Object around(ProceedingJoinPoint joinPoint, ResdisClean resdisClean) throws Throwable {
        ServerResponse serverResponse = new ServerResponse();
        try {


            Signature signature = joinPoint.getSignature();
            Object[] args = joinPoint.getArgs();
            Object result = joinPoint.proceed(args);

            //清除缓存
            this.cleanRedisCache(resdisClean, args);

            return result;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw throwable;
        }
    }

    private void cleanRedisCache(ResdisClean resdisClean, Object[] args) throws Exception {
        if (BaseUtils.isNotBlank(resdisClean.beanName()) && BaseUtils.isNotBlank(resdisClean.keysAndType())) {

            RedisService redisService = ApplicationContextProvider.getBean(resdisClean.beanName());

            if (redisService == null) {
                log.warn("ResdisClean 未获取到指定bean:{}", resdisClean.beanName());
                return;
            }

            JSONObject datas = null;
            try {
                datas = JSONObject.parseObject(resdisClean.keysAndType());
            } catch (Exception e) {
                e.printStackTrace();
                log.warn("ResdisClean keysAndType 不符合规范:{} ---", e.getMessage());
                return;
            }

            Map<String, String> params = new HashMap<>();
            for (String key : datas.keySet()) {

                if (!datas.getString(key).equals(redisCleanAspectType[0]) && !datas.getString(key).equals(redisCleanAspectType[1])) {
                    log.warn("ResdisClean keysAndType 不符合规范:{} ---", resdisClean.keysAndType());
                    return;
                }

                String[] keyNow = key.split(":");
                if (keyNow.length > 1) {
                    String redisKey = getRedisKey(keyNow[1], args);
                    if (BaseUtils.isBlank(redisKey))
                        return;
                    else
                        keyNow[1] = redisKey;
                }

                /**
                 * key处理
                 */
                if (datas.getString(key).equals(redisCleanAspectType[0])) {
                    redisService.del(keyNow[0]);
                } else {
                    /**
                     * hash处理
                     */
                    if (keyNow.length != 2) {
                        log.warn("ResdisClean keysAndType 不符合规范:{} ---", resdisClean.keysAndType());
                        return;
                    }
                    redisService.hdel(keyNow[0].split("-")[0], keyNow[1]);
                }
            }

            log.info("正在清除缓存 {}：{}  ", resdisClean.beanName(), resdisClean.keysAndType());
        }
    }

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
    @Pointcut("@annotation(com.jtframework.datasource.redis.ResdisQuery)")
    public void asRedisQuery() {
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
    @Around("asRedisQuery() && @annotation(resdisQuery)")
    public Object asRedisQueryAround(ProceedingJoinPoint joinPoint, ResdisQuery resdisQuery) throws Throwable {
        ServerResponse serverResponse = new ServerResponse();
        try {
            Signature signature = joinPoint.getSignature();
            Object[] args = joinPoint.getArgs();
            String key = null;
            RedisService redisService = null;

            boolean flag = true;
            if (BaseUtils.isBlank(resdisQuery.beanName()) || BaseUtils.isBlank(resdisQuery.key())) {
                flag = false;
            } else {
                if (!resdisQuery.type().equals(redisCleanAspectType[0]) && !resdisQuery.type().equals(redisCleanAspectType[1])) {
                    log.warn("resdisQuery  type 不符合规范:{}", resdisQuery.type());
                    flag = false;
                } else {
                    if (resdisQuery.type().equals(redisCleanAspectType[0])) {
                        key = getRedisKey(resdisQuery.key(), args);
                    } else {
                        key = getRedisKey(resdisQuery.key().split(":")[1], args);
                    }

                    if (BaseUtils.isBlank(key)) {
                        log.warn("ResdisQuery key 不符合规范:{} ---", resdisQuery.key());
                        flag = false;
                    } else {
                        redisService = ApplicationContextProvider.getBean(resdisQuery.beanName());

                        if (redisService == null) {
                            log.warn("resdisQuery 未获取到指定bean:{}", resdisQuery.beanName());
                            flag = false;
                        } else {
                            if (resdisQuery.type().equals(redisCleanAspectType[0])) {
                                if (redisService.hasKey(key)) {
                                    return redisService.get(key);
                                }
                            } else if (resdisQuery.type().equals(redisCleanAspectType[1])) {
                                String[] hkeys = resdisQuery.key().split("-");
                                if (redisService.hHasKey(hkeys[0], key)) {
                                    return redisService.hget(hkeys[0], key);
                                }
                            }
                        }
                    }
                }

            }

            Object result = joinPoint.proceed(args);

            if (flag && redisService != null && BaseUtils.isNotBlank(key)) {
                if (resdisQuery.type().equals(redisCleanAspectType[0])) {
                    redisService.set(key, result);
                } else if (resdisQuery.type().equals(redisCleanAspectType[1])) {
                    String[] hkeys = resdisQuery.key().split("-");
                    if (redisService.hHasKey(hkeys[0], key)) {
                        return redisService.hset(hkeys[0], key,result);
                    }
                }
            }
            return result;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw throwable;
        }
    }


}
