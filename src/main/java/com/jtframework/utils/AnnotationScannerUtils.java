package com.jtframework.utils;

import com.jtframework.base.rest.AnonymousAccess;
import com.jtframework.base.system.ApplicationContextProvider;
import com.jtframework.enums.RequestMethodEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.*;

@Slf4j
public class AnnotationScannerUtils {


    /**
     * 根据注解获取 service url
     * @param applicationContextProvider
     * @return
     * @throws Exception
     */
    public static Set<String> getAnnotationUrl(ApplicationContextProvider applicationContextProvider) throws Exception {
        // 搜寻匿名标记 url： @AnonymousAccess
        RequestMappingHandlerMapping requestMappingHandlerMapping = applicationContextProvider.getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);

        if (requestMappingHandlerMapping == null) {
            throw new Exception("未找到该bean");
        }

        Set<String> urls = new HashSet<>();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> infoEntry : requestMappingHandlerMapping.getHandlerMethods().entrySet()) {
            HandlerMethod handlerMethod = infoEntry.getValue();
            AnonymousAccess anonymousAccess = handlerMethod.getMethodAnnotation(AnonymousAccess.class);
            if (null != anonymousAccess) {
                List<RequestMethod> requestMethods = new ArrayList<>(infoEntry.getKey().getMethodsCondition().getMethods());
                RequestMethodEnum request = RequestMethodEnum.find(requestMethods.size() == 0 ? RequestMethodEnum.ALL.getType() : requestMethods.get(0).name());
                if (request != null) {
                    urls.addAll(infoEntry.getKey().getPatternsCondition().getPatterns());
                }
            }
        }
        return urls;
    }
}

