package com.jtframework.utils;

import com.jtframework.base.auth.AuthAccess;
import com.jtframework.base.auth.SysAuth;
import com.jtframework.base.dao.ServerModel;
import com.jtframework.base.auth.AnonymousAccess;
import com.jtframework.utils.system.ApplicationContextProvider;
import com.jtframework.enums.RequestMethodEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.*;

@Slf4j
public class AnnotationUtils {


    /**
     * 根据注解获取 AnonymousAccess url
     *
     * @param applicationContextProvider
     * @return
     * @throws Exception
     */
    public static Map<String,String> getAnonymousAccessUrl(ApplicationContextProvider applicationContextProvider) throws Exception {
        // 搜寻匿名标记 url： @AnonymousAccess
        RequestMappingHandlerMapping requestMappingHandlerMapping = applicationContextProvider.getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);

        if (requestMappingHandlerMapping == null) {
            throw new Exception("未找到该bean");
        }

        Map<String,String> urls = new HashMap<>();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> infoEntry : requestMappingHandlerMapping.getHandlerMethods().entrySet()) {
            HandlerMethod handlerMethod = infoEntry.getValue();
            AnonymousAccess anonymousAccess = handlerMethod.getMethodAnnotation(AnonymousAccess.class);
            if (null != anonymousAccess) {
                List<RequestMethod> requestMethods = new ArrayList<>(infoEntry.getKey().getMethodsCondition().getMethods());
                RequestMethodEnum request = RequestMethodEnum.find(requestMethods.size() == 0 ? RequestMethodEnum.ALL.getType() : requestMethods.get(0).name());
                if (request != null) {

                    infoEntry.getKey().getPatternsCondition().getPatterns().stream().forEach(url ->{
                        urls.put(url,anonymousAccess.serviceName());
                    });
                }
            }
        }
        return urls;
    }

    /**
     * 根据注解获取 SysAuth 值
     *
     * @param applicationContextProvider
     * @return
     * @throws Exception
     */
    public static Map<String, String> getSysAuth(ApplicationContextProvider applicationContextProvider) throws Exception {
        // 搜寻匿名标记 url： @AnonymousAccess
        RequestMappingHandlerMapping requestMappingHandlerMapping = applicationContextProvider.getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);

        if (requestMappingHandlerMapping == null) {
            throw new Exception("未找到该bean");
        }

        Map<String, String> result = new HashMap<>();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> infoEntry : requestMappingHandlerMapping.getHandlerMethods().entrySet()) {
            HandlerMethod handlerMethod = infoEntry.getValue();

            SysAuth sysAuth = handlerMethod.getMethodAnnotation(SysAuth.class);
            if (null != sysAuth) {
                result.put(sysAuth.name(), sysAuth.type().name() + "&&" + sysAuth.urlpath());
            }
        }

        return result;
    }



    /**
     * 根据注解获取 SysAuth 值
     *
     * @param applicationContextProvider
     * @return
     * @throws Exception
     */
    public static Map<String, String> getSysAuthAccess(ApplicationContextProvider applicationContextProvider) throws Exception {
        // 搜寻匿名标记 url： @AnonymousAccess
        RequestMappingHandlerMapping requestMappingHandlerMapping = applicationContextProvider.getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);

        if (requestMappingHandlerMapping == null) {
            throw new Exception("未找到该bean");
        }

        Map<String, String> result = new HashMap<>();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> infoEntry : requestMappingHandlerMapping.getHandlerMethods().entrySet()) {
            HandlerMethod handlerMethod = infoEntry.getValue();

            AuthAccess authAccess = handlerMethod.getMethodAnnotation(AuthAccess.class);
            if (null != authAccess) {
                List<RequestMethod> requestMethods = new ArrayList<>(infoEntry.getKey().getMethodsCondition().getMethods());
                RequestMethodEnum request = RequestMethodEnum.find(requestMethods.size() == 0 ? RequestMethodEnum.ALL.getType() : requestMethods.get(0).name());
                if (request != null) {

                    infoEntry.getKey().getPatternsCondition().getPatterns().stream().forEach(url ->{
                        result.put(url,authAccess.serviceName());
                    });
                }
            }
        }

        return result;
    }

    /**
     * 获取类的ServerModel 值
     *
     * @param cls
     * @return
     */
    public static String getServeModelValue(Class cls) {
        ServerModel serverModel = (ServerModel) cls.getAnnotation(ServerModel.class);
        return serverModel != null ? serverModel.value() : cls.getName();
    }

    /**
     * 获取类的ServerModel 描述
     *
     * @param cls
     * @return
     */
    public static String getServeModelDesc(Class cls) {
        ServerModel serverModel = (ServerModel) cls.getAnnotation(ServerModel.class);
        return serverModel != null ? serverModel.desc() : cls.getName();
    }

    /**
     * 获取类的ServerModel 描述
     *
     * @param cls
     * @return
     */
    public static String getSysAuthType(Class cls) {
        SysAuth sysAuth = (SysAuth) cls.getAnnotation(SysAuth.class);
        return sysAuth != null ? sysAuth.type().getDesc() : null;
    }


    /**
     * 获取类的ServerModel 描述
     *
     * @param cls
     * @return
     */
    public static String getSysAuthName(Class cls) {
        SysAuth sysAuth = (SysAuth) cls.getAnnotation(SysAuth.class);
        return sysAuth != null ? sysAuth.name() : null;
    }

}

