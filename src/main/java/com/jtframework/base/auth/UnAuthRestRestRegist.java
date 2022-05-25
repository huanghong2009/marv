package com.jtframework.base.auth;

import com.jtframework.datasource.redis.RedisServiceInit;
import com.jtframework.utils.AnnotationUtils;
import com.jtframework.utils.system.ApplicationContextProvider;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class UnAuthRestRestRegist implements InitializingBean {
    public static final String UNAUTH_URL_KEY = "sys_unauth_urls_";
    public static final String UNAUTH_SERVICE_KEY = "sys_unauth_service";
    /**
     * 当前环境
     */
    @Value("${spring.application.name}")
    public String serverName;

    @Autowired
    private ApplicationContextProvider applicationContextProvider;

    @Autowired
    private RedisServiceInit redisServiceInit;


    public Map<String,String> getUnAuthUrls() throws Exception {
        return AnnotationUtils.getAnonymousAccessUrl(applicationContextProvider);
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        if (redisServiceInit.getRedisService() != null) {
            String key = UnAuthRestRestRegist.UNAUTH_URL_KEY+serverName;

            redisServiceInit.getRedisService().sSet(UNAUTH_SERVICE_KEY,serverName);


            redisServiceInit.getRedisService().del(key);
            Map<String, String> uauthUrls = getUnAuthUrls();
            for (String mKey : uauthUrls.keySet()) {
                redisServiceInit.getRedisService().hset(key, mKey,uauthUrls.get(mKey));
            }
        }
    }
}
