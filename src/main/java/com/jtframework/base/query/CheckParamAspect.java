package com.jtframework.base.query;


import cn.hutool.core.util.ReflectUtil;
import com.jtframework.base.exception.BusinessException;
import com.jtframework.utils.BaseUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Component
@Aspect
@Slf4j
public class CheckParamAspect {
    public CheckParamAspect() {
        log.info("初始化 CheckParamAspect 注解");
    }

    /**
     * 方法用途（切入点表达式可以用&&,||,!来组合使用）:
     * execution: 匹配连接点：execution(* com.example.demo.*(..))--com.example.demo包下所有类的方法
     * within: 某个类里面
     * this: 指定AOP代理类的类型
     * target:指定目标对象的类型
     * args: 指定参数的类型
     * bean:指定特定的bean名称，可以使用通配符（Spring自带的）
     *
     * @target： 带有指定注解的类型
     * @args: 指定运行时传的参数带有指定的注解
     * @within: 匹配使用指定注解的类
     * @annotation:指定方法所应用的注解
     */
    @Pointcut("@annotation(com.jtframework.base.query.CheckParam)")
    public void asAnnotation() {
    }


    /**
     * 方法用途: 检查参数，最多支持 2层嵌套
     *
     * @Around 环绕增强，相当于MethodInterceptor，对带@AnnotationDemo注解的方法进行切面，并获取到注解的属性值
     * ProceedingJoinPoint: 环绕通知
     * <br/>
     * 操作步骤: TODO<br/>
     * ${tags}
     */
    @Before("asAnnotation() && @annotation(checkParam)")
    public void before(final JoinPoint joinPoint, CheckParam checkParam) throws BusinessException {
        Object[] args = joinPoint.getArgs();
        String[] argNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();

        if (args.length > 0) {
            if (argNames == null) {
                log.error("--- 未编译参数名称,跳过参数检查过程...");
            } else {
                Set<String> checkMap;
                if (BaseUtils.isNotBlank(checkParam.value())) {
                    String[] checks = checkParam.value().split(",");
                    checkMap = new HashSet<>(Arrays.asList(checks));
                } else {
                    checkMap = new HashSet<>();
                }

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

                /**
                 * 判断对象符合不符合规范
                 */
                for (String key : argAllFiledsMap.keySet()) {
                    boolean flag = true;
                    if (checkParam.checkType().equals(CheckParam.Type.EXCLUDE)) {
                        if (checkMap.contains(key)) {
                            flag = false;
                        }
                    } else if (checkParam.checkType().equals(CheckParam.Type.ONLY)) {
                        if (!checkMap.contains(key)) {
                            flag = false;
                        }
                    }

                    if (flag) {
                        Object value = argAllFiledsMap.get(key);
                        if (null == value) {
                            throw new BusinessException("请检查参数");
                        }

                        if (value instanceof String) {
                            if (BaseUtils.isBlank(String.valueOf(value))) {
                                throw new BusinessException("请检查参数");
                            }
                        }
                    }
                }


            }


        }
    }
}
