package com.jtframework.base.system;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author huanghong E-mail:767980702@qq.com
 * @version 创建时间：2017/12/25
 */
@Slf4j
@Component
public class ApplicationContextProvider implements ApplicationContextAware {
    /**
     * 上下文对象实例
     */
    private static ApplicationContext applicationContext;

    /**
     * bean 工厂实例
     */
    private DefaultListableBeanFactory defaultListableBeanFactory;

    /**
     *
     * @param name
     * @return
     */
    public static <T> T getBean(String name) throws Exception {
        return (T) getApplicationContext().getBean(name);
    }

    /**
     * 获取applicationContext
     *
     * @return
     */
    public static ApplicationContext getApplicationContext() throws Exception {
        if (applicationContext == null) {
            throw new Exception("applicationContext 未初始化");
        }
        return applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        ApplicationContextProvider.applicationContext = applicationContext;
        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;

        // 获取bean工厂并转换为DefaultListableBeanFactory
        this.defaultListableBeanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();

    }


    /**
     * 通过class获取Bean.
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T getBean(Class<T> clazz) throws Exception {
        return getApplicationContext().getBean(clazz);
    }

    /**
     * 通过class获取Bean.
     *
     * @param name
     * @return
     */
    public boolean containsBean(String name) throws Exception {
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

    /**
     * 注册bean到spring容器中
     *
     * @param beanName 名称
     * @param clazz    class
     */
    public void registerBean(String beanName, Class<?> clazz) {
        // 通过BeanDefinitionBuilder创建bean定义
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
        // 尝试移除之前相同的bean
        if (defaultListableBeanFactory.containsBean(beanName)) {
            defaultListableBeanFactory.removeBeanDefinition(beanName);
        }
        // 注册bean
        defaultListableBeanFactory
                .registerBeanDefinition(beanName, beanDefinitionBuilder.getRawBeanDefinition());
        log.info("register bean [{}],Class [{}] success.", beanName, clazz);
    }

    /**
     * 注册bean到spring容器中
     *
     * @param beanName 名称
     * @param object    class
     */
    public void registerBean(String beanName, Object object) {

        // 尝试移除之前相同的bean
        if (defaultListableBeanFactory.containsBean(beanName)) {
            defaultListableBeanFactory.removeBeanDefinition(beanName);
        }
        // 注册bean
        defaultListableBeanFactory
                .registerSingleton(beanName, object);
        log.info("register bean [{}],Class [{}] success.", beanName, object);
    }
}