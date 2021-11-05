package com.jtframework.datasource.mysql8_nosql;


import com.alibaba.fastjson.JSONObject;
import com.jtframework.utils.BaseUtils;
import com.mysql.cj.xdevapi.Client;
import com.mysql.cj.xdevapi.ClientFactory;
import com.mysql.cj.xdevapi.Schema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class Mysql8NoSqlFactoryConfig {

    /**
     * 获取mysql8 配置信息
     * @return
     */
    @Bean
    public Schema getSchema(@Value("${mysql8.host:}") String host,
                            @Value("${mysql8.prot:33060}")  Integer prot,
                            @Value("${mysql8.user:}")  String user,
                            @Value("${mysql8.password:}") String password,
                            @Value("${mysql8.db:}") String db,
                            @Value("${mysql8.maxSize:8}") Integer maxSize,
                            @Value("${mysql8.maxIdleTime:30000}") Integer maxIdleTime,
                            @Value("${mysql8.queueTimeout:30000}") Integer queueTimeout
                            ){
        if (BaseUtils.isBlank(host) || BaseUtils.isBlank(user) ||   BaseUtils.isBlank(password) ||  BaseUtils.isBlank(db)){
            log.warn("mysql8 未配置 ....");
            return null;
        }



        JSONObject poolConfig = new JSONObject();
        JSONObject pooling = new JSONObject();
        poolConfig.put("pooling",pooling);
        pooling.put("enabled",true);
        pooling.put("maxSize",maxSize);
        pooling.put("maxIdleTime",maxIdleTime);
        pooling.put("queueTimeout",queueTimeout);

        ClientFactory cf = new ClientFactory();

        String url = "mysqlx://"+host+":"+prot+"/"+db+"?user="+user+"&password="+password;
        Client cli = cf.getClient(url, poolConfig.toJSONString());

        log.warn("mysql8 启动:{},{}",url,poolConfig);
        return cli.getSession().getSchema(db);
    }


}
