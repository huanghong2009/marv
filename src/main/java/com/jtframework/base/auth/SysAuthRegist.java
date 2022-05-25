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

@Component
public class SysAuthRegist implements InitializingBean {


    public static final String SYS_AUTH_KEY = "sys_auth_key_";

    public static final String UNAUTH_SERVICE_KEY = "sys_auth_service";
    /**
     * 当前环境
     */
    @Value("${spring.application.name}")
    public String serverName;

    @Autowired
    private ApplicationContextProvider applicationContextProvider;

    @Autowired
    private RedisServiceInit redisServiceInit;

    /**
     * 根据服务获取 注册的sysauth
     *
     * @param serverName
     * @return
     */
    public List<String> getSysAuthInfosByServerName(String serverName) {
        List<String> list = new ArrayList<>();
        if (redisServiceInit.getRedisService() != null) {
            Map<Object, Object> datas = redisServiceInit.getRedisService().hmget(SysAuthRegist.SYS_AUTH_KEY + serverName);
            if (datas != null) {
                datas.keySet().forEach(key -> list.add(key + "&&" + datas.get(key).toString()));
            }
        }
        return list;
    }


    public Map<String, String> getUnAuthUrls() throws Exception {
        return AnnotationUtils.getSysAuth(applicationContextProvider);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (redisServiceInit.getRedisService() != null) {
            String hkey = SysAuthRegist.SYS_AUTH_KEY + serverName;
            redisServiceInit.getRedisService().del(hkey);
            redisServiceInit.getRedisService().sSet(UNAUTH_SERVICE_KEY,serverName);
            Map<String, String> datas = getUnAuthUrls();

            datas.keySet().stream().forEach(key -> {
                redisServiceInit.getRedisService().hset(hkey, key, datas.get(key));
            });
        }
    }
}
