package com.jtframework.datasource.redis;


import com.jtframework.utils.BaseUtils;
import com.jtframework.utils.ClassUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Aspect
@Slf4j
public class RedisQueryCacheAspect {

    @Autowired
    private RedisServiceInit redisServiceInit;

    @Autowired
    private RedisCacheListener redisCacheListener;

    /**
     * 二级缓存
     */
    public static ConcurrentHashMap<String, HashMap<String, Object>> CACHE = new ConcurrentHashMap<String, HashMap<String, Object>>();
    /**
     * 二级缓存
     */
    public static ConcurrentHashMap<String, HashMap<String, String>> CACHE_VISON = new ConcurrentHashMap<String, HashMap<String, String>>();


    public RedisQueryCacheAspect() {
        log.info("初始化 RedisQueryCacheAspect 代理类");
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
    @Pointcut("@annotation(com.jtframework.datasource.redis.RedisQuery)")
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
    @Around("asRedisQuery() && @annotation(redisQuery)")
    public Object asRedisQueryAround(ProceedingJoinPoint joinPoint, RedisQuery redisQuery) throws Throwable {

        try {
            MethodSignature signature = ((MethodSignature) joinPoint.getSignature());
            Object[] args = joinPoint.getArgs();
            String[] argNames = signature.getParameterNames();
            String group = redisQuery.group();
            Long timeOut = redisQuery.timeOut();

            String keyParame = redisQuery.key();


//            if (BaseUtils.isBlank(keyParame)) {
//                log.error("{} 方法注解key为空,redis 缓存不生效...", signature.getName());
//                return joinPoint.proceed(args);
//            }

            if (BaseUtils.isNotBlank(keyParame) && args.length == 0) {
                log.error("{} 方法参数列表为空,redis 缓存不生效...", signature.getName());
                return joinPoint.proceed(args);
            }

            if (redisServiceInit.getRedisService() == null) {
                log.error("{} redis 未配置，缓存不生效...", signature.getName());
                return joinPoint.proceed(args);
            }

            Map<String, Object> argAllFiledsMap = ClassUtils.getObjectFiledValue(args, argNames);

            String redisKey = "";

            if (BaseUtils.isNotBlank(keyParame)) {
                String[] keys = keyParame.split(",");

                for (String key : keys) {
                    if (BaseUtils.isNotBlank(key) && argAllFiledsMap.containsKey(key)) {
                        Object paramData = argAllFiledsMap.get(key);

                        /**
                         * 空值参数不处理
                         */
                        if (paramData == null || BaseUtils.isBlank(paramData.toString())) {
                            log.error("{} key标注的参数为空, 缓存不生效...", signature.getName());
                            return joinPoint.proceed(args);
                        }

                        redisKey = redisKey + paramData.toString() + ",";
                    }
                }
            } else {
                redisKey = group + ",";
            }


            if (BaseUtils.isNotBlank(redisKey)) {
                redisKey = redisKey.substring(0, redisKey.length() - 1);
            } else {
                log.error("{} key标注的参数为空,或最终的key长度过长,redis 缓存不生效...", signature.getName());
                return joinPoint.proceed(args);
            }
            String groupVision = group + "_version";
            if (redisQuery.isEnadbleL2Cache() && !CACHE.containsKey(group)) {
                CACHE.put(group, new HashMap<String, Object>());
                CACHE_VISON.put(group, new HashMap<String, String>());
            }

            /**
             * 开启二级缓存,查询本地
             */
            if (redisQuery.isEnadbleL2Cache() & CACHE.get(group).containsKey(redisKey)) {
                Object localData = CACHE.get(group).get(redisKey);

                /**
                 * 检查一下 reids 还有没有，没有的话 就去 去掉缓存,每3次请求校验一次
                 */
                String mqData = group + "," + redisKey;

                redisCacheListener.sendWithTopic(mqData);

                return localData;
            }

            /**
             * 查询redis
             */
            Object result = redisServiceInit.getRedisService().hget(group, redisKey);

            if (result != null) {
                if (redisQuery.isEnadbleL2Cache()) {
                    CACHE.get(group).put(redisKey, result);
                }

                return result;
            }



            /**
             * 查询方法
             */
            Object resultData = joinPoint.proceed(args);

            long localTime = System.currentTimeMillis();
            if (resultData != null) {
                redisServiceInit.getRedisService().hset(group, redisKey, resultData, timeOut);
                redisServiceInit.getRedisService().hset(groupVision, redisKey, String.valueOf(localTime), timeOut);

                /**
                 * 更新二级缓存
                 */
                if (redisQuery.isEnadbleL2Cache()) {
                    CACHE.get(group).put(redisKey, resultData);
                    CACHE_VISON.get(group).put(redisKey, String.valueOf(localTime));
                }
            } else {
                redisServiceInit.getRedisService().hset(group, redisKey, String.valueOf(localTime), 5);
                redisServiceInit.getRedisService().hset(groupVision, redisKey, String.valueOf(localTime), 5);
            }


            return resultData;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw throwable;
        }
    }


}
