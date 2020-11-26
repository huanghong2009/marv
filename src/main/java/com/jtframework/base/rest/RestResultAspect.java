//package com.jtframework.base.rest;
//
//
//import com.jtframework.utils.BaseUtils;
//import lombok.extern.slf4j.Slf4j;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.Signature;
//import org.aspectj.lang.annotation.AfterReturning;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.springframework.stereotype.Component;
//
//@Component
//@Aspect
//@Slf4j
//public class RestResultAspect {
//    public RestResultAspect(){
//        log.info("初始化 RestResultAspect 注解");
//    }
//    /**
//     * 方法用途（切入点表达式可以用&&,||,!来组合使用）:
//     * execution: 匹配连接点：execution(* com.example.demo.*(..))--com.example.demo包下所有类的方法
//     * within: 某个类里面
//     * this: 指定AOP代理类的类型
//     * target:指定目标对象的类型
//     * args: 指定参数的类型
//     * bean:指定特定的bean名称，可以使用通配符（Spring自带的）
//     *
//     * @target： 带有指定注解的类型
//     * @args: 指定运行时传的参数带有指定的注解
//     * @within: 匹配使用指定注解的类
//     * @annotation:指定方法所应用的注解
//     */
//    @Pointcut("@annotation(com.jtframework.base.rest.RestResult)")
//    public void asAnnotation() {
//    }
//
//
//    /**
//     * 方法用途:
//     *
//     * @Around 环绕增强，相当于MethodInterceptor，对带@AnnotationDemo注解的方法进行切面，并获取到注解的属性值
//     * ProceedingJoinPoint: 环绕通知
//     * <br/>
//     * 操作步骤: TODO<br/>
//     * ${tags}
//     */
//    @Around("asAnnotation() && @annotation(restResult)")
//    public Object around(ProceedingJoinPoint joinPoint, RestResult restResult) {
//        ServerResponse serverResponse = new ServerResponse();
//        try {
////            // AnnotationDemo注解的属性值
////            restResult.errorMessage();
//            Signature signature = joinPoint.getSignature();
//            Object[] args = joinPoint.getArgs();
//            log.info("正在调用 {} 下的  {} 请求参数是:{} ", signature.getClass().getName(), signature.getName(), args);
//            Object result = joinPoint.proceed(args);
//
//            serverResponse.setData(result);
//            serverResponse.setState(ServerResponse.State.SUCCEED.name());
//            serverResponse.setMsg(BaseUtils.isBlank(restResult.succedMessage()) ? "操作成功" : restResult.succedMessage());
//            return serverResponse;
//        } catch (Throwable throwable) {
//            throwable.printStackTrace();
//            log.info("{} - -{} 执行出错", throwable.getMessage());
//            serverResponse.setState(ServerResponse.State.ERROR.name());
//            serverResponse.setMsg(BaseUtils.isBlank(restResult.errorMessage()) ? "请求失败" : restResult.errorMessage());
//            return  serverResponse;
//        }
//    }
//
//
//
//
//}
