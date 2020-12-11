package com.jtframework.datasource.redis;


import com.jtframework.base.rest.ServerResponse;
import com.jtframework.utils.BaseUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Aspect
@Slf4j
public class RedisCacheAspect {

    /**
     * 根据规则获取缓存key
     *
     * @param args
     * @param argNames
     * @param keyParams
     * @return
     */
    private static String getRedisCacheKey(Object[] args, String[] argNames, String keyParams) {
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
                Map<String, Object> objFileddMap = BaseUtils.getObjectFiledValue(args[i]);
                for (String key : objFileddMap.keySet()) {
                    argAllFiledsMap.put(argNames[i] + "." + key, objFileddMap.get(key));
                }
            }
        }

        String[] keys = keyParams.split(",");
        String result = "";

        for (String key : keys) {
            if (argAllFiledsMap.containsKey(key)) {
                if (BaseUtils.isBlank(argAllFiledsMap.get(key).toString())) {
                    return "";
                }
                if (argAllFiledsMap.get(key).toString().length() > 80) {
                    return "";
                }
                result += argAllFiledsMap.get(key).toString() + "_";
            } else {
                return "";
            }
        }

        if (BaseUtils.isNotBlank(result)) {
            result = result.substring(0, result.length() - 1);
        }

        return result;
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
    @AfterReturning(value = "asAnnotation() && @annotation(resdisClean)", returning = "re")
    public void after(JoinPoint joinPoint, ResdisClean resdisClean, Object re) throws Throwable {
        try {
            MethodSignature signature = ((MethodSignature) joinPoint.getSignature());
            Object[] args = joinPoint.getArgs();
            String[] argNames = signature.getParameterNames();

            String keyParames = resdisClean.key();
            String group = resdisClean.group();

            String redisKey = "";

            boolean flag = true;

            if (BaseUtils.isBlank(keyParames)) {
                log.error("{} 方法注解key为空,redis 缓存清除不生效...", signature.getName());
                flag = false;
            }

            /**
             * 获取 redis key
             */
            if (flag) {
                redisKey = getRedisCacheKey(args, argNames, keyParames);
                if (BaseUtils.isBlank(redisKey)) {
                    log.error("{} key标注的参数为空,或最终的key长度过长,redis 缓存清除不生效...", signature.getName());
                    flag = false;
                }
            }

            /**
             * 删除缓存
             */
            if (RedisService.REDIS_STATIC_SERVICE != null && flag) {
                if (RedisService.REDIS_STATIC_SERVICE.hHasKey(group, redisKey)) {
                    RedisService.REDIS_STATIC_SERVICE.hdel(group, redisKey);
                    log.error("{},{} key标注缓存已删除...", group, redisKey);
                }
            }

        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw throwable;
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
            MethodSignature signature = ((MethodSignature) joinPoint.getSignature());
            Object[] args = joinPoint.getArgs();
            String[] argNames = signature.getParameterNames();


            String keyParames = resdisQuery.key();
            String group = resdisQuery.group();
            Long timeOut = resdisQuery.timeOut();

            boolean flag = true;

            if (args.length == 0) {
                log.error("{} 方法参数列表为空,redis 缓存不生效...", signature.getName());
                flag = false;
            }

            if (BaseUtils.isBlank(keyParames)) {
                log.error("{} 方法注解key为空,redis 缓存不生效...", signature.getName());
                flag = false;
            }

            String redisKey = "";

            /**
             * 获取 redis key
             */
            if (flag) {
                redisKey = getRedisCacheKey(args, argNames, keyParames);
                if (BaseUtils.isBlank(redisKey)) {
                    log.error("{} key标注的参数为空,或最终的key长度过长,redis 缓存不生效...", signature.getName());
                    flag = false;
                }
            }

            if (RedisService.REDIS_STATIC_SERVICE != null && flag) {
                if (RedisService.REDIS_STATIC_SERVICE.hHasKey(group, redisKey)) {
                    return RedisService.REDIS_STATIC_SERVICE.hget(group, redisKey);
                }
            }

            Object result = joinPoint.proceed(args);

            if (RedisService.REDIS_STATIC_SERVICE != null && flag) {
                RedisService.REDIS_STATIC_SERVICE.hset(group, redisKey, result, timeOut);
            }

            return result;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw throwable;
        }
    }


}
