package com.jtframework.datasource.mysql8_nosql;


import com.jtframework.utils.BaseUtils;
import com.mysql.cj.xdevapi.ClientFactory;
import com.mysql.cj.xdevapi.Schema;
import com.mysql.cj.xdevapi.Session;
import com.mysql.cj.xdevapi.SessionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class Mysql8NoSqlFactoryConfig {


    @Bean
    public Schema mySchema(Session session) {
        return session.getDefaultSchema();
    }

    /**
     * 获取mysql8 配置信息
     * @return
     */
    @Bean
    public Session mySession(@Value("${mysql8.host:}") String host,
                            @Value("${mysql8.prot:33060}")  Integer prot,
                            @Value("${mysql8.user:}")  String user,
                            @Value("${mysql8.password:}") String password,
                            @Value("${mysql8.db:}") String db
                            ){
        if (BaseUtils.isBlank(host) || BaseUtils.isBlank(user) ||   BaseUtils.isBlank(password) ||  BaseUtils.isBlank(db)){
            log.warn("mysql8 未配置 ....");
            return null;
        }

        ClientFactory cf = new ClientFactory();
        String url = "mysqlx://"+host+":"+prot+"/"+db+"?user="+user+"&password="+password;
        log.warn("mysql8 启动:{}",url);
        return new SessionFactory().getSession(url);
    }


}
