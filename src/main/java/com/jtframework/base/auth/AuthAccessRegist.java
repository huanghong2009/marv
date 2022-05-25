package com.jtframework.base.auth;

import com.jtframework.datasource.redis.RedisServiceInit;
import com.jtframework.utils.AnnotationUtils;
import com.jtframework.utils.system.ApplicationContextProvider;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AuthAccessRegist implements InitializingBean {
    public static final String AUTH_ACCESS_KEY = "sys_access_";
    public static final String AUTH_ACCESS_LIST_KEY = "sys_access_list";
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
        return AnnotationUtils.getSysAuthAccess(applicationContextProvider);
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        if (redisServiceInit.getRedisService() != null) {
            String key = AuthAccessRegist.AUTH_ACCESS_KEY+serverName;

            redisServiceInit.getRedisService().sSet(AUTH_ACCESS_LIST_KEY,serverName);


            redisServiceInit.getRedisService().del(key);
            Map<String, String> uauthUrls = getUnAuthUrls();
            for (String mKey : uauthUrls.keySet()) {
                redisServiceInit.getRedisService().hset(key, mKey,uauthUrls.get(mKey));
            }
        }
    }
}
