package com.jtframework.base.system;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author huanghong E-mail:767980702@qq.com
 * @version 创建时间：2017/12/25
 */
@Component
public class ApplicationContextProvider implements ApplicationContextAware {
    /**
     * 上下文对象实例
     */
    private static ApplicationContext applicationContext;

    /**
     * 通过name获取 Bean.
     *
     * @param name
     * @return
     */
    public static  <T> T getBean(String name) throws Exception {
        return (T)getApplicationContext().getBean(name);
    }

    /**
     * 获取applicationContext
     *
     * @return
     */
    public static ApplicationContext getApplicationContext() throws Exception {
        if (applicationContext == null ){
            throw new Exception("applicationContext 未初始化");
        }
        return applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextProvider.applicationContext = applicationContext;
    }


    /**
     * 通过class获取Bean.
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public  <T> T getBean(Class<T> clazz) throws Exception {
        return getApplicationContext().getBean(clazz);
    }

    /**
     * 通过class获取Bean.
     *
     * @param name
     * @return
     */
    public  boolean containsBean(String name) throws Exception {
        return getApplicationContext().containsBean(name);
    }

    /**
     * 通过name,以及Clazz返回指定的Bean
     *
     * @param name
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T getBean(String name, Class<T> clazz) throws Exception {
        return getApplicationContext().getBean(name, clazz);
    }
}