package com.jtframework.utils.system;

import com.jtframework.datasource.redis.RedisServiceInit;
import com.jtframework.utils.AnnotationScannerUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class UnAuthRestRegist implements InitializingBean {

    public static final String UNAUTH_URL_KEY = "unauth_urls";

    @Autowired
    private ApplicationContextProvider applicationContextProvider;

    @Autowired
    private RedisServiceInit redisServiceInit;


    public Set<String> getUnAuthUrls() throws Exception {
        return AnnotationScannerUtils.getAnnotationUrl(applicationContextProvider);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (redisServiceInit.getRedisService() != null) {
            List<String> data = new ArrayList<>(getUnAuthUrls());
            if (data.size() > 0){
                for (String datum : data) {
                    redisServiceInit.getRedisService().sSet(UnAuthRestRegist.UNAUTH_URL_KEY, datum);
                }
            }
        }
    }
}
